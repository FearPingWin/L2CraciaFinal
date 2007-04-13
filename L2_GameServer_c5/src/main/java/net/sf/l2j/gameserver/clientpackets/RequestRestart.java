/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package net.sf.l2j.gameserver.clientpackets;

import java.nio.ByteBuffer;

import net.sf.l2j.gameserver.ClientThread;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.serverpackets.CharSelectInfo;
import net.sf.l2j.gameserver.serverpackets.RestartResponse;

/**
 * This class ...
 * 
 * @version $Revision: 1.11.2.1.2.4 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestRestart extends ClientBasePacket
{
    private static final String _C__46_REQUESTRESTART = "[C] 46 RequestRestart";

    /**
     * packet type id 0x46
     * format:      c
     * @param decrypt
     */
    public RequestRestart(ByteBuffer buf, ClientThread client)
    {
        super(buf, client);
    }

    void runImpl()
    {

        L2PcInstance player = getClient().getActiveChar();
        if (player == null)
            return;
        
        if (player.logout())
        {
            player.sendPacket(new RestartResponse());    
            player.deleteMe();
            player.store();
            
            getClient().setActiveChar(null);
            // send char list
            CharSelectInfo cl = new CharSelectInfo(getClient().getLoginName(),
                                                   getClient().getSessionId().playOkID1);
            sendPacket(cl);
            getClient().setCharSelection(cl.getCharInfo());
        }
    }

    /* (non-Javadoc)
     * @see net.sf.l2j.gameserver.clientpackets.ClientBasePacket#getType()
     */
    public String getType()
    {
        return _C__46_REQUESTRESTART;
    }
}