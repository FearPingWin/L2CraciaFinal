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
package com.l2jfree.gameserver.handler.skillhandlers;

import com.l2jfree.gameserver.datatables.SkillTable;
import com.l2jfree.gameserver.handler.ISkillHandler;
import com.l2jfree.gameserver.model.L2Character;
import com.l2jfree.gameserver.model.L2Object;
import com.l2jfree.gameserver.model.L2Skill;
import com.l2jfree.gameserver.model.L2Skill.SkillType;
import com.l2jfree.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfree.gameserver.network.SystemMessageId;

/**
 * 
 * @author nBd
 */

public class Soul implements ISkillHandler
{
	private static final SkillType[]	SKILL_IDS	=
													{ SkillType.CHARGESOUL };

	public void useSkill(L2Character activeChar, L2Skill skill, @SuppressWarnings("unused")
	L2Object... targets)
	{
		if (!(activeChar instanceof L2PcInstance) || activeChar.isAlikeDead())
			return;

		L2PcInstance player = (L2PcInstance) activeChar;

		L2Skill soulmastery = SkillTable.getInstance().getInfo(L2Skill.SKILL_SOUL_MASTERY, player.getSkillLevel(L2Skill.SKILL_SOUL_MASTERY));

		if (soulmastery != null)
		{
			if (player.getSouls() < soulmastery.getNumSouls())
			{
				int count = 0;

				if (player.getSouls() + skill.getNumSouls() <= soulmastery.getNumSouls())
					count = skill.getNumSouls();
				else
					count = soulmastery.getNumSouls() - player.getSouls();

				player.increaseSouls(count);
			}
			else
			{
				player.sendPacket(SystemMessageId.SOUL_CANNOT_BE_INCREASED_ANYMORE);
			}
		}
	}

	public SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}