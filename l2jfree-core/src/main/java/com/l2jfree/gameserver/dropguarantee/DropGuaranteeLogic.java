package com.l2jfree.gameserver.dropguarantee;

import java.sql.SQLException;

/**
 * Высокоуровневая логика «гаранта»: нарастить счётчик, проверить порог, обнулить после выдачи.
 */
public final class DropGuaranteeLogic {
    private DropGuaranteeLogic() {}

    /**
     * Наращивает счётчик на deltaPpm и возвращает:
     *   true  — гарант достигнут (счётчик >= 1.0),
     *   false — гарант ещё не достигнут.
     */
    public static boolean addAndCheckGuaranteed(int charId, int itemId, int category,
                                                DropGuaranteeDAO.Source source, long deltaPpm)
            throws SQLException {
        long meter = DropGuaranteeDAO.addAndGetMeterPpm(charId, itemId, category, source, deltaPpm);
        return meter >= DropGuaranteeDAO.ONE_PPM_UNIT;
    }

    /**
     * Вызывается после фактической выдачи предмета игроку — обнуляет счётчик.
     */
    public static void resetAfterAward(int charId, int itemId, DropGuaranteeDAO.Source source)
            throws SQLException {
        DropGuaranteeDAO.resetMeter(charId, itemId, source);
    }
}
