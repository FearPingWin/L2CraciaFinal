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

import com.l2jfree.gameserver.model.L2ItemInstance;
import com.l2jfree.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfree.gameserver.network.SystemMessageId;
import com.l2jfree.gameserver.network.serverpackets.ExShowBaseAttributeCancelWindow;
import com.l2jfree.gameserver.network.serverpackets.InventoryUpdate;
import com.l2jfree.gameserver.network.serverpackets.SystemMessage;

public class RequestExRemoveItemAttribute extends L2GameClientPacket
{
	private static String _C__D0_23_REQUESTEXREMOVEITEMATTRIBUTE = "[C] D0:23 RequestExRemoveItemAttribute";

	private int _objectId;

	@Override
	public void readImpl()
	{
		_objectId = readD();
	}

	@Override
	public void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
			return;

		L2ItemInstance targetItem = activeChar.getInventory().getItemByObjectId(_objectId);

		if (targetItem == null)
			return;

		if (targetItem.getElementals() == null)
			return;

		if (activeChar.getInventory().getAdena() < 50000)
		{
			activeChar.sendPacket(new SystemMessage(SystemMessageId.YOU_NOT_ENOUGH_ADENA));
		}
		else
		{
			activeChar.reduceAdena("RemoveElement", 50000, activeChar, true);
			targetItem.clearElementAttr();
			InventoryUpdate iu = new InventoryUpdate();
			iu.addModifiedItem(targetItem);
			activeChar.sendPacket(iu);
			SystemMessage sm;
			if (targetItem.getEnchantLevel() > 0)
			{
				sm = new SystemMessage(SystemMessageId.S1_S2_ELEMENTAL_POWER_REMOVED);
				sm.addNumber(targetItem.getEnchantLevel());
				sm.addItemName(targetItem);
			}
			else
			{
				sm = new SystemMessage(SystemMessageId.S1_ELEMENTAL_POWER_REMOVED);
				sm.addItemName(targetItem);
			}
			activeChar.sendPacket(sm);
			activeChar.getInventory().reloadEquippedItems();
			activeChar.broadcastUserInfo();
			activeChar.sendPacket(new ExShowBaseAttributeCancelWindow(activeChar));
		}
	}

	@Override
	public String getType()
	{
		return _C__D0_23_REQUESTEXREMOVEITEMATTRIBUTE;
	}
}