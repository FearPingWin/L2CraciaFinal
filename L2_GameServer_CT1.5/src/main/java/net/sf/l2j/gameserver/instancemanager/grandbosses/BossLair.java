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
package net.sf.l2j.gameserver.instancemanager.grandbosses;

import net.sf.l2j.gameserver.ThreadPoolManager;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.entity.Entity;
import net.sf.l2j.gameserver.model.entity.GrandBossState;
import net.sf.l2j.gameserver.model.mapregion.TeleportWhereType;
import net.sf.l2j.gameserver.model.quest.QuestState;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * This class ...
 * control for sequence of fight against Antharas.
 * @version $Revision: $ $Date: $
 * @author  L2J_JP SANDMAN
 */
public abstract class BossLair extends Entity
{
	protected final static Log _log = LogFactory.getLog(BossLair.class.getName());

	public abstract void init();
	public abstract void setUnspawn();

	protected GrandBossState _state;
	protected String _questName;

	public GrandBossState.StateEnum getState()
	{
		return _state.getState();
	}

	public boolean isEnableEnterToLair()
	{
		return _state.getState() == GrandBossState.StateEnum.NOTSPAWN;
	}

	public synchronized boolean isPlayersAnnihilated()
	{
		for (L2PcInstance pc : getPlayersInside())
		{
			if (!pc.isDead())
				return false;
		}
		return true;
	}

	public void checkAnnihilated()
	{
		if(isPlayersAnnihilated())
		{
			ThreadPoolManager.getInstance().scheduleGeneral(new Runnable()
			{
				public void run()
				{
					setUnspawn();
				}
			}, 5000);
		}
	}

	@Override
	public void banishForeigners()
	{
		// Get players from this and nearest world regions
		for (L2PcInstance player : getPlayersInside())
		{
			if (_questName != null)
			{
				QuestState qs = player.getQuestState(_questName);
				if(qs != null)
					qs.exitQuest(true);
			}
			player.teleToLocation(TeleportWhereType.Town);
		}
	}
}
