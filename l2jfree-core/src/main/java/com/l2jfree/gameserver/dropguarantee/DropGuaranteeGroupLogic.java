package com.l2jfree.gameserver.dropguarantee;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.l2jfree.gameserver.model.drop.L2DropCategory;
import com.l2jfree.gameserver.model.drop.L2DropData;
import com.l2jfree.tools.random.Rnd;

/**
 * Логика групповой гарантии для предметов с низким шансом.
 * Предметы с шансом меньше порога объединяются в группу и накапливают общий прогресс.
 */
public final class DropGuaranteeGroupLogic {
    
    private DropGuaranteeGroupLogic() {}
    
    /** Порог для участия в групповой гарантии: 2% в PPM */
    public static final long GROUP_THRESHOLD_PPM = 20_000L; // 2% = 20,000 PPM
    
    /** Базовое значение для расчетных категорий (чтобы не пересекаться с L2 категориями 1,2,3) */
    public static final int CALCULATED_CATEGORY_BASE = 1000;
    
    /**
     * Вычислить категорию по шансу предмета для группировки в гарантийной системе.
     * Предметы с похожими шансами попадают в одну категорию.
     * 
     * @param chancePpm шанс предмета в PPM
     * @return номер категории для группировки
     */
    public static int calculatePityCategory(long chancePpm) {
        if (chancePpm >= GROUP_THRESHOLD_PPM) {
            // Предметы с высоким шансом не участвуют в групповой гарантии
            return CALCULATED_CATEGORY_BASE + 999;
        }
        
        // Логарифмическое разбиение на интервалы
        if (chancePpm < 100) return CALCULATED_CATEGORY_BASE + 1;      // 0.0001% - 0.0099%
        if (chancePpm < 300) return CALCULATED_CATEGORY_BASE + 2;      // 0.01% - 0.0299%
        if (chancePpm < 500) return CALCULATED_CATEGORY_BASE + 3;      // 0.03% - 0.0499%
        if (chancePpm < 1000) return CALCULATED_CATEGORY_BASE + 4;     // 0.05% - 0.0999%
        if (chancePpm < 2000) return CALCULATED_CATEGORY_BASE + 5;     // 0.1% - 0.1999%
        if (chancePpm < 5000) return CALCULATED_CATEGORY_BASE + 6;     // 0.2% - 0.4999%
        if (chancePpm < 10000) return CALCULATED_CATEGORY_BASE + 7;    // 0.5% - 0.9999%
        return CALCULATED_CATEGORY_BASE + 8;                           // 1% - 1.9999%
    }
    
    /**
     * Класс для хранения информации о предмете и его шансе
     */
    public static class ItemWithChance {
        public final L2DropData dropData;
        public final long chancePpm;
        
        public ItemWithChance(L2DropData dropData, long chancePpm) {
            this.dropData = dropData;
            this.chancePpm = chancePpm;
        }
    }
    
    /**
     * Результат обработки групповой гарантии
     */
    public static class GroupResult {
        public final boolean groupTriggered; // Сработала ли групповая гарантия
        public final L2DropData guaranteedItem; // Выбранный предмет (если гарантия сработала)
        
        public GroupResult(boolean groupTriggered, L2DropData guaranteedItem) {
            this.groupTriggered = groupTriggered;
            this.guaranteedItem = guaranteedItem;
        }
    }
    
    /**
     * Определить, участвует ли категория в новой системе групповой гарантии
     */
    public static boolean isGroupEligible(int category, DropGuaranteeDAO.Source source) {
        // Все категории кроме 0 (адены) могут участвовать
        if (source == DropGuaranteeDAO.Source.DROP) {
            return category != 0;
        } else if (source == DropGuaranteeDAO.Source.SPOIL) {
            return true; // Все спойл-предметы участвуют
        }
        return false;
    }
    
    /**
     * Интерфейс для расчета шанса предмета
     */
    public interface ChanceCalculator {
        long calculateChance(L2DropData drop);
    }
    
    /**
     * Сгруппировать предметы категории по расчетным интервалам вероятности
     * @return Map где ключ - расчетная категория, значение - список предметов в этой группе
     */
    public static java.util.Map<Integer, List<ItemWithChance>> groupItemsByPityCategory(
            L2DropCategory category, ChanceCalculator chanceCalculator) {
        
        java.util.Map<Integer, List<ItemWithChance>> groups = new java.util.HashMap<>();
        
        for (L2DropData drop : category.getAllDrops()) {
            long chancePpm = chanceCalculator.calculateChance(drop);
            int pityCategory = calculatePityCategory(chancePpm);
            
            // Добавляем предмет в соответствующую группу
            groups.computeIfAbsent(pityCategory, k -> new java.util.ArrayList<>())
                  .add(new ItemWithChance(drop, chancePpm));
        }
        
        return groups;
    }
    
    /**
     * Обработать группу предметов с низким шансом
     * @param l2Category исходная L2 категория (1, 2, 3)
     * @param pityCategory расчетная категория по интервалам вероятности (1001, 1002, ...)
     * @return результат обработки (сработала ли гарантия и какой предмет выбран)
     */
    public static GroupResult processLowChanceGroup(int charId, int npcId, int l2Category, int pityCategory, DropGuaranteeDAO.Source source,
                                                   List<ItemWithChance> lowChanceItems) throws SQLException {
        
        if (lowChanceItems.isEmpty()) {
            return new GroupResult(false, null);
        }
        
        // Получаем текущее состояние группы
        DropGuaranteeGroupDAO.GroupState state = DropGuaranteeGroupDAO.getGroupState(charId, npcId, l2Category, pityCategory, source);
        
        // Фильтруем предметы - исключаем уже выданные в текущем цикле
        List<ItemWithChance> availableItems = new ArrayList<>();
        long maxChancePpm = 0L;
        
        for (ItemWithChance item : lowChanceItems) {
            if (!state.excludedItems.contains(item.dropData.getItemId())) {
                availableItems.add(item);
                // Берем максимальный шанс из группы для накопления гарантии
                // (чтобы не увеличивать общий дроп, а только обеспечить гарантию самого редкого предмета)
                if (item.chancePpm > maxChancePpm) {
                    maxChancePpm = item.chancePpm;
                }
            }
        }
        
        // Если все предметы группы уже выданы - начинаем новый цикл
        if (availableItems.isEmpty()) {
            DropGuaranteeGroupDAO.resetGroup(charId, npcId, l2Category, pityCategory, source);
            // На этом убийстве мы не накапливаем прогресс - новый цикл начнется со следующего убийства
            return new GroupResult(false, null);
        }
        
        // Добавляем прогресс группы
        Set<Integer> currentGroupItems = new HashSet<>();
        for (ItemWithChance item : lowChanceItems) {
            currentGroupItems.add(item.dropData.getItemId());
        }
        
        DropGuaranteeGroupDAO.GroupState newState = DropGuaranteeGroupDAO.addGroupProgress(
            charId, npcId, l2Category, pityCategory, source, maxChancePpm, currentGroupItems, new HashSet<>());
        
        // Проверяем, достигнут ли порог гарантии
        if (newState.groupSumPpm >= DropGuaranteeDAO.ONE_PPM_UNIT) {
            // Выбираем случайный предмет из доступных
            L2DropData selectedItem = selectRandomItem(availableItems);
            
            // Сбрасываем прогресс группы и добавляем предмет к исключенным
            DropGuaranteeGroupDAO.resetGroupProgress(charId, npcId, l2Category, pityCategory, source);
            DropGuaranteeGroupDAO.addExcludedItem(charId, npcId, l2Category, pityCategory, source, selectedItem.getItemId());
            
            // Проверяем, не исчерпались ли все предметы в группе
            Set<Integer> currentExcludedItems = DropGuaranteeGroupDAO.getGroupState(charId, npcId, l2Category, pityCategory, source).excludedItems;
            if (currentExcludedItems.size() >= lowChanceItems.size()) {
                // Все предметы группы выданы - сбрасываем группу для начала нового цикла
                DropGuaranteeGroupDAO.resetGroup(charId, npcId, l2Category, pityCategory, source);
            }
            
            return new GroupResult(true, selectedItem);
        }
        
        return new GroupResult(false, null);
    }
    
    /**
     * Выбрать случайный предмет из списка (с учетом весов шансов)
     */
    private static L2DropData selectRandomItem(List<ItemWithChance> items) {
        if (items.isEmpty()) {
            return null;
        }
        
        if (items.size() == 1) {
            return items.get(0).dropData;
        }
        
        // Вычисляем общий вес
        long totalWeight = items.stream().mapToLong(item -> item.chancePpm).sum();
        
        // Выбираем случайное значение
        long randomValue = Rnd.get((int)Math.min(totalWeight, Integer.MAX_VALUE));
        
        // Находим соответствующий предмет
        long currentWeight = 0;
        for (ItemWithChance item : items) {
            currentWeight += item.chancePpm;
            if (randomValue < currentWeight) {
                return item.dropData;
            }
        }
        
        // Fallback - последний предмет
        return items.get(items.size() - 1).dropData;
    }
    
    /**
     * Обработать предметы с высоким шансом обычной логикой
     */
    public static L2DropData processHighChanceItems(List<ItemWithChance> highChanceItems) {
        if (highChanceItems.isEmpty()) {
            return null;
        }
        
        // Используем обычную логику - выбираем по весам и делаем ролл
        // Это аналог оригинального categoryDrops.dropOne()
        
        // Вычисляем общий вес
        long totalWeight = highChanceItems.stream().mapToLong(item -> item.chancePpm).sum();
        
        if (totalWeight <= 0) {
            return null;
        }
        
        // Выбираем предмет по весам
        long randomValue = Rnd.get((int)Math.min(totalWeight, Integer.MAX_VALUE));
        long currentWeight = 0;
        
        for (ItemWithChance item : highChanceItems) {
            currentWeight += item.chancePpm;
            if (randomValue < currentWeight) {
                // Делаем обычный ролл для выбранного предмета
                if (Rnd.get(DropGuaranteeDAO.ONE_PPM_UNIT) < item.chancePpm) {
                    return item.dropData;
                }
                break;
            }
        }
        
        return null;
    }
}