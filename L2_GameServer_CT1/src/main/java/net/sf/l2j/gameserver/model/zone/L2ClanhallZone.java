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
package net.sf.l2j.gameserver.model.zone;

import net.sf.l2j.gameserver.instancemanager.ClanHallManager;
import net.sf.l2j.gameserver.model.L2Character;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.entity.ClanHall;
import net.sf.l2j.gameserver.network.serverpackets.AgitDecoInfo;

public class L2ClanhallZone extends L2DefaultZone
{
	protected ClanHall _clanhall;

	@Override
	protected void register()
	{
		_clanhall = ClanHallManager.getInstance().getClanHallById(_clanhallId);
		_clanhall.registerZone(this);
	}

	@Override
	protected void onEnter(L2Character character)
	{
		if (character instanceof L2PcInstance)
		{
			// Set as in clan hall
			character.setInsideZone(FLAG_CLANHALL, true);

			// Send decoration packet
			AgitDecoInfo deco = new AgitDecoInfo(_clanhall);
			((L2PcInstance)character).sendPacket(deco);

			// Send a message
			if (_clanhall.getOwnerId() != 0 && _clanhall.getOwnerId() == ((L2PcInstance)character).getClanId())
				((L2PcInstance)character).sendMessage("You have entered your clan hall");
		}

		super.onEnter(character);
	}
	
	@Override
	protected void onExit(L2Character character)
	{
		if (character instanceof L2PcInstance)
		{
			// Unset clanhall zone
			character.setInsideZone(FLAG_CLANHALL, false);

			// Send a message
			if (_clanhall.getOwnerId() != 0 && _clanhall.getOwnerId() == ((L2PcInstance)character).getClanId())
				((L2PcInstance)character).sendMessage("You have left your clan hall");
		}

		super.onExit(character);
	}
}
