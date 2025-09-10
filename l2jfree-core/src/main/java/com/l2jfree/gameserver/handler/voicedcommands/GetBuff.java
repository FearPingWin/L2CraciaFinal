package com.l2jfree.gameserver.handler.voicedcommands;

import com.l2jfree.gameserver.handler.IVoicedCommandHandler;
import com.l2jfree.gameserver.gameobjects.L2Player;
import com.l2jfree.gameserver.model.skills.L2Skill;
import com.l2jfree.gameserver.datatables.SkillTable;

import java.lang.reflect.Method;

public final class GetBuff implements IVoicedCommandHandler {
    private static final String[] VOICED_COMMANDS = { "getbuff" };

    private static final int[][] BUFFS = new int[][]{
        {1204,2},  // Wind Walk 2 -common
        {1040,3},  // Shield 3 -common
        {1048,6},  // Bless the Soul- wizard
        {1045,6},  // Bless the Body
        {1086,1},  // Haste -warrior
        {1268,4},  // Vampiric Rage
        {1044,3},  // Regeneration
        {1085,3},  // Acumen
        {1078,1},  // Concentration
        {1059,1},  // Empower
        {1036,3},  // Magic Barrier 3
        //{1045,3},  // Bless the Body
        //{1240,3},  // Guidance
        //{1077,3},  // Focus
        //{1242,3}   // Death Whisper
        //{1251,2},  // Might
    };

    @Override
    public boolean useVoicedCommand(String command, L2Player activeChar, String target) {
        if (activeChar == null) return false;
        if (!"getbuff".equalsIgnoreCase(command)) return false;

        for (int[] s : BUFFS) {
            L2Skill skill = SkillTable.getInstance().getInfo(s[0], s[1]);
            if (skill == null) continue;
            applySkillEffects(skill, activeChar); // мгновенно, без MP/каста
        }

        activeChar.sendMessage("Newbie buffs applied.");
        return true;
    }

    @Override
    public String[] getVoicedCommandList() { return VOICED_COMMANDS; }

    private static void applySkillEffects(L2Skill skill, L2Player player) {
        try {
            Class<?> CRE = Class.forName("com.l2jfree.gameserver.gameobjects.L2Creature");

            try {
                Method m = skill.getClass().getMethod("getEffects", CRE, CRE);
                m.invoke(skill, player, player);
                return;
            } catch (NoSuchMethodException ignored) {}

            try {
                Method m = skill.getClass().getMethod("getEffects", CRE, CRE, boolean.class, boolean.class);
                m.invoke(skill, player, player, false, false);
                return;
            } catch (NoSuchMethodException ignored) {}

            try {
                Method m = skill.getClass().getMethod("applyEffects", CRE, CRE);
                m.invoke(skill, player, player);
                return;
            } catch (NoSuchMethodException ignored) {}
        } catch (Exception ignored) {}
    }
}
