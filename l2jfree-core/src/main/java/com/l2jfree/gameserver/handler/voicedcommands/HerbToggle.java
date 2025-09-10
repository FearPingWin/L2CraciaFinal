package com.l2jfree.gameserver.handler.voicedcommands;

import com.l2jfree.gameserver.handler.IVoicedCommandHandler;
import com.l2jfree.gameserver.gameobjects.L2Player;

public final class HerbToggle implements IVoicedCommandHandler {
    private static final String[] VOICED_COMMANDS = { "herb" };

    @Override
    public boolean useVoicedCommand(String command, L2Player activeChar, String target) {
        if (activeChar == null) { return false; }
        if (!"herb".equalsIgnoreCase(command)) { return false; }

        boolean nowEnabled = !activeChar.isAutoHerbPickupEnabled();
        activeChar.setAutoHerbPickupEnabled(nowEnabled);
        if (nowEnabled) { activeChar.sendMessage("Auto herb pickup enabled."); }
        else { activeChar.sendMessage("Auto herb pickup disabled."); }
        return true;
    }

    @Override
    public String[] getVoicedCommandList() { return VOICED_COMMANDS; }
}
