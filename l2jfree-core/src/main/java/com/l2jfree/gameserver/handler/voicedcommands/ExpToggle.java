package com.l2jfree.gameserver.handler.voicedcommands;

import com.l2jfree.gameserver.handler.IVoicedCommandHandler;
import com.l2jfree.gameserver.gameobjects.L2Player;

public final class ExpToggle implements IVoicedCommandHandler {
    private static final String[] VOICED_COMMANDS = { "exp" };

    @Override
    public boolean useVoicedCommand(String command, L2Player activeChar, String target) {
        if (activeChar == null) { return false; }
        if (!"exp".equalsIgnoreCase(command)) { return false; }

        boolean nowDisabled = !activeChar.isExpDisabled();
        activeChar.setExpDisabled(nowDisabled);
        if (nowDisabled) { activeChar.sendMessage("EXP gain disabled."); }
        else { activeChar.sendMessage("EXP gain enabled."); }
        return true;
    }

    @Override
    public String[] getVoicedCommandList() { return VOICED_COMMANDS; }
}
