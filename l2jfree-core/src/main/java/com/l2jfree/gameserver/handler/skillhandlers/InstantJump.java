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

import com.l2jfree.gameserver.ai.CtrlIntention;
import com.l2jfree.gameserver.handler.ISkillHandler;
import com.l2jfree.gameserver.model.L2Character;
import com.l2jfree.gameserver.model.L2Object;
import com.l2jfree.gameserver.model.L2Skill;
import com.l2jfree.gameserver.network.SystemMessageId;
import com.l2jfree.gameserver.network.serverpackets.FlyToLocation;
import com.l2jfree.gameserver.network.serverpackets.SystemMessage;
import com.l2jfree.gameserver.network.serverpackets.ValidateLocation;
import com.l2jfree.gameserver.network.serverpackets.FlyToLocation.FlyType;
import com.l2jfree.gameserver.skills.Formulas;
import com.l2jfree.gameserver.templates.skills.L2SkillType;
import com.l2jfree.tools.random.Rnd;

/**
 *
 * @author Didldak
 * Some parts taken from EffectWarp, which cannot be used for this case.
 */
public class InstantJump implements ISkillHandler
{
	private static final L2SkillType[] SKILL_IDS =
	{
		L2SkillType.INSTANT_JUMP
	};

	public void useSkill(L2Character activeChar, L2Skill skill, L2Object... targets)
	{
		if (targets.length == 0 ||
				!(targets[0] instanceof L2Character))
			return;
		
		L2Character target = (L2Character)targets[0];

		Formulas f = Formulas.getInstance();
		
		int x = 0, y = 0, z = 0;
		
		// Gracia Final support = it ports you behind your target, uncomment these lines when Gracia Final! is out.
		/*
		int px = target.getX();
		int py = target.getY();
		double ph = Util.convertHeadingToDegree(target.getHeading());
		
		ph+=180;
		
		if(ph>360)
			ph-=360;
		
		ph = (Math.PI * ph) / 180;
		
		x = (int) (px + (25 * Math.cos(ph)));
		y = (int) (py + (25 * Math.sin(ph)));
		z = target.getZ();
		*/
		
		// Gracia Part I+II pozitioning - comment these lines when gracia final is out
		// a little random location around player... not directly "into him"
		x = target.getX() + Rnd.nextInt(5);
		y = target.getY() + Rnd.nextInt(5);
		z = target.getZ();
		
		
		activeChar.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
		activeChar.broadcastPacket(new FlyToLocation(activeChar, x, y, z, FlyType.DUMMY));
		activeChar.abortAttack();
		activeChar.abortCast();
		
		activeChar.getPosition().setXYZ(x, y, z);
		activeChar.broadcastPacket(new ValidateLocation(activeChar));
		
		if (skill.hasEffects())
		{
			if (target.reflectSkill(skill))
			{
				activeChar.stopSkillEffects(skill.getId());
				skill.getEffects(target, activeChar);
				//SystemMessage sm = new SystemMessage(SystemMessageId.YOU_FEEL_S1_EFFECT);
				//sm.addSkillName(skill);
				//activeChar.sendPacket(sm);
			}
			else
			{
				// activate attacked effects, if any
				target.stopSkillEffects(skill.getId());
				byte shld = f.calcShldUse(activeChar, target);
				if (f.calcSkillSuccess(activeChar, target, skill, shld, false, false, false))
				{
					skill.getEffects(activeChar, target);
				
					//SystemMessage sm = new SystemMessage(SystemMessageId.YOU_FEEL_S1_EFFECT);
					//sm.addSkillName(skill);
					//target.sendPacket(sm);
				}
				else
				{
					SystemMessage sm = new SystemMessage(SystemMessageId.S1_WAS_UNAFFECTED_BY_S2);
					sm.addCharName(target);
					sm.addSkillName(skill);
					activeChar.sendPacket(sm);
				}
			}
		}
	}
	
	
	
	/**
	 * 
	 * @see com.l2jfree.gameserver.handler.ISkillHandler#getSkillIds()
	 */
	public L2SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}
