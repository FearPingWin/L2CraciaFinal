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
package com.l2jfree.gameserver.skills.effects;

import com.l2jfree.gameserver.model.L2Effect;
import com.l2jfree.gameserver.model.actor.instance.L2PlayableInstance;
import com.l2jfree.gameserver.skills.Env;

/**
 * @author earendil
 */
public final class EffectNoblesseBless extends L2Effect
{

	public EffectNoblesseBless(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	public EffectType getEffectType()
	{
		return EffectType.NOBLESSE_BLESSING;
	}

	/** Notify started */
	public void onStart()
	{
		if (getEffected() instanceof L2PlayableInstance)
			((L2PlayableInstance) getEffected()).startNoblesseBlessing();
	}

	/** Notify exited */
	public void onExit()
	{
		if (getEffected() instanceof L2PlayableInstance)
			((L2PlayableInstance) getEffected()).stopNoblesseBlessing(this);
	}

	public boolean onActionTime()
	{
		// just stop this effect
		return false;
	}
}
