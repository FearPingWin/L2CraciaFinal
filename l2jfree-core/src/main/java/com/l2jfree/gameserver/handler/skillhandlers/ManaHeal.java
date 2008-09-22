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
import com.l2jfree.gameserver.model.L2Skill.SkillType;
import com.l2jfree.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfree.gameserver.network.SystemMessageId;
import com.l2jfree.gameserver.network.serverpackets.StatusUpdate;
import com.l2jfree.gameserver.network.serverpackets.SystemMessage;
import com.l2jfree.gameserver.skills.Stats;

/**
 * This class ...
 * 
 * @version $Revision: 1.1.2.2.2.1 $ $Date: 2005/03/02 15:38:36 $
 */

public class ManaHeal implements ISkillHandler
{
	/* (non-Javadoc)
	 * @see com.l2jfree.gameserver.handler.IItemHandler#useItem(com.l2jfree.gameserver.model.L2PcInstance, com.l2jfree.gameserver.model.L2ItemInstance)
	 */
	private static final SkillType[]	SKILL_IDS	=
													{ SkillType.MANAHEAL, SkillType.MANARECHARGE, SkillType.MANAHEAL_PERCENT };

	/* (non-Javadoc)
	 * @see com.l2jfree.gameserver.handler.IItemHandler#useItem(com.l2jfree.gameserver.model.L2PcInstance, com.l2jfree.gameserver.model.L2ItemInstance)
	 */
	public void useSkill(@SuppressWarnings("unused")
	L2Character actChar, L2Skill skill, L2Object... targets)
	{
		L2Character target = null;

		for (L2Object element : targets)
		{
			target = (L2Character) element;

			double mp = skill.getPower();
			if (skill.getSkillType() == SkillType.MANAHEAL_PERCENT)
			{
				//double mp = skill.getPower();
				mp = target.getMaxMp() * mp / 100.0;
			}
			else
			{
				mp = (skill.getSkillType() == SkillType.MANARECHARGE) ? target.calcStat(Stats.RECHARGE_MP_RATE, mp, null, null) : mp;
			}

			if (actChar.getLevel() != target.getLevel())
			{
				if (actChar.getLevel() + 3 >= target.getLevel())
					mp = mp * 1;
				else if (actChar.getLevel() + 5 <= target.getLevel())
					mp = mp * 0.6;
				else if (actChar.getLevel() + 7 <= target.getLevel())
					mp = mp * 0.4;
				else if (actChar.getLevel() + 9 <= target.getLevel())
					mp = mp * 0.3;
				else if (actChar.getLevel() + 10 <= target.getLevel())
					mp = mp * 0.1;
			}

			//from CT2 u will receive exact MP, u can't go over it, if u have full MP and u get MP buff, u will receive 0MP restored message
			if ((target.getStatus().getCurrentMp() + mp) >= target.getMaxMp())
			{
				mp = target.getMaxMp() - target.getStatus().getCurrentMp();
			}

			target.setLastHealAmount((int) mp);
			target.getStatus().setCurrentMp(mp + target.getStatus().getCurrentMp());
			StatusUpdate sump = new StatusUpdate(target.getObjectId());
			sump.addAttribute(StatusUpdate.CUR_MP, (int) target.getStatus().getCurrentMp());
			target.sendPacket(sump);

			if (actChar instanceof L2PcInstance && actChar != target)
			{
				SystemMessage sm = new SystemMessage(SystemMessageId.S2_MP_RESTORED_BY_S1);
				sm.addString(actChar.getName());
				sm.addNumber((int) mp);
				target.sendPacket(sm);
			}
			else
			{
				SystemMessage sm = new SystemMessage(SystemMessageId.S1_MP_RESTORED);
				sm.addNumber((int) mp);
				target.sendPacket(sm);
			}
		}
	}

	public SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}
