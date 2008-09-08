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
package ai.individual;

import java.util.List;
import javolution.util.FastList;

import com.l2jfree.gameserver.ai.CtrlIntention;
import com.l2jfree.gameserver.datatables.SkillTable;
import com.l2jfree.gameserver.model.L2Attackable;
import com.l2jfree.gameserver.model.actor.instance.L2NpcInstance;
import com.l2jfree.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfree.gameserver.network.serverpackets.PlaySound;
import com.l2jfree.gameserver.network.serverpackets.SocialAction;
import com.l2jfree.tools.random.Rnd;
import ai.group_template.L2AttackableAIScript;

/**
 * Queen Ant AI
 * @author Emperorc
 *
 */
public class QueenAnt extends L2AttackableAIScript
{
	private static final int QUEEN = 29001;
	private static final int LARVA = 29002;
	private static final int NURSE = 29003;
	private static final int GUARD = 29004;
	private static final int ROYAL = 29005;

	private static boolean _isAlive = false;

	private static List<L2Attackable> _Minions = new FastList<L2Attackable>();

	public QueenAnt (int questId, String name, String descr)
	{
		super(questId,name,descr);
		int[] mobs = {QUEEN, LARVA, NURSE, GUARD, ROYAL};
		registerMobs(mobs);
	}

	public String onSpawn(L2NpcInstance npc)
	{
		if (npc.getNpcId() == QUEEN)
		{
			_isAlive = true;
			startQuestTimer("action", 10000, npc, null, true);
			npc.broadcastPacket(new PlaySound(1, "BS02_D", 1, npc.getObjectId(), npc.getX(), npc.getY(), npc.getZ()));
			//Spawn minions
			addSpawn(LARVA,-21600, 179482, -5846, Rnd.get(360), false, 0);
			addSpawn(NURSE,-22000, 179482, -5846, 0, false, 0);
			addSpawn(NURSE,-21200, 179482, -5846, 0, false, 0);
			int radius = 400;
			for (int i = 0; i<6; i++)
			{
				int x = (int) (radius*Math.cos(i*1.407)); //1.407~2pi/6
				int y = (int) (radius*Math.sin(i*1.407));
				addSpawn(NURSE, npc.getX()+x, npc.getY()+y, npc.getZ(), 0, false, 0);
			}
			for (int i = 0; i < 8; i++)
			{
				int x = (int) (radius*Math.cos(i*.7854)); //.7854~2pi/8
				int y = (int) (radius*Math.sin(i*.7854));
				_Minions.add((L2Attackable) addSpawn(ROYAL, npc.getX() + x, npc.getY() + y, npc.getZ(), 0, false, 0));
			}
			startQuestTimer("check_royal__Zone", 120000, npc, null, true);
		}
		return null;
	}

	public String onAdvEvent (String event, L2NpcInstance npc, L2PcInstance player)
	{
		if (event.equalsIgnoreCase("action") && npc != null)
		{
			if (Rnd.get(3)==0)
			{
				if (Rnd.get(2)==0)
				{
					npc.broadcastPacket(new SocialAction(npc.getObjectId(), 3));
				}
				else
				{
					npc.broadcastPacket(new SocialAction(npc.getObjectId(), 4));
				}
			}
		}
		else if (event.equalsIgnoreCase("check_royal__Zone") && npc != null)
		{
			for (L2Attackable mob : _Minions)
			{
				if (mob != null && !npc.isInsideRadius(mob, 1000, false, false))
				{
					mob.teleToLocation(npc.getX(),npc.getY(),npc.getZ());
				}
			}
		}
		else if (event.equalsIgnoreCase("despawn_royals"))
		{
			for (L2Attackable mob : _Minions)
			{
				if (mob != null)
				{
					mob.decayMe();
				}
			}
			_Minions.clear();
		}
		else if (event.equalsIgnoreCase("spawn_royal"))
		{
			_Minions.add((L2Attackable) addSpawn(ROYAL, npc.getX(), npc.getY(), npc.getZ(), 0, false, 0));
		}
		else if (event.equalsIgnoreCase("spawn_nurse"))
		{
			addSpawn(NURSE, npc.getX(), npc.getY(), npc.getZ(), 0, false, 0);
		}
		return null;
	}

	public String onFactionCall (L2NpcInstance npc, L2NpcInstance caller, L2PcInstance attacker, boolean isPet) 
	{
		if (caller == null || npc == null)
			return super.onFactionCall(npc, caller, attacker, isPet);
		int npcId = npc.getNpcId();
		int callerId = caller.getNpcId();
		if (npcId == NURSE)
		{
			if (callerId == LARVA)
			{
				npc.setTarget(caller);
				npc.doCast(SkillTable.getInstance().getInfo(4020,1));
				npc.doCast(SkillTable.getInstance().getInfo(4024,1));
				return null;
			}
			else if (callerId == QUEEN)
			{
				if (npc.getTarget() != null && npc.getTarget() instanceof L2NpcInstance)
				{
					if (((L2NpcInstance) npc.getTarget()).getNpcId() == LARVA)
					{
						return null;
					}
				}
				npc.setTarget(caller);
				npc.doCast(SkillTable.getInstance().getInfo(4020,1));
				return null;
			}
		}
		return super.onFactionCall(npc, caller, attacker, isPet);
	}

	public String onAttack (L2NpcInstance npc, L2PcInstance attacker, int damage, boolean isPet)
	{
		int npcId = npc.getNpcId();
		if (npcId == NURSE)
		{
			npc.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE, null, null);
			return null;
		}
		return super.onAttack(npc, attacker, damage, isPet);
	}

	public String onKill (L2NpcInstance npc, L2PcInstance killer, boolean isPet) 
	{
		int npcId = npc.getNpcId();
		if (npcId == QUEEN)
		{
			_isAlive = false;
			npc.broadcastPacket(new PlaySound(1, "BS02_D", 1, npc.getObjectId(), npc.getX(), npc.getY(), npc.getZ()));
			cancelQuestTimers("spawn_minion");
			startQuestTimer("despawn_royals", 20000, null, null);
		}
		else if (_isAlive)
		{
			if (npcId == ROYAL)
			{
				_Minions.remove(npc);
				startQuestTimer("spawn_royal", (280+Rnd.get(40))*1000, npc, null);
			}
			else if (npcId == NURSE)
			{
				startQuestTimer("spawn_nurse", 10000, npc, null);
			}
		}
		return super.onKill(npc, killer, isPet);
	}

	public static void main(String[] args)
	{
		// now call the constructor (starts up the ai)
		new QueenAnt(-1, "queen_ant", "ai");
	}
}