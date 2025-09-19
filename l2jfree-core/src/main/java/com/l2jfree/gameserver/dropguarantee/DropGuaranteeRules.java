package com.l2jfree.gameserver.dropguarantee;

public final class DropGuaranteeRules {
    private DropGuaranteeRules() {}

    /**
     * Определяет, участвует ли категория в механике гарантированного дропа.
     * Согласно требованиям: гарантия работает для категорий 2 и 3.
     */
    public static boolean isEligible(int category, DropGuaranteeDAO.Source source) {
        if (source == DropGuaranteeDAO.Source.DROP) {
            // Гарантия только для категорий 2 и 3
            return category == 2 || category == 3;
        } else if (source == DropGuaranteeDAO.Source.SPOIL) {
            // SPOIL: для всех спойл-предметов (обычно категория -1)
            return true;
        }
        return false;
    }
}
