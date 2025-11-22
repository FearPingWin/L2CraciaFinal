package com.l2jfree.gameserver.handler.voicedcommands;

import com.l2jfree.gameserver.gameobjects.L2Player;
import com.l2jfree.gameserver.handler.IVoicedCommandHandler;

/**
 * Voiced command .delevel
 * Decreases player's level by 1 by removing the corresponding amount of EXP.
 */
public final class Delevel implements IVoicedCommandHandler
{
    private static final String[] VOICED_COMMANDS = { "delevel" };

    @Override
    public boolean useVoicedCommand(String command, L2Player activeChar, String target)
    {
        if (activeChar == null)
            return false;

        if (!"delevel".equalsIgnoreCase(command))
            return false;
        final int currentLevel = activeChar.getLevel();
        if (currentLevel <= 1)
        {
            activeChar.sendMessage("You cannot delevel below level 1.");
            return true;
        }

        final long currentExp = activeChar.getStat().getExp();
        final long targetExp = activeChar.getStat().getExpForLevel(currentLevel - 1);
        
        if (currentExp <= targetExp)
        {
            activeChar.sendMessage("Your experience is already at or below the previous level threshold.");
            return true;
        }

        final long expToRemove = currentExp - targetExp;
        activeChar.getStat().addExp(-expToRemove);
        activeChar.sendMessage("Your level has been decreased by 1. Current level: " + activeChar.getLevel());
        return true;
    }

    @Override
    public String[] getVoicedCommandList()
    {
        return VOICED_COMMANDS;
    }
}
