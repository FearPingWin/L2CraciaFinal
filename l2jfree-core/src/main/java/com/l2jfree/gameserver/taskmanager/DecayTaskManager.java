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
package com.l2jfree.gameserver.taskmanager;

import java.util.Map;

import javolution.util.FastMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.l2jfree.gameserver.ThreadPoolManager;
import com.l2jfree.gameserver.model.L2Boss;
import com.l2jfree.gameserver.model.L2Character;
import com.l2jfree.gameserver.model.L2Summon;
import com.l2jfree.gameserver.model.actor.instance.L2MonsterInstance;
import com.l2jfree.gameserver.model.actor.instance.L2PetInstance;
import com.l2jfree.tools.random.Rnd;

public final class DecayTaskManager implements Runnable
{
	private final static Log _log = LogFactory.getLog(DecayTaskManager.class);
	
	public static final int RAID_BOSS_DECAY_TIME = 30000;
	public static final int ATTACKABLE_DECAY_TIME = 8500;
	
	private static DecayTaskManager _instance;
	
	public static DecayTaskManager getInstance()
	{
		if (_instance == null)
			_instance = new DecayTaskManager();
		
		return _instance;
	}
	
	private final FastMap<L2Character, Long> _decayTasks = new FastMap<L2Character, Long>();
	
	private DecayTaskManager()
	{
		ThreadPoolManager.getInstance().scheduleAtFixedRate(this, Rnd.get(1000), Rnd.get(990, 1010));
		
		_log.info("DecayTaskManager: Initialized.");
	}
	
	public synchronized boolean hasDecayTask(L2Character actor)
	{
		return _decayTasks.containsKey(actor);
	}
	
	public synchronized double getRemainingDecayTime(L2Character actor)
	{
		double remaining = _decayTasks.get(actor) - System.currentTimeMillis();
		
		return remaining / getDecayTime0(actor);
	}
	
	public synchronized void addDecayTask(L2Character actor)
	{
		_decayTasks.put(actor, System.currentTimeMillis() + getDecayTime0(actor));
	}
	
	private int getDecayTime0(L2Character actor)
	{
		if (actor instanceof L2MonsterInstance)
		{
			switch (((L2MonsterInstance)actor).getNpcId())
			{
				case 29019: // Antharas
				case 29066: // Antharas
				case 29067: // Antharas
				case 29068: // Antharas
					return 12000;
				case 29028: // Valakas
					return 18000;
				case 29014: // Orfen
				case 29001: // Queen Ant
					return 150000;
				case 29045: // Frintezza
					return 9500;
				case 29046: // Scarlet Van Halisha lvl 85 -> Morphing
					return 2000;
				case 29047: // Scarlet Van Halisha lvl 90
					return 7500;
			}
		}
		
		if (actor instanceof L2Boss)
			return RAID_BOSS_DECAY_TIME;
		
		if (actor instanceof L2PetInstance)
			return 86400000;
		
		if (actor instanceof L2Summon)
			return 300000;
		
		return ATTACKABLE_DECAY_TIME;
	}
	
	public synchronized void cancelDecayTask(L2Character actor)
	{
		_decayTasks.remove(actor);
	}
	
	public synchronized void run()
	{
		for (Map.Entry<L2Character, Long> entry : _decayTasks.entrySet())
		{
			if (System.currentTimeMillis() > entry.getValue())
			{
				final L2Character actor = entry.getKey();
				
				actor.onDecay();
				
				_decayTasks.remove(actor);
			}
		}
	}
	
	public synchronized String getStats()
	{
		final StringBuilder sb = new StringBuilder();
		sb.append("============= DecayTask Manager Report ============").append("\r\n");
		sb.append("Tasks count: ").append(_decayTasks.size()).append("\r\n");
		sb.append("Tasks dump:").append("\r\n");
		
		for (L2Character actor : _decayTasks.keySet())
		{
			sb.append("(").append(_decayTasks.get(actor) - System.currentTimeMillis()).append(") - ");
			sb.append(actor.getClass().getSimpleName()).append("/").append(actor.getName()).append("\r\n");
		}
		
		return sb.toString();
	}
}
