package com.l2jfree.gameserver.dropguarantee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.l2jfree.L2DatabaseFactory;

/**
 * Доступ к таблице pity_item_counter для механики «гарантированного дропа».
 * Хранит счётчик в «частях на миллион» (1.0 = 1_000_000).
 */
public final class DropGuaranteeDAO {
    public enum Source { DROP, SPOIL }

    /** Единица счётчика (1.0) в частях на миллион. */
    public static final long ONE_PPM_UNIT = 1_000_000L;

    private DropGuaranteeDAO() { }

    /**
     * Нарастить счётчик на deltaPpm и вернуть его текущее значение после наращивания.
     * Если записи ещё нет, будет создана со значением deltaPpm.
     */
    public static long addAndGetMeterPpm(int charId, int itemId, int category, Source source, long deltaPpm) throws SQLException {
        try (Connection con = L2DatabaseFactory.getInstance().getConnection()) {
            con.setAutoCommit(false);
            try {
                upsertAdd(con, charId, itemId, category, source, deltaPpm);
                long val = selectMeterForUpdate(con, charId, itemId, source);
                con.commit();
                return val;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    /**
     * Обнулить счётчик после фактической выдачи предмета игроку.
     */
    public static void resetMeter(int charId, int itemId, Source source) throws SQLException {
        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(
                 "UPDATE pity_item_counter " +
                 "SET meter_ppm = 0, updated_at = NOW() " +
                 "WHERE char_id = ? AND item_id = ? AND source = ?")) {
            ps.setInt(1, charId);
            ps.setInt(2, itemId);
            ps.setString(3, source.name());
            ps.executeUpdate();
        }
    }

    /**
     * Получить текущее значение счетчика для игрока и предмета.
     * Возвращает 0, если записи нет.
     */
    public static long getCurrentMeter(int charId, int itemId, Source source) throws SQLException {
        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(
                 "SELECT meter_ppm FROM pity_item_counter " +
                 "WHERE char_id = ? AND item_id = ? AND source = ?")) {
            ps.setInt(1, charId);
            ps.setInt(2, itemId);
            ps.setString(3, source.name());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getLong(1) : 0L;
            }
        }
    }

    // -------------------- Внутренние операции --------------------

    private static void upsertAdd(Connection con, int charId, int itemId, int category, Source source, long deltaPpm) throws SQLException {
        final long cap = 20L * ONE_PPM_UNIT;

        // ── НОРМАЛИЗУЕМ КАТЕГОРИЮ ───────────────────────────────────────────────
        int normCategory = category;
        if (source == Source.DROP && normCategory < 0) // в DROP категорией -1 быть не должно
            normCategory = 0; // или 2/3 — на твой выбор для аналитики
        // ────────────────────────────────────────────────────────────────────────

        try (PreparedStatement ps = con.prepareStatement(
            "INSERT INTO pity_item_counter (char_id, item_id, category, source, meter_ppm) " +
            "VALUES (?, ?, ?, ?, ?) " +
            "ON DUPLICATE KEY UPDATE " +
            "  category = VALUES(category), " +
            "  meter_ppm = LEAST(meter_ppm + VALUES(meter_ppm), ?), " +
            "  updated_at = NOW()")) {
            ps.setInt(1, charId);
            ps.setInt(2, itemId);
            ps.setInt(3, normCategory); // <── вот здесь
            ps.setString(4, source.name());
            ps.setLong(5, deltaPpm);
            ps.setLong(6, cap);
            ps.executeUpdate();
        }
    }

    private static long selectMeterForUpdate(Connection con, int charId, int itemId, Source source) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
            "SELECT meter_ppm FROM pity_item_counter " +
            "WHERE char_id = ? AND item_id = ? AND source = ? FOR UPDATE")) {
            ps.setInt(1, charId);
            ps.setInt(2, itemId);
            ps.setString(3, source.name());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getLong(1) : 0L;
            }
        }
    }
}
