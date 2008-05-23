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

import com.l2jfree.gameserver.network.serverpackets.StartRotation;

/**
 * This class ...
 * 
 * @version $Revision: 1.1.4.3 $ $Date: 2005/03/27 15:29:30 $
 */
public class StartRotating extends L2GameClientPacket
{
	private static final String _C__4A_STARTROTATING = "[C] 4A StartRotating";

	private int _degree;
	private int _side;
	/**
	 * packet type id 0x4a
	 * 
	 * sample
	 * 
	 * 4a
	 * fb 0f 00 00 // degree (goes from 0 to 65535)
	 * 01 00 00 00 // side (01 00 00 00 = right, ff ff ff ff = left)
	 * 
	 * format:		cdd
	 * @param decrypt
	 */
    @Override
    protected void readImpl()
    {
        _degree = readD();
        _side = readD();
    }

    @Override
    protected void runImpl()
	{
		if (getClient().getActiveChar() == null)
		    return;
		StartRotation br = new StartRotation(getClient().getActiveChar(), _degree, _side);
		getClient().getActiveChar().broadcastPacket(br);
	}

	/* (non-Javadoc)
	 * @see com.l2jfree.gameserver.clientpackets.ClientBasePacket#getType()
	 */
	@Override
	public String getType()
	{
		return _C__4A_STARTROTATING;
	}
}
