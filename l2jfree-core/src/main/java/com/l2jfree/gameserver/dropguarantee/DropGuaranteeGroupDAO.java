package com.l2jfree.gameserver.dropguarantee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import com.l2jfree.L2DatabaseFactory;

/**
 * DAO для работы с групповой гарантией - новая система для предметов с низким шансом.
 */
public final class DropGuaranteeGroupDAO {
    
    private DropGuaranteeGroupDAO() {}
    
    /**
     * Данные о состоянии группы гарантии для игрока
     */
    public static class GroupState {
        public final long groupSumPpm;
        public final Set<Integer> groupItems;     // Полный список предметов в группе
        public final Set<Integer> excludedItems;  // Исключенные предметы в текущем цикле
        public final int cycleId;
        
        public GroupState(long groupSumPpm, Set<Integer> groupItems, Set<Integer> excludedItems, int cycleId) {
            this.groupSumPpm = groupSumPpm;
            this.groupItems = groupItems != null ? groupItems : new HashSet<>();
            this.excludedItems = excludedItems != null ? excludedItems : new HashSet<>();
            this.cycleId = cycleId;
        }
    }
    
    /**
     * Получить текущее состояние группы для игрока
     */
    public static GroupState getGroupState(int charId, int npcId, int l2Category, int pityCategory, DropGuaranteeDAO.Source source) throws SQLException {
        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(
                 "SELECT group_sum_ppm, group_items, excluded_items, cycle_id FROM pity_group_progress " +
                 "WHERE char_id = ? AND npc_id = ? AND l2_category = ? AND pity_category = ? AND source = ?")) {
            ps.setInt(1, charId);
            ps.setInt(2, npcId);
            ps.setInt(3, l2Category);
            ps.setInt(4, pityCategory);
            ps.setString(5, source.name());
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    long groupSum = rs.getLong("group_sum_ppm");
                    String groupItemsStr = rs.getString("group_items");
                    String excludedStr = rs.getString("excluded_items");
                    int cycleId = rs.getInt("cycle_id");
                    
                    Set<Integer> groupItems = parseExcludedItems(groupItemsStr);  // Используем ту же функцию парсинга
                    Set<Integer> excluded = parseExcludedItems(excludedStr);
                    return new GroupState(groupSum, groupItems, excluded, cycleId);
                } else {
                    return new GroupState(0L, new HashSet<>(), new HashSet<>(), 1);
                }
            }
        }
    }
    
    /**
     * Добавить прогресс к группе и вернуть новое состояние
     */
    public static GroupState addGroupProgress(int charId, int npcId, int l2Category, int pityCategory, DropGuaranteeDAO.Source source, 
                                            long deltaPpm, Set<Integer> currentGroupItems, Set<Integer> currentExcluded) throws SQLException {
        try (Connection con = L2DatabaseFactory.getInstance().getConnection()) {
            con.setAutoCommit(false);
            try {
                // Получаем текущее состояние с блокировкой
                GroupState current;
                try (PreparedStatement ps = con.prepareStatement(
                    "SELECT group_sum_ppm, group_items, excluded_items, cycle_id FROM pity_group_progress " +
                    "WHERE char_id = ? AND npc_id = ? AND l2_category = ? AND pity_category = ? AND source = ? FOR UPDATE")) {
                    ps.setInt(1, charId);
                    ps.setInt(2, npcId);
                    ps.setInt(3, l2Category);
                    ps.setInt(4, pityCategory);
                    ps.setString(5, source.name());
                    
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            long groupSum = rs.getLong("group_sum_ppm");
                            String groupItemsStr = rs.getString("group_items");
                            String excludedStr = rs.getString("excluded_items");
                            int cycleId = rs.getInt("cycle_id");
                            Set<Integer> groupItems = parseExcludedItems(groupItemsStr);
                            Set<Integer> excluded = parseExcludedItems(excludedStr);
                            current = new GroupState(groupSum, groupItems, excluded, cycleId);
                        } else {
                            current = new GroupState(0L, new HashSet<>(), new HashSet<>(), 1);
                        }
                    }
                }
                
                // Проверяем консистентность группы
                if (current.groupItems.isEmpty()) {
                    // Первый раз для этой группы - инициализируем список предметов
                    current = new GroupState(current.groupSumPpm, new HashSet<>(currentGroupItems), current.excludedItems, current.cycleId);
                } else if (!current.groupItems.equals(currentGroupItems)) {
                    // Состав группы изменился - сбрасываем прогресс
                    current = new GroupState(0L, new HashSet<>(currentGroupItems), new HashSet<>(), current.cycleId + 1);
                }
                
                // Обновляем состояние
                long newSum = current.groupSumPpm + deltaPpm;
                Set<Integer> newGroupItems = new HashSet<>(currentGroupItems);
                Set<Integer> newExcluded = new HashSet<>(current.excludedItems);
                newExcluded.addAll(currentExcluded);
                
                // Сохраняем в базу
                try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO pity_group_progress (char_id, npc_id, l2_category, pity_category, source, group_sum_ppm, group_items, excluded_items, cycle_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE " +
                    "group_sum_ppm = ?, group_items = ?, excluded_items = ?, updated_at = NOW()")) {
                    
                    String groupItemsStr = serializeExcludedItems(newGroupItems);
                    String excludedStr = serializeExcludedItems(newExcluded);
                    
                    ps.setInt(1, charId);
                    ps.setInt(2, npcId);
                    ps.setInt(3, l2Category);
                    ps.setInt(4, pityCategory);
                    ps.setString(5, source.name());
                    ps.setLong(6, newSum);
                    ps.setString(7, groupItemsStr);
                    ps.setString(8, excludedStr);
                    ps.setInt(9, current.cycleId);
                    ps.setLong(10, newSum);
                    ps.setString(11, groupItemsStr);
                    ps.setString(12, excludedStr);
                    ps.executeUpdate();
                }
                
                con.commit();
                return new GroupState(newSum, newGroupItems, newExcluded, current.cycleId);
                
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }
    
    /**
     * Добавить выданный предмет к исключенным
     */
    public static void addExcludedItem(int charId, int npcId, int l2Category, int pityCategory, DropGuaranteeDAO.Source source, int itemId) throws SQLException {
        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(
                 "UPDATE pity_group_progress SET " +
                 "excluded_items = CASE " +
                 "  WHEN excluded_items IS NULL OR excluded_items = '' THEN ? " +
                 "  ELSE CONCAT(excluded_items, ',', ?) " +
                 "END, " +
                 "updated_at = NOW() " +
                 "WHERE char_id = ? AND npc_id = ? AND l2_category = ? AND pity_category = ? AND source = ?")) {
            
            ps.setString(1, String.valueOf(itemId));
            ps.setString(2, String.valueOf(itemId));
            ps.setInt(3, charId);
            ps.setInt(4, npcId);
            ps.setInt(5, l2Category);
            ps.setInt(6, pityCategory);
            ps.setString(7, source.name());
            ps.executeUpdate();
        }
    }
    
    /**
     * Сбросить группу (новый цикл) - очистить исключения и прогресс
     */
    public static void resetGroup(int charId, int npcId, int l2Category, int pityCategory, DropGuaranteeDAO.Source source) throws SQLException {
        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(
                 "UPDATE pity_group_progress SET " +
                 "group_sum_ppm = 0, excluded_items = NULL, cycle_id = cycle_id + 1, updated_at = NOW() " +
                 "WHERE char_id = ? AND npc_id = ? AND l2_category = ? AND pity_category = ? AND source = ?")) {
            ps.setInt(1, charId);
            ps.setInt(2, npcId);
            ps.setInt(3, l2Category);
            ps.setInt(4, pityCategory);
            ps.setString(5, source.name());
            ps.executeUpdate();
        }
    }
    
    /**
     * Сбросить только прогресс группы (после выдачи предмета)
     */
    public static void resetGroupProgress(int charId, int npcId, int l2Category, int pityCategory, DropGuaranteeDAO.Source source) throws SQLException {
        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(
                 "UPDATE pity_group_progress SET group_sum_ppm = 0, updated_at = NOW() " +
                 "WHERE char_id = ? AND npc_id = ? AND l2_category = ? AND pity_category = ? AND source = ?")) {
            ps.setInt(1, charId);
            ps.setInt(2, npcId);
            ps.setInt(3, l2Category);
            ps.setInt(4, pityCategory);
            ps.setString(5, source.name());
            ps.executeUpdate();
        }
    }
    
    // Вспомогательные методы для работы с excluded_items
    private static Set<Integer> parseExcludedItems(String excludedStr) {
        Set<Integer> result = new HashSet<>();
        if (excludedStr != null && !excludedStr.trim().isEmpty()) {
            String[] items = excludedStr.split(",");
            for (String item : items) {
                try {
                    result.add(Integer.parseInt(item.trim()));
                } catch (NumberFormatException e) {
                    // Игнорируем некорректные записи
                }
            }
        }
        return result;
    }
    
    private static String serializeExcludedItems(Set<Integer> excluded) {
        if (excluded == null || excluded.isEmpty()) {
            return null;
        }
        
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Integer itemId : excluded) {
            if (!first) sb.append(",");
            sb.append(itemId);
            first = false;
        }
        return sb.toString();
    }
}