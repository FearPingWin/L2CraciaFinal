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

import com.l2jfree.gameserver.datatables.SkillTable;
import com.l2jfree.gameserver.handler.IItemHandler;
import com.l2jfree.gameserver.model.L2ItemInstance;
import com.l2jfree.gameserver.model.L2Object;
import com.l2jfree.gameserver.model.L2Skill;
import com.l2jfree.gameserver.model.actor.instance.L2MonsterInstance;
import com.l2jfree.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfree.gameserver.model.actor.instance.L2PlayableInstance;
import com.l2jfree.gameserver.network.SystemMessageId;
import com.l2jfree.gameserver.network.serverpackets.ActionFailed;
import com.l2jfree.gameserver.network.serverpackets.SystemMessage;

/**
 * author: evill33t
 * 
 */
public class HolyWater implements IItemHandler
{
	public static final int		INTERACTION_DISTANCE	= 1000;

	private static final int[]	ITEM_IDS				=
														{ 9673 };

	public void useItem(L2PlayableInstance playable, L2ItemInstance item)
	{
		L2PcInstance activeChar = (L2PcInstance) playable;
		L2Skill skill = SkillTable.getInstance().getInfo(2358, 1);
		L2Object target = activeChar.getTarget();

		if (target instanceof L2MonsterInstance)
		{
			if (((L2MonsterInstance) target).getNpcId() == 18463 || ((L2MonsterInstance) target).getNpcId() == 18464)
			{
				activeChar.doCast(skill);
			}
			else
			{
				activeChar.sendPacket(new SystemMessage(SystemMessageId.INCORRECT_TARGET));
				activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			}
		}
		else
		{
			activeChar.sendPacket(new SystemMessage(SystemMessageId.INCORRECT_TARGET));
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
		}
	}

	public int[] getItemIds()
	{
		return ITEM_IDS;
	}
}
