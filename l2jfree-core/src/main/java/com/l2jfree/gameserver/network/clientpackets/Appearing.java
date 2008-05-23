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

import com.l2jfree.gameserver.TaskPriority;
import com.l2jfree.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfree.gameserver.network.serverpackets.UserInfo;

/**
 * Appearing Packet Handler<p>
 * <p>
 * 0000: 30 <p>
 * <p>
 * 
 * @version $Revision: 1.3.4.4 $ $Date: 2005/03/29 23:15:33 $
 */
public class Appearing extends L2GameClientPacket
{
	private static final String _C__30_APPEARING = "[C] 30 Appearing";
	//private final static Log _log = LogFactory.getLog(Appearing.class.getName());

	// c

	@Override
	protected void readImpl()
	{
	}

	/** urgent messages, execute immediatly */
	public TaskPriority getPriority() { return TaskPriority.PR_HIGH; }

	@Override
	protected void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if(activeChar == null) return;
		if (activeChar.isTeleporting()) activeChar.onTeleported();

		sendPacket(new UserInfo(activeChar));
	}

	/* (non-Javadoc)
	 * @see com.l2jfree.gameserver.clientpackets.ClientBasePacket#getType()
	 */
	@Override
	public String getType()
	{
		return _C__30_APPEARING;
	}
}
