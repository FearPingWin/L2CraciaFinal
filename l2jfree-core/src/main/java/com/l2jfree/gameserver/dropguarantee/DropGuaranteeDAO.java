package com.l2jfree.gameserver.dropguarantee;

/**
 * Перечисление источников дропа для системы гарантии.
 * Также содержит константы, используемые в системе.
 */
public final class DropGuaranteeDAO {
    public enum Source { DROP, SPOIL }

    /** Единица счётчика (1.0) в частях на миллион. */
    public static final long ONE_PPM_UNIT = 1_000_000L;

    private DropGuaranteeDAO() { }
}
