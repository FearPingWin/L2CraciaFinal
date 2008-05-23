/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jfree.gameserver.network.clientpackets;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.l2jfree.gameserver.ai.CtrlEvent;
import com.l2jfree.gameserver.model.L2CharPosition;
import com.l2jfree.gameserver.model.L2Character;
import com.l2jfree.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfree.gameserver.network.serverpackets.PartyMemberPosition;

/**
 * This class ...
 * 
 * @version $Revision: 1.1.2.1.2.4 $ $Date: 2005/03/27 15:29:30 $
 */
public class CannotMoveAnymore extends L2GameClientPacket
{
	private static final String _C__36_STOPMOVE = "[C] 36 CannotMoveAnymore";
	private final static Log _log = LogFactory.getLog(CannotMoveAnymore.class.getName());
	

	private int _x;
	private int _y;
	private int _z;
	private int _heading;
	/**
	 * packet type id 0x36
	 * 
	 * sample
	 * 
	 * 36
	 * a8 4f 02 00 // x
	 * 17 85 01 00 // y
	 * a7 00 00 00 // z
	 * 98 90 00 00 // heading?
	 * 
	 * format:		cdddd
	 * @param decrypt
	 */
    @Override
    protected void readImpl()
    {
        _x = readD();
        _y = readD();
        _z = readD();
        _heading = readD();
    }

    @Override
    protected void runImpl()
	{
		L2Character player = getClient().getActiveChar();
		if (player == null)
		    return;
		if (_log.isDebugEnabled())
			_log.debug("client: x:"+_x+" y:"+_y+" z:"+_z+
					" server x:"+player.getX()+" y:"+player.getY()+" z:"+player.getZ());
		if (player.getAI() != null)
        {
		    player.getAI().notifyEvent(CtrlEvent.EVT_ARRIVED_BLOCKED, new L2CharPosition(_x, _y, _z, _heading));
        }
		if(player instanceof L2PcInstance && ((L2PcInstance)player).getParty() != null)
			((L2PcInstance)player).getParty().broadcastToPartyMembers(((L2PcInstance)player),new PartyMemberPosition((L2PcInstance)player));

//		player.stopMove();
//
//		if (_log.isDebugEnabled())
//			_log.debug("client: x:"+_x+" y:"+_y+" z:"+_z+
//					" server x:"+player.getX()+" y:"+player.getZ()+" z:"+player.getZ());
//		StopMove smwl = new StopMove(player);
//		getClient().getActiveChar().sendPacket(smwl);
//		getClient().getActiveChar().broadcastPacket(smwl);
//		
//		StopRotation sr = new StopRotation(getClient().getActiveChar(), _heading);
//		getClient().getActiveChar().sendPacket(sr);
//		getClient().getActiveChar().broadcastPacket(sr);
	}

	/* (non-Javadoc)
	 * @see net.sf.l2j.gameserver.clientpackets.ClientBasePacket#getType()
	 */
	@Override
	public String getType()
	{
		return _C__36_STOPMOVE;
	}
}
