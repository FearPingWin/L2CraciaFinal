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
package com.l2jfree.gameserver.skills.l2skills;

import com.l2jfree.gameserver.model.L2Character;
import com.l2jfree.gameserver.model.L2Effect;
import com.l2jfree.gameserver.model.L2Object;
import com.l2jfree.gameserver.model.L2Skill;
import com.l2jfree.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfree.gameserver.network.SystemMessageId;
import com.l2jfree.gameserver.network.serverpackets.SystemMessage;
import com.l2jfree.gameserver.skills.Stats;
import com.l2jfree.gameserver.skills.funcs.Func;
import com.l2jfree.gameserver.templates.skills.L2SkillType;
import com.l2jfree.gameserver.templates.StatsSet;

/**
 * Used for Break Duress skill mainly
 * uses number of charges to negate, negate number depends on charge consume  
 * @author Darki699
 */
public class L2SkillChargeNegate extends L2Skill
{
	public L2SkillChargeNegate(StatsSet set)
	{
		super(set);
	}

	@Override
	public void useSkill(L2Character activeChar, L2Object... targets)
	{
		if (activeChar.isAlikeDead() || !(activeChar instanceof L2PcInstance))
			return;

		for (L2Object element : targets)
		{
			L2Character target = (L2Character) element;
			if (target.isAlikeDead())
				continue;

			String[] _negateStats = getNegateStats();
			int count = 0;
			for (String stat : _negateStats)
			{
				count++;
				if (count > getNeededCharges())
				{
					// ROOT=1 PARALYZE=2 SLOW=3 
					return;
				}

				if (stat == "root")
				{
					negateEffect(target, L2SkillType.ROOT);
				}
				if (stat == "slow")
				{
					negateEffect(target, L2SkillType.DEBUFF);
					negateEffect(target, L2SkillType.WEAKNESS);
				}
				if (stat == "paralyze")
				{
					negateEffect(target, L2SkillType.PARALYZE);
				}
			}
		}
	}

	private void negateEffect(L2Character target, L2SkillType type)
	{
		L2Effect[] effects = target.getAllEffects();
		for (L2Effect e : effects)
		{
			if (type == L2SkillType.DEBUFF || type == L2SkillType.WEAKNESS)
			{
				if (e.getSkill().getSkillType() == type)
				{
					// Only exit debuffs and weaknesses affecting runSpd 
					for (Func f : e.getStatFuncs())
					{
						if (f.stat == Stats.RUN_SPEED)
						{
							if (target instanceof L2PcInstance)
							{
								SystemMessage sm = new SystemMessage(SystemMessageId.S1_HAS_WORN_OFF);
								sm.addSkillName(e.getSkill().getId());
								target.sendPacket(sm);
							}
							e.exit();
							break;
						}
					}
				}
			}
			else if (e.getSkill().getSkillType() == type)
			{
				if (target instanceof L2PcInstance)
				{
					SystemMessage sm = new SystemMessage(SystemMessageId.S1_HAS_WORN_OFF);
					sm.addSkillName(e.getSkill().getId());
					target.sendPacket(sm);
				}
				e.exit();
			}
		}
	}
}
