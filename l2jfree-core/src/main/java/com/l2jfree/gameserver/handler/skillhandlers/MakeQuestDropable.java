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

import com.l2jfree.gameserver.handler.ISkillHandler;
import com.l2jfree.gameserver.model.L2Character;
import com.l2jfree.gameserver.model.L2Object;
import com.l2jfree.gameserver.model.L2Skill;
import com.l2jfree.gameserver.model.actor.instance.L2MonsterInstance;
import com.l2jfree.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfree.gameserver.network.SystemMessageId;
import com.l2jfree.gameserver.network.serverpackets.ActionFailed;
import com.l2jfree.gameserver.templates.L2SkillType;

/**
 *
 * @author  evill33t
 */
public class MakeQuestDropable implements ISkillHandler
{
	private static final L2SkillType[]	SKILL_IDS	=
													{ L2SkillType.MAKE_QUEST_DROPABLE };

	public void useSkill(L2Character activeChar, L2Skill skill, @SuppressWarnings("unused")
	L2Object... targets)
	{
		if (!(activeChar instanceof L2PcInstance))
			return;

		L2Object[] targetList = skill.getTargetList(activeChar);

		if (targetList == null)
		{
			return;
		}

		for (L2Object element : targetList)
		{
			if (!(element instanceof L2MonsterInstance) || ((L2MonsterInstance) element).getQuestDropable())
			{
				activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
				activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			}
			else
			{
				((L2MonsterInstance) element).setQuestDropable(true);
			}
		}
	}

	public L2SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}
