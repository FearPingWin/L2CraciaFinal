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
import java.util.logging.Logger;

import net.sf.l2j.gameserver.ClientThread;
import net.sf.l2j.gameserver.model.L2Clan;
import net.sf.l2j.gameserver.model.L2ClanMember;
import net.sf.l2j.gameserver.serverpackets.PledgeReceivePowerInfo;

/**
 * This class ...
 * 
 * @version $Revision: 1.3.4.4 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestPledgeMemberPowerInfo extends ClientBasePacket
{
	private static final String _C__24_REQUESTJOINPLEDGE = "[C] 24 RequestPledgeMemberPowerInfo";
	static Logger _log = Logger.getLogger(RequestJoinPledge.class.getName());

	private final int _not_known;
    private final String _target;
	
	public RequestPledgeMemberPowerInfo(ByteBuffer buf, ClientThread client)
	{
		super(buf, client);
        _not_known  = readD();
        _target = readS();
        //_log.warning("1.pledge member power info packet started: "+_target);
	}

	void runImpl()
	{
		L2Clan clan = getClient().getActiveChar().getClan();
        if (clan != null)
        {
            L2ClanMember cm = clan.getClanMember(_target);
            if (cm != null && cm.isOnline())
            {
                getClient().getActiveChar().sendPacket(new PledgeReceivePowerInfo(cm.getPlayerInstance()));
                //_log.warning("Everything is Ok with this packet: "+_target);
            }            
            //else 
                //_log.warning("Wtf is worng with this packet");            
        }
        //_log.warning("Wtf is worng with this packet, no clan?!?!?!?!?");
	}

	/* (non-Javadoc)
	 * @see net.sf.l2j.gameserver.clientpackets.ClientBasePacket#getType()
	 */
	public String getType()
	{
		return _C__24_REQUESTJOINPLEDGE;
	}
}
