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
package com.l2jfree.gameserver.handler.itemhandlers;

import com.l2jfree.Config;
import com.l2jfree.gameserver.Shutdown;
import com.l2jfree.gameserver.handler.IItemHandler;
import com.l2jfree.gameserver.model.L2ItemInstance;
import com.l2jfree.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfree.gameserver.model.actor.instance.L2PlayableInstance;
import com.l2jfree.gameserver.network.SystemMessageId;
import com.l2jfree.gameserver.network.serverpackets.ChooseInventoryItem;

public class EnchantScrolls implements IItemHandler
{
	private static final int[]	ITEM_IDS	=
											{ 729, 730, 731, 732, 6569, 6570, // a grade
			947,
			948,
			949,
			950,
			6571,
			6572, // b grade
			951,
			952,
			953,
			954,
			6573,
			6574, // c grade
			955,
			956,
			957,
			958,
			6575,
			6576, // d grade
			959,
			960,
			961,
			962,
			6577,
			6578							// s grade
											};

	public void useItem(L2PlayableInstance playable, L2ItemInstance item)
	{
		if (!(playable instanceof L2PcInstance))
			return;
		L2PcInstance activeChar = (L2PcInstance) playable;
		if (activeChar.isCastingNow())
			return;

		// Restrict enchant during restart/shutdown (because of an existing exploit)
		if (Config.SAFE_REBOOT && Config.SAFE_REBOOT_DISABLE_ENCHANT && Shutdown.getCounterInstance() != null
				&& Shutdown.getCounterInstance().getCountdown() <= Config.SAFE_REBOOT_TIME)
		{
			activeChar.sendMessage("Enchanting items is not allowed during restart/shutdown.");
			return;
		}

		activeChar.setActiveEnchantItem(item);
		activeChar.sendPacket(SystemMessageId.SELECT_ITEM_TO_ENCHANT);

		int itemId = item.getItemId();

		if (Config.ALLOW_CRYSTAL_SCROLL && (itemId == 957 || itemId == 958 || itemId == 953 || itemId == 954 || // Crystal scrolls D and C Grades
				itemId == 949 || itemId == 950 || itemId == 731 || itemId == 732 || // Crystal scrolls B and A Grades
				itemId == 961 || itemId == 962)) // Crystal scrolls S Grade
			activeChar.sendPacket(new ChooseInventoryItem(itemId - 2));
		else
			activeChar.sendPacket(new ChooseInventoryItem(itemId));

		return;
	}

	public int[] getItemIds()
	{
		return ITEM_IDS;
	}
}