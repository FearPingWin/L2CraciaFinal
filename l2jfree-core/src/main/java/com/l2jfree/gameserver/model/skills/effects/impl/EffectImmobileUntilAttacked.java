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
package com.l2jfree.gameserver.model.skills.effects.impl;

import com.l2jfree.gameserver.model.L2Effect;
import com.l2jfree.gameserver.model.skills.Env;
import com.l2jfree.gameserver.model.skills.templates.L2EffectType;
import com.l2jfree.gameserver.templates.effects.EffectTemplate;

/**
 * @author Ahmed
 */
public final class EffectImmobileUntilAttacked extends L2Effect
{
	public EffectImmobileUntilAttacked(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.IMMOBILEUNTILATTACKED;
	}
	
	/** Notify started */
	@Override
	protected boolean onStart()
	{
		getEffected().startImmobileUntilAttacked();
		return true;
	}
	
	/** Notify exited */
	@Override
	protected void onExit()
	{
		getEffected().stopImmobileUntilAttacked(false);
		for (int id : getSkill().getNegateId())
		{
			getEffected().stopSkillEffects(id);
		}
	}
}
