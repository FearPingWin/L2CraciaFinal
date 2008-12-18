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
package com.l2jfree.gameserver.skills;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.l2jfree.Config;
import com.l2jfree.gameserver.SevenSigns;
import com.l2jfree.gameserver.SevenSignsFestival;
import com.l2jfree.gameserver.instancemanager.CastleManager;
import com.l2jfree.gameserver.instancemanager.ClanHallManager;
import com.l2jfree.gameserver.instancemanager.FortManager;
import com.l2jfree.gameserver.instancemanager.SiegeManager;
import com.l2jfree.gameserver.model.L2Character;
import com.l2jfree.gameserver.model.L2SiegeClan;
import com.l2jfree.gameserver.model.L2Skill;
import com.l2jfree.gameserver.model.L2Summon;
import com.l2jfree.gameserver.model.actor.instance.L2CubicInstance;
import com.l2jfree.gameserver.model.actor.instance.L2DoorInstance;
import com.l2jfree.gameserver.model.actor.instance.L2GrandBossInstance;
import com.l2jfree.gameserver.model.actor.instance.L2NpcInstance;
import com.l2jfree.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfree.gameserver.model.actor.instance.L2PetInstance;
import com.l2jfree.gameserver.model.actor.instance.L2PlayableInstance;
import com.l2jfree.gameserver.model.entity.Castle;
import com.l2jfree.gameserver.model.entity.ClanHall;
import com.l2jfree.gameserver.model.entity.Fort;
import com.l2jfree.gameserver.model.entity.Siege;
import com.l2jfree.gameserver.model.itemcontainer.Inventory;
import com.l2jfree.gameserver.model.zone.L2Zone;
import com.l2jfree.gameserver.network.SystemMessageId;
import com.l2jfree.gameserver.network.serverpackets.SystemMessage;
import com.l2jfree.gameserver.skills.conditions.ConditionPlayerState;
import com.l2jfree.gameserver.skills.conditions.ConditionUsingItemType;
import com.l2jfree.gameserver.skills.conditions.ConditionPlayerState.CheckPlayerState;
import com.l2jfree.gameserver.skills.funcs.Func;
import com.l2jfree.gameserver.templates.item.L2Armor;
import com.l2jfree.gameserver.templates.skills.L2SkillType;
import com.l2jfree.gameserver.templates.chars.L2PcTemplate;
import com.l2jfree.gameserver.templates.item.L2Weapon;
import com.l2jfree.gameserver.templates.item.L2WeaponType;
import com.l2jfree.gameserver.util.Util;
import com.l2jfree.tools.random.Rnd;

/**
 * Global calculations, can be modified by server admins
 */
public final class Formulas
{
	/** Regen Task period */
	protected static final Log		_log					= LogFactory.getLog(L2Character.class.getName());
	private static final int		HP_REGENERATE_PERIOD	= 3000;											// 3 secs

	public static int				MAX_STAT_VALUE			= 100;

	private static final double[]	STRCompute				= new double[]
															{ 1.036, 34.845 };									//{1.016, 28.515}; for C1
	private static final double[]	INTCompute				= new double[]
															{ 1.020, 31.375 };									//{1.020, 31.375}; for C1
	private static final double[]	DEXCompute				= new double[]
															{ 1.009, 19.360 };									//{1.009, 19.360}; for C1
	private static final double[]	WITCompute				= new double[]
															{ 1.050, 20.000 };									//{1.050, 20.000}; for C1
	private static final double[]	CONCompute				= new double[]
															{ 1.030, 27.632 };									//{1.015, 12.488}; for C1
	private static final double[]	MENCompute				= new double[]
															{ 1.010, -0.060 };									//{1.010, -0.060}; for C1

	protected static final double[]	WITbonus				= new double[MAX_STAT_VALUE];
	protected static final double[]	MENbonus				= new double[MAX_STAT_VALUE];
	protected static final double[]	INTbonus				= new double[MAX_STAT_VALUE];
	protected static final double[]	STRbonus				= new double[MAX_STAT_VALUE];
	protected static final double[]	DEXbonus				= new double[MAX_STAT_VALUE];
	protected static final double[]	CONbonus				= new double[MAX_STAT_VALUE];

	protected static final double[]	sqrtMENbonus			= new double[MAX_STAT_VALUE];
	protected static final double[]	sqrtCONbonus			= new double[MAX_STAT_VALUE];

	// These values are 100% matching retail tables, no need to change and no need add 
	// calculation into the stat bonus when accessing (not efficient),
	// better to have everything precalculated and use values directly (saves CPU)
	static
	{
		for (int i = 0; i < STRbonus.length; i++)
			STRbonus[i] = Math.floor(Math.pow(STRCompute[0], i - STRCompute[1]) * 100 + .5d) / 100;
		for (int i = 0; i < INTbonus.length; i++)
			INTbonus[i] = Math.floor(Math.pow(INTCompute[0], i - INTCompute[1]) * 100 + .5d) / 100;
		for (int i = 0; i < DEXbonus.length; i++)
			DEXbonus[i] = Math.floor(Math.pow(DEXCompute[0], i - DEXCompute[1]) * 100 + .5d) / 100;
		for (int i = 0; i < WITbonus.length; i++)
			WITbonus[i] = Math.floor(Math.pow(WITCompute[0], i - WITCompute[1]) * 100 + .5d) / 100;
		for (int i = 0; i < CONbonus.length; i++)
			CONbonus[i] = Math.floor(Math.pow(CONCompute[0], i - CONCompute[1]) * 100 + .5d) / 100;
		for (int i = 0; i < MENbonus.length; i++)
			MENbonus[i] = Math.floor(Math.pow(MENCompute[0], i - MENCompute[1]) * 100 + .5d) / 100;

		// precompute  square root values
		for (int i = 0; i < sqrtCONbonus.length; i++)
			sqrtCONbonus[i] = Math.sqrt(CONbonus[i]);
		for (int i = 0; i < sqrtMENbonus.length; i++)
			sqrtMENbonus[i] = Math.sqrt(MENbonus[i]);
	}

	static class FuncAddLevel3 extends Func
	{
		static final FuncAddLevel3[]	_instancies	= new FuncAddLevel3[Stats.NUM_STATS];

		static Func getInstance(Stats stat)
		{
			int pos = stat.ordinal();
			if (_instancies[pos] == null)
				_instancies[pos] = new FuncAddLevel3(stat);
			return _instancies[pos];
		}

		private FuncAddLevel3(Stats pStat)
		{
			super(pStat, 0x10, null);
		}

		@Override
		public void calc(Env env)
		{
			env.value += env.player.getLevel() / 3.0;
		}
	}

	static class FuncMultLevelMod extends Func
	{
		static final FuncMultLevelMod[]	_instancies	= new FuncMultLevelMod[Stats.NUM_STATS];

		static Func getInstance(Stats stat)
		{

			int pos = stat.ordinal();
			if (_instancies[pos] == null)
				_instancies[pos] = new FuncMultLevelMod(stat);
			return _instancies[pos];
		}

		private FuncMultLevelMod(Stats pStat)
		{
			super(pStat, 0x20, null);
		}

		@Override
		public void calc(Env env)
		{
			env.value *= env.player.getLevelMod();
		}
	}

	static class FuncMultRegenResting extends Func
	{
		static final FuncMultRegenResting[]	_instancies	= new FuncMultRegenResting[Stats.NUM_STATS];

		/**
		 * Return the Func object corresponding to the state concerned.<BR><BR>
		 */
		static Func getInstance(Stats stat)
		{
			int pos = stat.ordinal();

			if (_instancies[pos] == null)
				_instancies[pos] = new FuncMultRegenResting(stat);

			return _instancies[pos];
		}

		/**
		 * Constructor of the FuncMultRegenResting.<BR><BR>
		 */
		private FuncMultRegenResting(Stats pStat)
		{
			super(pStat, 0x20, null);
			setCondition(new ConditionPlayerState(CheckPlayerState.RESTING, true));
		}

		/**
		 * Calculate the modifier of the state concerned.<BR><BR>
		 */
		@Override
		public void calc(Env env)
		{
			if (!cond.test(env))
				return;

			env.value *= 1.45;
		}
	}

	static class FuncPAtkMod extends Func
	{
		static final FuncPAtkMod	_fpa_instance	= new FuncPAtkMod();

		static Func getInstance()
		{
			return _fpa_instance;
		}

		private FuncPAtkMod()
		{
			super(Stats.POWER_ATTACK, 0x30, null);
		}

		@Override
		public void calc(Env env)
		{
			env.value *= STRbonus[env.player.getStat().getSTR()] * env.player.getLevelMod();
		}
	}

	static class FuncMAtkMod extends Func
	{
		static final FuncMAtkMod	_fma_instance	= new FuncMAtkMod();

		static Func getInstance()
		{
			return _fma_instance;
		}

		private FuncMAtkMod()
		{
			super(Stats.MAGIC_ATTACK, 0x20, null);
		}

		@Override
		public void calc(Env env)
		{
			double intb = INTbonus[env.player.getINT()];
			double lvlb = env.player.getLevelMod();
			env.value *= (lvlb * lvlb) * (intb * intb);
		}
	}

	static class FuncMDefMod extends Func
	{
		static final FuncMDefMod	_fmm_instance	= new FuncMDefMod();

		static Func getInstance()
		{
			return _fmm_instance;
		}

		private FuncMDefMod()
		{
			super(Stats.MAGIC_DEFENCE, 0x20, null);
		}

		@Override
		public void calc(Env env)
		{
			if (env.player instanceof L2PcInstance)
			{
				L2PcInstance p = (L2PcInstance) env.player;
				if (p.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LFINGER) != null)
					env.value -= 5;
				if (p.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RFINGER) != null)
					env.value -= 5;
				if (p.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LEAR) != null)
					env.value -= 9;
				if (p.getInventory().getPaperdollItem(Inventory.PAPERDOLL_REAR) != null)
					env.value -= 9;
				if (p.getInventory().getPaperdollItem(Inventory.PAPERDOLL_NECK) != null)
					env.value -= 13;
			}
			env.value *= MENbonus[env.player.getStat().getMEN()] * env.player.getLevelMod();
		}
	}

	static class FuncPDefMod extends Func
	{
		static final FuncPDefMod	_fmm_instance	= new FuncPDefMod();

		static Func getInstance()
		{
			return _fmm_instance;
		}

		private FuncPDefMod()
		{
			super(Stats.POWER_DEFENCE, 0x20, null);
		}

		@Override
		public void calc(Env env)
		{
			if (env.player instanceof L2PcInstance)
			{
				L2PcInstance p = (L2PcInstance) env.player;
				boolean hasMagePDef = (p.getClassId().isMage() || p.getClassId().getId() == 0x31); // orc mystics are a special case
				if (p.getInventory().getPaperdollItem(Inventory.PAPERDOLL_HEAD) != null)
					env.value -= 12;
				if (p.getInventory().getPaperdollItem(Inventory.PAPERDOLL_CHEST) != null)
					env.value -= hasMagePDef ? 15 : 31;
				if (p.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LEGS) != null)
					env.value -= hasMagePDef ? 8 : 18;
				if (p.getInventory().getPaperdollItem(Inventory.PAPERDOLL_GLOVES) != null)
					env.value -= 8;
				if (p.getInventory().getPaperdollItem(Inventory.PAPERDOLL_FEET) != null)
					env.value -= 7;
			}
			env.value *= env.player.getLevelMod();
		}
	}

	static class FuncGatesPDefMod extends Func
	{
		static final FuncGatesPDefMod _fmm_instance = new FuncGatesPDefMod();

		static Func getInstance()
		{
			return _fmm_instance;
		}

		private FuncGatesPDefMod()
		{
			super(Stats.POWER_DEFENCE, 0x20, null);
		}

		@Override
		public void calc(Env env)
		{
			if (SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_STRIFE) == SevenSigns.CABAL_DAWN)
				env.value *= Config.ALT_SIEGE_DAWN_GATES_PDEF_MULT; 
			else if (SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_STRIFE) == SevenSigns.CABAL_DUSK)
				env.value *= Config.ALT_SIEGE_DUSK_GATES_PDEF_MULT;
		}
	}

	static class FuncGatesMDefMod extends Func
	{
		static final FuncGatesMDefMod _fmm_instance = new FuncGatesMDefMod();

		static Func getInstance()
		{
			return _fmm_instance;
		}

		private FuncGatesMDefMod()
		{
			super(Stats.MAGIC_DEFENCE, 0x20, null);
		}

		@Override
		public void calc(Env env)
		{
			if (SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_STRIFE) == SevenSigns.CABAL_DAWN)
				env.value *= Config.ALT_SIEGE_DAWN_GATES_MDEF_MULT;
			else if (SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_STRIFE) == SevenSigns.CABAL_DUSK)
				env.value *= Config.ALT_SIEGE_DUSK_GATES_MDEF_MULT;
		}
	}

	static class FuncBowAtkRange extends Func
	{
		private static final FuncBowAtkRange	_fbarInstance	= new FuncBowAtkRange();

		static Func getInstance()
		{
			return _fbarInstance;
		}

		private FuncBowAtkRange()
		{
			super(Stats.POWER_ATTACK_RANGE, 0x10, null);
			setCondition(new ConditionUsingItemType(L2WeaponType.BOW.mask()));
		}

		@Override
		public void calc(Env env)
		{
			// default is 40 and with bow should be 500
			if (!cond.test(env))
				return;
			env.value += 460;
		}
	}

	static class FuncCrossBowAtkRange extends Func
	{
		private static final FuncCrossBowAtkRange	_fcb_instance	= new FuncCrossBowAtkRange();

		static Func getInstance()
		{
			return _fcb_instance;
		}

		private FuncCrossBowAtkRange()
		{
			super(Stats.POWER_ATTACK_RANGE, 0x10, null);
			setCondition(new ConditionUsingItemType(L2WeaponType.CROSSBOW.mask()));
		}

		@Override
		public void calc(Env env)
		{
			if (!cond.test(env))
				return;
			// default is 40 and with crossbow should be 400
			env.value += 360;
		}
	}

	static class FuncAtkAccuracy extends Func
	{
		static final FuncAtkAccuracy	_faaInstance	= new FuncAtkAccuracy();

		static Func getInstance()
		{
			return _faaInstance;
		}

		private FuncAtkAccuracy()
		{
			super(Stats.ACCURACY_COMBAT, 0x10, null);
		}

		@Override
		public void calc(Env env)
		{
			L2Character p = env.player;
			//[Square(DEX)]*6 + lvl + weapon hitbonus;
			env.value += Math.sqrt(p.getStat().getDEX()) * 6;
			env.value += p.getLevel();
			if (p instanceof L2Summon)
				env.value += (p.getLevel() < 60) ? 4 : 5;
			if (p.getLevel() > 77)
				env.value += (p.getLevel() - 77);
			if (p.getLevel() > 69)
				env.value += (p.getLevel() - 69);
		}
	}

	static class FuncAtkEvasion extends Func
	{
		static final FuncAtkEvasion	_faeInstance	= new FuncAtkEvasion();

		static Func getInstance()
		{
			return _faeInstance;
		}

		private FuncAtkEvasion()
		{
			super(Stats.EVASION_RATE, 0x10, null);
		}

		@Override
		public void calc(Env env)
		{
			L2Character p = env.player;
			//[Square(DEX)]*6 + lvl;
			env.value += Math.sqrt(p.getStat().getDEX()) * 6;
			env.value += p.getLevel();
			if (p.getLevel() > 77)
				env.value += (p.getLevel() - 77);
			if (p.getLevel() > 69)
				env.value += (p.getLevel() - 69);
		}
	}

	static class FuncAtkCritical extends Func
	{
		static final FuncAtkCritical	_facInstance	= new FuncAtkCritical();

		static Func getInstance()
		{
			return _facInstance;
		}

		private FuncAtkCritical()
		{
			super(Stats.CRITICAL_RATE, 0x09, null);
		}

		@Override
		public void calc(Env env)
		{
			L2Character p = env.player;
			if (p instanceof L2Summon)
				env.value = 40;
			else if (p instanceof L2PcInstance && p.getActiveWeaponInstance() == null)
				env.value = 40;
			else
			{
				env.value *= DEXbonus[p.getStat().getDEX()];
				env.value *= 10;
			}
			env.baseValue = env.value;
		}
	}

	static class FuncMAtkCritical extends Func
	{
		static final FuncMAtkCritical	_fac_instance	= new FuncMAtkCritical();

		static Func getInstance()
		{
			return _fac_instance;
		}

		private FuncMAtkCritical()
		{
			super(Stats.MCRITICAL_RATE, 0x30, null);
		}

		@Override
		public void calc(Env env)
		{
			L2Character p = env.player;
			if (p instanceof L2Summon)
				env.value = 8;
			else if (p instanceof L2PcInstance && p.getActiveWeaponInstance() == null)
				env.value = 8;
			else
				env.value *= WITbonus[p.getStat().getWIT()];
		}
	}

	static class FuncMoveSpeed extends Func
	{
		static final FuncMoveSpeed	_fmsInstance	= new FuncMoveSpeed();

		static Func getInstance()
		{
			return _fmsInstance;
		}

		private FuncMoveSpeed()
		{
			super(Stats.RUN_SPEED, 0x30, null);
		}

		@Override
		public void calc(Env env)
		{
			L2PcInstance p = (L2PcInstance) env.player;
			env.value *= DEXbonus[p.getStat().getDEX()];
		}
	}

	static class FuncPAtkSpeed extends Func
	{
		static final FuncPAtkSpeed	_fasInstance	= new FuncPAtkSpeed();

		static Func getInstance()
		{
			return _fasInstance;
		}

		private FuncPAtkSpeed()
		{
			super(Stats.POWER_ATTACK_SPEED, 0x20, null);
		}

		@Override
		public void calc(Env env)
		{
			L2PcInstance p = (L2PcInstance) env.player;
			env.value *= DEXbonus[p.getStat().getDEX()];
		}
	}

	static class FuncMAtkSpeed extends Func
	{
		static final FuncMAtkSpeed	_fasInstance	= new FuncMAtkSpeed();

		static Func getInstance()
		{
			return _fasInstance;
		}

		private FuncMAtkSpeed()
		{
			super(Stats.MAGIC_ATTACK_SPEED, 0x20, null);
		}

		@Override
		public void calc(Env env)
		{
			L2PcInstance p = (L2PcInstance) env.player;
			env.value *= WITbonus[p.getStat().getWIT()];
		}
	}

	static class FuncMaxLoad extends Func
	{
		static final FuncMaxLoad	_fmsInstance	= new FuncMaxLoad();

		static Func getInstance()
		{
			return _fmsInstance;
		}

		private FuncMaxLoad()
		{
			super(Stats.MAX_LOAD, 0x30, null);
		}

		@Override
		public void calc(Env env)
		{
			L2PcInstance p = (L2PcInstance) env.player;
			env.value *= CONbonus[p.getStat().getCON()];
		}
	}

	static class FuncHennaSTR extends Func
	{
		static final FuncHennaSTR	_fhInstance	= new FuncHennaSTR();

		static Func getInstance()
		{
			return _fhInstance;
		}

		private FuncHennaSTR()
		{
			super(Stats.STAT_STR, 0x10, null);
		}

		@Override
		public void calc(Env env)
		{
			//          L2PcTemplate t = (L2PcTemplate)env._player.getTemplate();
			L2PcInstance pc = (L2PcInstance) env.player;
			if (pc != null)
				env.value += pc.getHennaStatSTR();
		}
	}

	static class FuncHennaDEX extends Func
	{
		static final FuncHennaDEX	_fhInstance	= new FuncHennaDEX();

		static Func getInstance()
		{
			return _fhInstance;
		}

		private FuncHennaDEX()
		{
			super(Stats.STAT_DEX, 0x10, null);
		}

		@Override
		public void calc(Env env)
		{
			//          L2PcTemplate t = (L2PcTemplate)env._player.getTemplate();
			L2PcInstance pc = (L2PcInstance) env.player;
			if (pc != null)
				env.value += pc.getHennaStatDEX();
		}
	}

	static class FuncHennaINT extends Func
	{
		static final FuncHennaINT	_fhInstance	= new FuncHennaINT();

		static Func getInstance()
		{
			return _fhInstance;
		}

		private FuncHennaINT()
		{
			super(Stats.STAT_INT, 0x10, null);
		}

		@Override
		public void calc(Env env)
		{
			//          L2PcTemplate t = (L2PcTemplate)env._player.getTemplate();
			L2PcInstance pc = (L2PcInstance) env.player;
			if (pc != null)
				env.value += pc.getHennaStatINT();
		}
	}

	static class FuncHennaMEN extends Func
	{
		static final FuncHennaMEN	_fhInstance	= new FuncHennaMEN();

		static Func getInstance()
		{
			return _fhInstance;
		}

		private FuncHennaMEN()
		{
			super(Stats.STAT_MEN, 0x10, null);
		}

		@Override
		public void calc(Env env)
		{
			//          L2PcTemplate t = (L2PcTemplate)env._player.getTemplate();
			L2PcInstance pc = (L2PcInstance) env.player;
			if (pc != null)
				env.value += pc.getHennaStatMEN();
		}
	}

	static class FuncHennaCON extends Func
	{
		static final FuncHennaCON	_fhInstance	= new FuncHennaCON();

		static Func getInstance()
		{
			return _fhInstance;
		}

		private FuncHennaCON()
		{
			super(Stats.STAT_CON, 0x10, null);
		}

		@Override
		public void calc(Env env)
		{
			//          L2PcTemplate t = (L2PcTemplate)env._player.getTemplate();
			L2PcInstance pc = (L2PcInstance) env.player;
			if (pc != null)
				env.value += pc.getHennaStatCON();
		}
	}

	static class FuncHennaWIT extends Func
	{
		static final FuncHennaWIT	_fhInstance	= new FuncHennaWIT();

		static Func getInstance()
		{
			return _fhInstance;
		}

		private FuncHennaWIT()
		{
			super(Stats.STAT_WIT, 0x10, null);
		}

		@Override
		public void calc(Env env)
		{
			//          L2PcTemplate t = (L2PcTemplate)env._player.getTemplate();
			L2PcInstance pc = (L2PcInstance) env.player;
			if (pc != null)
				env.value += pc.getHennaStatWIT();
		}
	}

	static class FuncMaxHpAdd extends Func
	{
		static final FuncMaxHpAdd	_fmhaInstance	= new FuncMaxHpAdd();

		static Func getInstance()
		{
			return _fmhaInstance;
		}

		private FuncMaxHpAdd()
		{
			super(Stats.MAX_HP, 0x10, null);
		}

		@Override
		public void calc(Env env)
		{
			L2PcTemplate t = (L2PcTemplate) env.player.getTemplate();
			int lvl = env.player.getLevel() - t.getClassBaseLevel();
			double hpmod = t.getLvlHpMod() * lvl;
			double hpmax = (t.getLvlHpAdd() + hpmod) * lvl;
			double hpmin = (t.getLvlHpAdd() * lvl) + hpmod;
			env.value += (hpmax + hpmin) / 2;
		}
	}

	static class FuncMaxHpMul extends Func
	{
		static final FuncMaxHpMul	_fmhmInstance	= new FuncMaxHpMul();

		static Func getInstance()
		{
			return _fmhmInstance;
		}

		private FuncMaxHpMul()
		{
			super(Stats.MAX_HP, 0x20, null);
		}

		@Override
		public void calc(Env env)
		{
			L2PcInstance p = (L2PcInstance) env.player;
			env.value *= CONbonus[p.getStat().getCON()];
		}
	}

	static class FuncMaxCpAdd extends Func
	{
		static final FuncMaxCpAdd	_fmcaInstance	= new FuncMaxCpAdd();

		static Func getInstance()
		{
			return _fmcaInstance;
		}

		private FuncMaxCpAdd()
		{
			super(Stats.MAX_CP, 0x10, null);
		}

		@Override
		public void calc(Env env)
		{
			L2PcTemplate t = (L2PcTemplate) env.player.getTemplate();
			int lvl = env.player.getLevel() - t.getClassBaseLevel();
			double cpmod = t.getLvlCpMod() * lvl;
			double cpmax = (t.getLvlCpAdd() + cpmod) * lvl;
			double cpmin = (t.getLvlCpAdd() * lvl) + cpmod;
			env.value += (cpmax + cpmin) / 2;
		}
	}

	static class FuncMaxCpMul extends Func
	{
		static final FuncMaxCpMul	_fmcmInstance	= new FuncMaxCpMul();

		static Func getInstance()
		{
			return _fmcmInstance;
		}

		private FuncMaxCpMul()
		{
			super(Stats.MAX_CP, 0x20, null);
		}

		@Override
		public void calc(Env env)
		{
			L2PcInstance p = (L2PcInstance) env.player;
			env.value *= CONbonus[p.getStat().getCON()];
		}
	}

	static class FuncMaxMpAdd extends Func
	{
		static final FuncMaxMpAdd	_fmmaInstance	= new FuncMaxMpAdd();

		static Func getInstance()
		{
			return _fmmaInstance;
		}

		private FuncMaxMpAdd()
		{
			super(Stats.MAX_MP, 0x10, null);
		}

		@Override
		public void calc(Env env)
		{
			L2PcTemplate t = (L2PcTemplate) env.player.getTemplate();
			int lvl = env.player.getLevel() - t.getClassBaseLevel();
			double mpmod = t.getLvlMpMod() * lvl;
			double mpmax = (t.getLvlMpAdd() + mpmod) * lvl;
			double mpmin = (t.getLvlMpAdd() * lvl) + mpmod;
			env.value += (mpmax + mpmin) / 2;
		}
	}

	static class FuncMaxMpMul extends Func
	{
		static final FuncMaxMpMul	_fmmmInstance	= new FuncMaxMpMul();

		static Func getInstance()
		{
			return _fmmmInstance;
		}

		private FuncMaxMpMul()
		{
			super(Stats.MAX_MP, 0x20, null);
		}

		@Override
		public void calc(Env env)
		{
			L2PcInstance p = (L2PcInstance) env.player;
			env.value *= MENbonus[p.getStat().getMEN()];
		}
	}

	private static final Formulas	_instance	= new Formulas();

	public static Formulas getInstance()
	{
		return _instance;
	}

	private Formulas()
	{
	}

	/**
	 * Return the period between 2 regenerations task (3s for L2Character, 5 min for L2DoorInstance).<BR><BR>
	 */
	public int getRegeneratePeriod(L2Character cha)
	{
		if (cha instanceof L2DoorInstance)
			return HP_REGENERATE_PERIOD * 100; // 5 mins

		return HP_REGENERATE_PERIOD; // 3s
	}

	/**
	 * Return the standard NPC Calculator set containing ACCURACY_COMBAT and EVASION_RATE.<BR><BR>
	 *
	 * <B><U> Concept</U> :</B><BR><BR>
	 * A calculator is created to manage and dynamically calculate the effect of a character property (ex : MAX_HP, REGENERATE_HP_RATE...).
	 * In fact, each calculator is a table of Func object in which each Func represents a mathematic function : <BR><BR>
	 *
	 * FuncAtkAccuracy -> Math.sqrt(_player.getDEX())*6+_player.getLevel()<BR><BR>
	 *
	 * To reduce cache memory use, L2NPCInstances who don't have skills share the same Calculator set called <B>NPC_STD_CALCULATOR</B>.<BR><BR>
	 *
	 */
	public Calculator[] getStdNPCCalculators()
	{
		Calculator[] std = new Calculator[Stats.NUM_STATS];

		// Add the FuncAtkAccuracy to the Standard Calculator of ACCURACY_COMBAT
		std[Stats.ACCURACY_COMBAT.ordinal()] = new Calculator();
		std[Stats.ACCURACY_COMBAT.ordinal()].addFunc(FuncAtkAccuracy.getInstance());

		// Add the FuncAtkEvasion to the Standard Calculator of EVASION_RATE
		std[Stats.EVASION_RATE.ordinal()] = new Calculator();
		std[Stats.EVASION_RATE.ordinal()].addFunc(FuncAtkEvasion.getInstance());

		return std;
	}

	public Calculator[] getStdDoorCalculators()
	{
		Calculator[] std = new Calculator[Stats.NUM_STATS];

		// Add the FuncAtkAccuracy to the Standard Calculator of ACCURACY_COMBAT
		std[Stats.ACCURACY_COMBAT.ordinal()] = new Calculator();
		std[Stats.ACCURACY_COMBAT.ordinal()].addFunc(FuncAtkAccuracy.getInstance());

		// Add the FuncAtkEvasion to the Standard Calculator of EVASION_RATE
		std[Stats.EVASION_RATE.ordinal()] = new Calculator();
		std[Stats.EVASION_RATE.ordinal()].addFunc(FuncAtkEvasion.getInstance());

		//SevenSigns PDEF Modifier
		std[Stats.POWER_DEFENCE.ordinal()].addFunc(FuncGatesPDefMod.getInstance());

		//SevenSigns MDEF Modifier
		std[Stats.MAGIC_DEFENCE.ordinal()].addFunc(FuncGatesMDefMod.getInstance());

		return std;
	}

	/**
	 * Add basics Func objects to L2PcInstance and L2Summon.<BR><BR>
	 *
	 * <B><U> Concept</U> :</B><BR><BR>
	 * A calculator is created to manage and dynamically calculate the effect of a character property (ex : MAX_HP, REGENERATE_HP_RATE...).
	 * In fact, each calculator is a table of Func object in which each Func represents a mathematic function : <BR><BR>
	 *
	 * FuncAtkAccuracy -> Math.sqrt(_player.getDEX())*6+_player.getLevel()<BR><BR>
	 *
	 * @param cha L2PcInstance or L2Summon that must obtain basic Func objects
	 */
	public void addFuncsToNewCharacter(L2Character cha)
	{
		if (cha instanceof L2PcInstance)
		{
			cha.addStatFunc(FuncMaxHpAdd.getInstance());
			cha.addStatFunc(FuncMaxHpMul.getInstance());
			cha.addStatFunc(FuncMaxCpAdd.getInstance());
			cha.addStatFunc(FuncMaxCpMul.getInstance());
			cha.addStatFunc(FuncMaxMpAdd.getInstance());
			cha.addStatFunc(FuncMaxMpMul.getInstance());
			//cha.addStatFunc(FuncMultRegenResting.getInstance(Stats.REGENERATE_HP_RATE));
			//cha.addStatFunc(FuncMultRegenResting.getInstance(Stats.REGENERATE_CP_RATE));
			//cha.addStatFunc(FuncMultRegenResting.getInstance(Stats.REGENERATE_MP_RATE));
			cha.addStatFunc(FuncBowAtkRange.getInstance());
			cha.addStatFunc(FuncCrossBowAtkRange.getInstance());
			//cha.addStatFunc(FuncMultLevelMod.getInstance(Stats.POWER_ATTACK));
			//cha.addStatFunc(FuncMultLevelMod.getInstance(Stats.POWER_DEFENCE));
			//cha.addStatFunc(FuncMultLevelMod.getInstance(Stats.MAGIC_DEFENCE));
			if (Config.LEVEL_ADD_LOAD)
				cha.addStatFunc(FuncMultLevelMod.getInstance(Stats.MAX_LOAD));
			cha.addStatFunc(FuncPAtkMod.getInstance());
			cha.addStatFunc(FuncMAtkMod.getInstance());
			cha.addStatFunc(FuncPDefMod.getInstance());
			cha.addStatFunc(FuncMDefMod.getInstance());
			cha.addStatFunc(FuncAtkCritical.getInstance());
			cha.addStatFunc(FuncMAtkCritical.getInstance());
			cha.addStatFunc(FuncAtkAccuracy.getInstance());
			cha.addStatFunc(FuncAtkEvasion.getInstance());
			cha.addStatFunc(FuncPAtkSpeed.getInstance());
			cha.addStatFunc(FuncMAtkSpeed.getInstance());
			cha.addStatFunc(FuncMoveSpeed.getInstance());
			cha.addStatFunc(FuncMaxLoad.getInstance());

			cha.addStatFunc(FuncHennaSTR.getInstance());
			cha.addStatFunc(FuncHennaDEX.getInstance());
			cha.addStatFunc(FuncHennaINT.getInstance());
			cha.addStatFunc(FuncHennaMEN.getInstance());
			cha.addStatFunc(FuncHennaCON.getInstance());
			cha.addStatFunc(FuncHennaWIT.getInstance());
		}
		else if (cha instanceof L2PetInstance)
		{
			cha.addStatFunc(FuncPAtkMod.getInstance());
			cha.addStatFunc(FuncMAtkMod.getInstance());
			cha.addStatFunc(FuncPDefMod.getInstance());
			cha.addStatFunc(FuncMDefMod.getInstance());
		}
		else if (cha instanceof L2Summon)
		{
			//cha.addStatFunc(FuncMultRegenResting.getInstance(Stats.REGENERATE_HP_RATE));
			//cha.addStatFunc(FuncMultRegenResting.getInstance(Stats.REGENERATE_MP_RATE));
			cha.addStatFunc(FuncAtkCritical.getInstance());
			cha.addStatFunc(FuncMAtkCritical.getInstance());
			cha.addStatFunc(FuncAtkAccuracy.getInstance());
			cha.addStatFunc(FuncAtkEvasion.getInstance());
		}

	}

	/**
	 * Calculate the HP regen rate (base + modifiers).<BR><BR>
	 */
	public final double calcHpRegen(L2Character cha)
	{
		double init = cha.getTemplate().getBaseHpReg();
		double hpRegenMultiplier;
		double hpRegenBonus = 0;

		if (cha.isRaid())
			hpRegenMultiplier = Config.RAID_HP_REGEN_MULTIPLIER;
		else if (cha instanceof L2PcInstance)
			hpRegenMultiplier = Config.PLAYER_HP_REGEN_MULTIPLIER;
		else
			hpRegenMultiplier = Config.NPC_HP_REGEN_MULTIPLIER;

		if (cha.isChampion())
			hpRegenMultiplier *= Config.CHAMPION_HP_REGEN;

		// [L2J_JP ADD SANDMAN]
		// The recovery power of Zaken decreases under sunlight.
		if (cha instanceof L2GrandBossInstance)
		{
			L2GrandBossInstance boss = (L2GrandBossInstance) cha;
			if ((boss.getNpcId() == 29022) && boss.isInsideZone(L2Zone.FLAG_SUNLIGHTROOM))
				hpRegenMultiplier *= 0.75;
		}

		if (cha instanceof L2PcInstance)
		{
			L2PcInstance player = (L2PcInstance) cha;

			// Calculate correct baseHpReg value for certain level of PC
			if (player.getLevel() >= 71)
				init = 8.5;
			else if (player.getLevel() >= 61)
				init = 7.5;
			else if (player.getLevel() >= 51)
				init = 6.5;
			else if (player.getLevel() >= 41)
				init = 5.5;
			else if (player.getLevel() >= 31)
				init = 4.5;
			else if (player.getLevel() >= 21)
				init = 3.5;
			else if (player.getLevel() >= 11)
				init = 2.5;
			else
				init = 2.0;

			// SevenSigns Festival modifier
			if (SevenSignsFestival.getInstance().isFestivalInProgress() && player.isFestivalParticipant())
				init *= calcFestivalRegenModifier(player);
			else
			{
				double siegeModifier = calcSiegeRegenModifer(player);
				if (siegeModifier > 0)
					init *= siegeModifier;
			}

			if (player.isInsideZone(L2Zone.FLAG_CLANHALL) && player.getClan() != null)
			{
				int clanHallIndex = player.getClan().getHasHideout();
				if (clanHallIndex > 0)
				{
					ClanHall clansHall = ClanHallManager.getInstance().getClanHallById(clanHallIndex);
					if (clansHall != null)
						if (clansHall.getFunction(ClanHall.FUNC_RESTORE_HP) != null)
							hpRegenMultiplier *= 1 + clansHall.getFunction(ClanHall.FUNC_RESTORE_HP).getLvl() / 100;
				}
			}

			// Mother Tree effect is calculated at last
			if (player.isInsideZone(L2Zone.FLAG_MOTHERTREE))
				hpRegenBonus += 2;

			if (player.isInsideZone(L2Zone.FLAG_CASTLE) && player.getClan() != null)
			{
				int castleIndex = player.getClan().getHasCastle();
				if (castleIndex > 0)
				{
					Castle castle = CastleManager.getInstance().getCastleById(castleIndex);
					if(castle != null)
						if (castle.getFunction(Castle.FUNC_RESTORE_HP) != null)
							hpRegenMultiplier *= 1 + castle.getFunction(Castle.FUNC_RESTORE_HP).getLvl() / 100;
				}
			}

			if (player.isInsideZone(L2Zone.FLAG_FORT) && player.getClan() != null)
			{
				int fortIndex = player.getClan().getHasFort();
				if (fortIndex > 0)
				{
					Fort fort = FortManager.getInstance().getFortById(fortIndex);
					if (fort != null)
						if (fort.getFunction(Fort.FUNC_RESTORE_HP) != null)
							hpRegenMultiplier *= 1 + fort.getFunction(Fort.FUNC_RESTORE_HP).getLvl() / 100;
				}
			}

			// Calculate Movement bonus
			if (player.isSitting() && player.getLevel() < 41) // Sitting below lvl 40
			{
				init *= 1.5;
				hpRegenBonus += (40 - player.getLevel()) * 0.7;
			}
			else if (player.isSitting())
				init *= 2.5; // Sitting
			else if (player.isRunning())
				init *= 0.7; // Running
			else if (player.isMoving())
				init *= 1.1; // Walking
			else
				init *= 1.5; // Staying

			// Add CON bonus
			init *= cha.getLevelMod() * CONbonus[cha.getStat().getCON()];
		}

		if (init < 1)
			init = 1;

		return cha.calcStat(Stats.REGENERATE_HP_RATE, init, null, null) * hpRegenMultiplier + hpRegenBonus;
	}

	/**
	 * Calculate the MP regen rate (base + modifiers).<BR><BR>
	 */
	public final double calcMpRegen(L2Character cha)
	{
		double init = cha.getTemplate().getBaseMpReg();
		double mpRegenMultiplier;
		double mpRegenBonus = 0;

		if (cha.isRaid())
			mpRegenMultiplier = Config.RAID_MP_REGEN_MULTIPLIER;
		else if (cha instanceof L2PcInstance)
			mpRegenMultiplier = Config.PLAYER_MP_REGEN_MULTIPLIER;
		else
			mpRegenMultiplier = Config.NPC_MP_REGEN_MULTIPLIER;

		if (cha instanceof L2PcInstance)
		{
			L2PcInstance player = (L2PcInstance) cha;

			// Calculate correct baseMpReg value for certain level of PC
			if (player.getLevel() >= 71)
				init = 3.0;
			else if (player.getLevel() >= 61)
				init = 2.7;
			else if (player.getLevel() >= 51)
				init = 2.4;
			else if (player.getLevel() >= 41)
				init = 2.1;
			else if (player.getLevel() >= 31)
				init = 1.8;
			else if (player.getLevel() >= 21)
				init = 1.5;
			else if (player.getLevel() >= 11)
				init = 1.2;
			else
				init = 0.9;

			// SevenSigns Festival modifier
			if (SevenSignsFestival.getInstance().isFestivalInProgress() && player.isFestivalParticipant())
				init *= calcFestivalRegenModifier(player);

			// Mother Tree effect is calculated at last
			if (player.isInsideZone(L2Zone.FLAG_MOTHERTREE))
				mpRegenBonus += 2;

			if (player.isInsideZone(L2Zone.FLAG_CASTLE) && player.getClan() != null)
			{
				int castleIndex = player.getClan().getHasCastle();
				if (castleIndex > 0)
				{
					Castle castle = CastleManager.getInstance().getCastleById(castleIndex);
					if(castle != null)
						if (castle.getFunction(Castle.FUNC_RESTORE_MP) != null)
							mpRegenMultiplier *= 1+ castle.getFunction(Castle.FUNC_RESTORE_MP).getLvl()/100;
				}
			}

			if (player.isInsideZone(L2Zone.FLAG_FORT) && player.getClan() != null)
			{
				int fortIndex = player.getClan().getHasFort();
				if (fortIndex > 0)
				{
					Fort fort = FortManager.getInstance().getFortById(fortIndex);
					if(fort != null)
						if (fort.getFunction(Fort.FUNC_RESTORE_MP) != null)
							mpRegenMultiplier *= 1+ fort.getFunction(Fort.FUNC_RESTORE_MP).getLvl()/100;
				}
			}

			if (player.isInsideZone(L2Zone.FLAG_CLANHALL) && player.getClan() != null)
			{
				int clanHallIndex = player.getClan().getHasHideout();
				if (clanHallIndex > 0)
				{
					ClanHall clansHall = ClanHallManager.getInstance().getClanHallById(clanHallIndex);
					if (clansHall != null)
						if (clansHall.getFunction(ClanHall.FUNC_RESTORE_MP) != null)
							mpRegenMultiplier *= 1 + clansHall.getFunction(ClanHall.FUNC_RESTORE_MP).getLvl() / 100;
				}
			}

			// Calculate Movement bonus
			if (player.isSitting())
				init *= 2.5; // Sitting.
			else if (player.isRunning())
				init *= 0.7; // Running
			else if (player.isMoving())
				init *= 1.1; // Walking
			else
				init *= 1.5; // Staying

			// Add MEN bonus
			init *= cha.getLevelMod() * MENbonus[cha.getStat().getMEN()];
		}

		if (init < 1)
			init = 1;

		return cha.calcStat(Stats.REGENERATE_MP_RATE, init, null, null) * mpRegenMultiplier + mpRegenBonus;
	}

	/**
	 * Calculate the CP regen rate (base + modifiers).<BR><BR>
	 */
	public final double calcCpRegen(L2Character cha)
	{
		double init = cha.getTemplate().getBaseHpReg();
		double cpRegenMultiplier = Config.PLAYER_CP_REGEN_MULTIPLIER;
		double cpRegenBonus = 0;

		if (cha instanceof L2PcInstance)
		{
			L2PcInstance player = (L2PcInstance) cha;

			// Calculate correct baseHpReg value for certain level of PC
			init += (player.getLevel() > 10) ? ((player.getLevel() - 1) / 10.0) : 0.5;

			// Calculate Movement bonus
			if (player.isSitting())
				init *= 1.5; // Sitting
			else if (!player.isMoving())
				init *= 1.1; // Staying
			else if (player.isRunning())
				init *= 0.7; // Running
		}
		else
		{
			// Calculate Movement bonus
			if (!cha.isMoving())
				init *= 1.1; // Staying
			else if (cha.isRunning())
				init *= 0.7; // Running
		}

		// Apply CON bonus
		init *= cha.getLevelMod() * CONbonus[cha.getStat().getCON()];
		if (init < 1)
			init = 1;

		return cha.calcStat(Stats.REGENERATE_CP_RATE, init, null, null) * cpRegenMultiplier + cpRegenBonus;
	}

	@SuppressWarnings("deprecation")
	public final double calcFestivalRegenModifier(L2PcInstance activeChar)
	{
		final int[] festivalInfo = SevenSignsFestival.getInstance().getFestivalForPlayer(activeChar);
		final int oracle = festivalInfo[0];
		final int festivalId = festivalInfo[1];
		int[] festivalCenter;

		// If the player isn't found in the festival, leave the regen rate as it is.
		if (festivalId < 0)
			return 0;

		// Retrieve the X and Y coords for the center of the festival arena the player is in.
		if (oracle == SevenSigns.CABAL_DAWN)
			festivalCenter = SevenSignsFestival.FESTIVAL_DAWN_PLAYER_SPAWNS[festivalId];
		else
			festivalCenter = SevenSignsFestival.FESTIVAL_DUSK_PLAYER_SPAWNS[festivalId];

		// Check the distance between the player and the player spawn point, in the center of the arena.
		double distToCenter = activeChar.getDistance(festivalCenter[0], festivalCenter[1]);

		if (_log.isDebugEnabled())
			_log.info("Distance: " + distToCenter + ", RegenMulti: " + (distToCenter * 2.5) / 50);

		return 1.0 - (distToCenter * 0.0005); // Maximum Decreased Regen of ~ -65%;
	}

	public final double calcSiegeRegenModifer(L2PcInstance activeChar)
	{
		if (activeChar == null || activeChar.getClan() == null)
			return 0;

		Siege siege = SiegeManager.getInstance().getSiege(activeChar);
		if (siege == null || !siege.getIsInProgress())
			return 0;

		L2SiegeClan siegeClan = siege.getAttackerClan(activeChar.getClan().getClanId());
		if (siegeClan == null || siegeClan.getFlag().size() == 0 || !Util.checkIfInRange(200, activeChar, siegeClan.getFlag().get(0), true))
			return 0;

		return 1.5; // If all is true, then modifer will be 50% more
	}

	/** Calculate blow damage based on cAtk */
	public double calcBlowDamage(L2Character attacker, L2Character target, L2Skill skill, byte shld, boolean ss)
	{
		double power = skill.getPower();
		double damage = attacker.getPAtk(target);
		double defence = target.getPDef(attacker);
		if (ss)
			damage *= 2.;
		switch(shld)
		{
			case 1:
				defence += target.getShldDef();
				break;
			case 2: // perfect block
				return 1;
		}
		if (ss && skill.getSSBoost() > 0)
			power *= skill.getSSBoost();

		damage = (attacker.calcStat(Stats.CRITICAL_DAMAGE, (damage+power), target, skill)
				+ (attacker.calcStat(Stats.CRITICAL_DAMAGE_ADD, 0, target, skill) * 6.5))
				* (target.calcStat(Stats.CRIT_VULN, target.getTemplate().baseCritVuln, target, skill));

		// get the natural vulnerability for the template
		if (target instanceof L2NpcInstance)
		{
			damage *= ((L2NpcInstance) target).getTemplate().getVulnerability(Stats.DAGGER_WPN_VULN);
		}

		// get the vulnerability for the instance due to skills (buffs, passives, toggles, etc)
		damage = target.calcStat(Stats.DAGGER_WPN_VULN, damage, target, null);
		damage *= 70. / defence;
		damage += Rnd.nextDouble() * attacker.getRandomDamage(target);

		// Dmg bonusses in PvP fight
		if((attacker instanceof L2PlayableInstance)
				&& (target instanceof L2PlayableInstance))
		{
			// Removed skill == null check since it cannot be null at this 
			// position or method throws NullPOinterException a way before 
			damage *= attacker.calcStat(Stats.PVP_PHYS_SKILL_DMG, 1, null, null);
		}

		if (target instanceof L2PlayableInstance) //aura flare de-buff, etc
			damage *= skill.getPvpMulti();
		return damage < 1 ? 1. : damage;
	}

	/** Calculated damage caused by ATTACK of attacker on target,
	 * called separatly for each weapon, if dual-weapon is used.
	 *
	 * @param attacker player or NPC that makes ATTACK
	 * @param target player or NPC, target of ATTACK
	 * @param miss one of ATTACK_XXX constants
	 * @param crit if the ATTACK have critical success
	 * @param dual if dual weapon is used
	 * @param ss if weapon item was charged by soulshot
	 * @return damage points
	 */
	public final double calcPhysDam(L2Character attacker, L2Character target, L2Skill skill, byte shld, boolean crit, boolean dual, boolean ss)
	{
		boolean transformed = false;
		if (attacker instanceof L2PcInstance)
		{
			L2PcInstance pcInst = (L2PcInstance) attacker;
			if (pcInst.isGM() && pcInst.getAccessLevel() < Config.GM_CAN_GIVE_DAMAGE)
				return 0;
			transformed = pcInst.isTransformed();
		}

		double damage = attacker.getPAtk(target);
		double defence = target.getPDef(attacker);

		switch (shld)
		{
			case 1:
			{
				if (!Config.ALT_GAME_SHIELD_BLOCKS)
					defence += target.getShldDef();
				break;
			}
			case 2: // perfect block
				return 1.;
		}

		if (ss)
			damage *= 2;
		if (skill != null)
		{
			double skillpower = skill.getPower();
			if (skill.getSkillType() == L2SkillType.FATALCOUNTER)
			{
				skillpower *= (3.5 * (1 - attacker.getStatus().getCurrentHp() / attacker.getMaxHp()));
			}

			float ssboost = skill.getSSBoost();
			if (ssboost <= 0)
				damage += skillpower;
			else if (ssboost > 0)
			{
				if (ss)
				{
					skillpower *= ssboost;
					damage += skillpower;
				}
				else
					damage += skillpower;
			}
		}
		// In C5 summons make 10 % less dmg in PvP.
		if (attacker instanceof L2Summon && target instanceof L2PcInstance)
			damage *= 0.9;

		// defence modifier depending of the attacker weapon
		L2Weapon weapon = attacker.getActiveWeaponItem();
		Stats stat = null;
		if (weapon != null && !transformed)
		{
			switch (weapon.getItemType())
			{
			case BOW:
				stat = Stats.BOW_WPN_VULN;
				break;
			case CROSSBOW:
				stat = Stats.CROSSBOW_WPN_VULN;
				break;
			case BLUNT:
			case BIGBLUNT:
				stat = Stats.BLUNT_WPN_VULN;
				break;
			case DAGGER:
				stat = Stats.DAGGER_WPN_VULN;
				break;
			case DUAL:
				stat = Stats.DUAL_WPN_VULN;
				break;
			case DUALFIST:
				stat = Stats.DUALFIST_WPN_VULN;
				break;
			case ETC:
				stat = Stats.ETC_WPN_VULN;
				break;
			case FIST:
				stat = Stats.FIST_WPN_VULN;
				break;
			case POLE:
				stat = Stats.POLE_WPN_VULN;
				break;
			case SWORD:
				stat = Stats.SWORD_WPN_VULN;
				break;
			case BIGSWORD:
				stat = Stats.BIGSWORD_WPN_VULN;
				break;
			case ANCIENT_SWORD:
				stat = Stats.SWORD_WPN_VULN;
				break;
			case RAPIER:
				stat = Stats.DAGGER_WPN_VULN;
				break;
			}
		}

		if (crit)
			damage += attacker.getCriticalDmg(target, damage) 
			* target.calcStat(Stats.CRIT_VULN, target.getTemplate().baseCritVuln, target, skill)
			+ attacker.calcStat(Stats.CRITICAL_DAMAGE_ADD, 0, target, skill);
		
		/*if (shld && !Config.ALT_GAME_SHIELD_BLOCKS)
		{
			defence += target.getStat().getShldDef();
		}*/
		//if (!(attacker instanceof L2RaidBossInstance) && 
		/*
		if ((attacker instanceof L2NpcInstance || attacker instanceof L2SiegeGuardInstance))
		{
		    if (attacker instanceof L2RaidBossInstance) damage *= 1; // was 10 changed for temp fix
		    //          else
		    //          damage *= 2;
		    //          if (attacker instanceof L2NpcInstance || attacker instanceof L2SiegeGuardInstance){
		    //damage = damage * attacker.getSTR() * attacker.getAccuracy() * 0.05 / defence;
		    //          damage = damage * attacker.getSTR()*  (attacker.getSTR() + attacker.getLevel()) * 0.025 / defence;
		    //          damage += _rnd.nextDouble() * damage / 10 ;
		}
		*/
		//      else {
		//if (skill == null)
		damage = 70 * damage / defence;

		if (stat != null)
		{
			// get the vulnerability due to skills (buffs, passives, toggles, etc)
			damage = target.calcStat(stat, damage, target, null);
			if (target instanceof L2NpcInstance)
			{
				// get the natural vulnerability for the template
				damage *= ((L2NpcInstance) target).getTemplate().getVulnerability(stat);
			}
		}

		damage += Rnd.nextDouble() * damage / 10;
		//      damage += _rnd.nextDouble()* attacker.getRandomDamage(target);
		//      }
		if (shld > 0 && Config.ALT_GAME_SHIELD_BLOCKS)
		{
			damage -= target.getStat().getShldDef();
			if (damage < 0)
				damage = 0;
		}
		if (target instanceof L2PcInstance && weapon != null && weapon.getItemType() == L2WeaponType.DAGGER && skill != null)
		{
			L2Armor armor = ((L2PcInstance) target).getActiveChestArmorItem();
			if (armor != null)
			{
				if (((L2PcInstance) target).isWearingHeavyArmor())
					damage /= Config.ALT_DAGGER_DMG_VS_HEAVY;
				if (((L2PcInstance) target).isWearingLightArmor())
					damage /= Config.ALT_DAGGER_DMG_VS_LIGHT;
				if (((L2PcInstance) target).isWearingMagicArmor())
					damage /= Config.ALT_DAGGER_DMG_VS_ROBE;
			}
		}

		if (attacker instanceof L2NpcInstance)
		{
			//Skill Valakas
			if (((L2NpcInstance) attacker).getTemplate().getIdTemplate() == 29028)
				damage /= target.getStat().getPDefValakas(attacker);
		}
		if (target instanceof L2NpcInstance)
		{
			switch (((L2NpcInstance) target).getTemplate().getRace())
			{
			case UNDEAD:
				damage *= attacker.getStat().getPAtkUndead(target);
				break;
			case BEAST:
				damage *= attacker.getStat().getPAtkMonsters(target);
				break;
			case ANIMAL:
				damage *= attacker.getStat().getPAtkAnimals(target);
				break;
			case PLANT:
				damage *= attacker.getStat().getPAtkPlants(target);
				break;
			case DRAGON:
				damage *= attacker.getStat().getPAtkDragons(target);
				break;
			case BUG:
				damage *= attacker.getStat().getPAtkInsects(target);
				break;
			case GIANT:
				damage *= attacker.getStat().getPAtkGiants(target);
				break;
			case MAGICCREATURE:
				damage *= attacker.getStat().getPAtkMagic(target);
				break;
			default:
				// nothing
				break;
			}
			//Skill Valakas
			if (((L2NpcInstance) target).getTemplate().getIdTemplate() == 29028)
				damage *= attacker.getStat().getPAtkValakas(target);
		}
		if (attacker instanceof L2NpcInstance)
		{
			switch (((L2NpcInstance) attacker).getTemplate().getRace())
			{
			case UNDEAD:
				damage /= target.getStat().getPDefUndead(attacker);
				break;
			case BEAST:
				damage /= target.getStat().getPDefMonsters(attacker);
				break;
			case ANIMAL:
				damage /= target.getStat().getPDefAnimals(attacker);
				break;
			case PLANT:
				damage /= target.getStat().getPDefPlants(attacker);
				break;
			case DRAGON:
				damage /= target.getStat().getPDefDragons(attacker);
				break;
			case BUG:
				damage /= target.getStat().getPDefInsects(attacker);
				break;
			case GIANT:
				damage /= target.getStat().getPDefGiants(attacker);
				break;
			case MAGICCREATURE:
				damage /= target.getStat().getPDefMagic(attacker);
				break;
			default:
				// nothing
				break;
			}
		}

		if (skill != null)
		{
			if (target instanceof L2PlayableInstance) //aura flare de-buff, etc
				damage *= skill.getPvpMulti();
		}

		if (damage > 0 && damage < 1)
		{
			damage = 1;
		}
		else if (damage < 0)
		{
			damage = 0;
		}

		// Dmg bonusses in PvP fight
		if ((attacker instanceof L2PlayableInstance) && (target instanceof L2PlayableInstance))
		{
			if (skill == null)
				damage *= attacker.calcStat(Stats.PVP_PHYSICAL_DMG, 1, null, null);
			else
				damage *= attacker.calcStat(Stats.PVP_PHYS_SKILL_DMG, 1, null, null);
		}

		if (attacker instanceof L2PcInstance)
		{
			if (((L2PcInstance) attacker).getClassId().isMage())
				damage *= Config.ALT_MAGES_PHYSICAL_DAMAGE_MULTI;
			else
				damage *= Config.ALT_FIGHTERS_PHYSICAL_DAMAGE_MULTI;
		}
		else if (attacker instanceof L2Summon)
			damage *= Config.ALT_PETS_PHYSICAL_DAMAGE_MULTI;
		else if (attacker instanceof L2NpcInstance)
			damage *= Config.ALT_NPC_PHYSICAL_DAMAGE_MULTI;

		return damage;
	}

	public final double calcMagicDam(L2CubicInstance attacker, L2Character target, L2Skill skill, boolean mcrit, byte shld)
	{
		if (target.isInvul())
			return 0;

		double mAtk = attacker.getMAtk();
		double mDef = target.getMDef(attacker.getOwner(), skill);

		switch (shld)
		{
			case 1:
				mDef += target.getShldDef(); // kamael
				break;
			case 2: // perfect block
				return 1;
		}

		double damage = 91 * Math.sqrt(mAtk) / mDef * skill.getPower() * calcSkillVulnerability(target, skill);
		L2PcInstance owner = attacker.getOwner();

		// Failure calculation
		if (Config.ALT_GAME_MAGICFAILURES && !calcMagicSuccess(owner, target, skill))
		{
			if (calcMagicSuccess(owner, target, skill) && (target.getLevel() - skill.getMagicLevel()) <= 9)
			{
				if (skill.getSkillType() == L2SkillType.DRAIN)
					owner.sendPacket(new SystemMessage(SystemMessageId.DRAIN_HALF_SUCCESFUL));
				else
					owner.sendPacket(new SystemMessage(SystemMessageId.ATTACK_FAILED));
				damage /= 2;
			}
			else
			{
				SystemMessage sm = new SystemMessage(SystemMessageId.S1_WAS_UNAFFECTED_BY_S2);
				sm.addCharName(target);
				sm.addSkillName(skill);
				owner.sendPacket(sm);
				damage = 1;
			}

			if (target instanceof L2PcInstance)
			{
				if (skill.getSkillType() == L2SkillType.DRAIN)
				{
					SystemMessage sm = new SystemMessage(SystemMessageId.RESISTED_S1_DRAIN);
					sm.addPcName(owner);
					target.sendPacket(sm);
				}
				else
				{
					SystemMessage sm = new SystemMessage(SystemMessageId.RESISTED_S1_MAGIC);
					sm.addPcName(owner);
					target.sendPacket(sm);
				}
			}
		}
		else if (mcrit)
			damage *= Config.ALT_MCRIT_RATE;

		return damage;
	}

	public final double calcMagicDam(L2Character attacker, L2Character target, L2Skill skill, byte shld, boolean ss, boolean bss, boolean mcrit)
	{
		if (attacker instanceof L2PcInstance)
		{
			L2PcInstance pcInst = (L2PcInstance) attacker;
			if (pcInst.isGM() && pcInst.getAccessLevel() < Config.GM_CAN_GIVE_DAMAGE)
				return 0;
		}

		double mAtk = attacker.getMAtk(target, skill);
		double mDef = target.getMDef(attacker, skill);

		switch (shld)
		{
			case 1:
				mDef += target.getShldDef(); // kamael
				break;
			case 2: // perfect block
				return 1;
		}

		if (bss)
			mAtk *= 4;
		else if (ss)
			mAtk *= 2;

		double power = skill.getPower(attacker);
		if (skill.getSkillType() == L2SkillType.DEATHLINK)
		{
			double part = attacker.getStatus().getCurrentHp() / attacker.getMaxHp();
			/*if (part > 0.005)
				power *= (-0.45 * Math.log(part) + 1.);
			else
				power *= (-0.45 * Math.log(0.005) + 1.);*/
			power *= (Math.pow(1.7165 - part, 2) * 0.577);
		}

		double damage = 91 * Math.sqrt(mAtk) / mDef * power * calcSkillVulnerability(target, skill);

		// In C5 summons make 10 % less dmg in PvP.
		if (attacker instanceof L2Summon && target instanceof L2PcInstance)
			damage *= 0.9;

		//      if(attacker instanceof L2PcInstance && target instanceof L2PcInstance) damage *= 0.9; // PvP modifier (-10%)

		// Failure calculation
		if (Config.ALT_GAME_MAGICFAILURES && !calcMagicSuccess(attacker, target, skill))
		{
			if (attacker instanceof L2PcInstance)
			{
				if (calcMagicSuccess(attacker, target, skill) && (target.getLevel() - attacker.getLevel()) <= 9)
				{
					if (skill.getSkillType() == L2SkillType.DRAIN)
						attacker.sendPacket(new SystemMessage(SystemMessageId.DRAIN_HALF_SUCCESFUL));
					else
						attacker.sendPacket(new SystemMessage(SystemMessageId.ATTACK_FAILED));

					damage /= 2;
				}
				else
				{
					SystemMessage sm = new SystemMessage(SystemMessageId.S1_WAS_UNAFFECTED_BY_S2);
					sm.addCharName(target);
					sm.addSkillName(skill);
					attacker.sendPacket(sm);

					damage = 1;
				}
			}

			if (target instanceof L2PcInstance)
			{
				if (skill.getSkillType() == L2SkillType.DRAIN)
				{
					SystemMessage sm = new SystemMessage(SystemMessageId.RESISTED_S1_DRAIN);
					sm.addCharName(attacker);
					target.sendPacket(sm);
				}
				else
				{
					SystemMessage sm = new SystemMessage(SystemMessageId.RESISTED_S1_MAGIC);
					sm.addCharName(attacker);
					target.sendPacket(sm);
				}
			}
		}
		else if (mcrit)
			damage *= Config.ALT_MCRIT_RATE;
		damage += Rnd.nextDouble() * attacker.getRandomDamage(target);

		// Pvp bonusses for dmg
		if ((attacker instanceof L2PlayableInstance) && (target instanceof L2PlayableInstance))
		{
			//if the skill is an itemskill and ALT_ITEM_SKILLS_NOT_INFLUENCED is true do.. nothing
			if (!(skill.isItemSkill() && Config.ALT_ITEM_SKILLS_NOT_INFLUENCED))
			{
				if (skill.isMagic())
					damage *= attacker.calcStat(Stats.PVP_MAGICAL_DMG, 1, null, null);
				else
					damage *= attacker.calcStat(Stats.PVP_PHYS_SKILL_DMG, 1, null, null);
			}
		}

		if (attacker instanceof L2PcInstance)
		{
			if (((L2PcInstance) attacker).getClassId().isMage())
				damage = damage * Config.ALT_MAGES_MAGICAL_DAMAGE_MULTI;
			else
				damage = damage * Config.ALT_FIGHTERS_MAGICAL_DAMAGE_MULTI;
		}
		else if (attacker instanceof L2Summon)
			damage *= Config.ALT_PETS_MAGICAL_DAMAGE_MULTI;
		else if (attacker instanceof L2NpcInstance)
			damage *= Config.ALT_NPC_MAGICAL_DAMAGE_MULTI;

		if (target instanceof L2PlayableInstance) //aura flare de-buff, etc
			damage *= skill.getPvpMulti();

		return damage;
	}

	/** Returns true in case of critical hit */
	public final boolean calcCrit(double rate)
	{
		return rate > Rnd.get(1000);
	}

	/** Calculate value of blow success */
	public final boolean calcBlow(L2Character activeChar, L2Character target, int chance)
	{
		return activeChar.calcStat(Stats.BLOW_RATE, chance * (1.0 + (activeChar.getStat().getDEX() - 20) / 100), target, null) > Rnd.get(100);
	}

	/** Calculate value of lethal chance */
	public final double calcLethal(L2Character activeChar, L2Character target, int baseLethal, int magiclvl)
	{
		double chance = 0;
		if (magiclvl > 0)
		{
			int delta = ((magiclvl + activeChar.getLevel()) / 2) - 1 - target.getLevel();

			// delta [-3,infinite[
			if (delta >= -3)
			{
				chance = (baseLethal * ((double) activeChar.getLevel() / target.getLevel()));
			}
			// delta [-9, -3[
			else if (delta < -3 && delta >= -9)
			{
				//               baseLethal
				// chance = -1 * ----------- 
				//               (delta / 3)
				chance = (-3) * (baseLethal / (delta));
			}
			//delta ]-infinite,-9[
			else
			{
				chance = baseLethal / 15;
			}
		}
		else
		{
			chance = (baseLethal * ((double)activeChar.getLevel() / target.getLevel()));
		}
		return 10 * activeChar.calcStat(Stats.LETHAL_RATE, chance, target, null);
 	}

	public final boolean calcLethalHit(L2Character activeChar, L2Character target, L2Skill skill)
	{
		if (!target.isRaid() && !(target instanceof L2DoorInstance) && !(target instanceof L2NpcInstance && ((L2NpcInstance) target).getNpcId() == 35062))
		{
			int chance = Rnd.get(1000);
			// 2nd lethal effect activate (cp,hp to 1 or if target is npc then hp to 1)
			if (skill.getLethalChance2() > 0 && chance < calcLethal(activeChar, target, skill.getLethalChance2(), skill.getMagicLevel()))
			{
				if (target instanceof L2NpcInstance)
					target.reduceCurrentHp(target.getStatus().getCurrentHp() - 1, activeChar);
				else if (target instanceof L2PcInstance) // If is a active player set his HP and CP to 1
				{
					L2PcInstance player = (L2PcInstance) target;
					if (!player.isInvul())
					{
						player.getStatus().setCurrentHp(1);
						player.getStatus().setCurrentCp(1);
						player.sendPacket(SystemMessageId.LETHAL_STRIKE_SUCCESSFUL);
					}
				}
				activeChar.sendPacket(new SystemMessage(SystemMessageId.LETHAL_STRIKE));
			}
			else if (skill.getLethalChance1() > 0 && chance < calcLethal(activeChar, target, skill.getLethalChance1(), skill.getMagicLevel()))
			{
				if (target instanceof L2PcInstance)
				{
					L2PcInstance player = (L2PcInstance) target;
					if (!player.isInvul())
					{
						player.getStatus().setCurrentCp(1); // Set CP to 1
						player.sendPacket(SystemMessageId.CP_DISAPPEARS_WHEN_HIT_WITH_A_HALF_KILL_SKILL);
					}
				}
				else if (target instanceof L2NpcInstance) // If is a monster remove first damage and after 50% of current hp
					target.reduceCurrentHp(target.getStatus().getCurrentHp() / 2, activeChar);
				activeChar.sendPacket(SystemMessageId.HALF_KILL);
			}
			else
				return false;
		}
		else
			return false;

		return true;
	}

	/** Returns true in case of critical hit */
	public final boolean calcCrit(L2Character attacker, L2Character target, double rate)
	{
		int critHit = Rnd.get(1000);
		if (attacker instanceof L2PcInstance)
		{
			if (attacker.isBehindTarget())
				critHit = Rnd.get(700);
			else if (!attacker.isFacing(target, 60) && !attacker.isBehindTarget())
				critHit = Rnd.get(800);
			critHit = Rnd.get(900);
		}
		return rate > critHit;
	}

	public final boolean calcMCrit(double mRate)
	{
		return mRate > Rnd.get(1000);
	}

	/** Returns true in case when ATTACK is canceled due to hit */
	public final boolean calcAtkBreak(L2Character target, double dmg)
	{
		if (target.isRaid() || target.isInvul() || dmg <= 0)
			return false; // No attack break

		if (target instanceof L2PcInstance)
		{
			if (((L2PcInstance) target).getForceBuff() != null)
				return true;
		}

		double init = 0;

		if (Config.ALT_GAME_CANCEL_CAST && target.isCastingNow())
		{
			init = 15;
		}
		else if (Config.ALT_GAME_CANCEL_BOW && target.isAttackingNow()
					&& target.getActiveWeaponItem() != null && target.getActiveWeaponItem().getItemType() == L2WeaponType.BOW)
		{
			init = 15;
		}
		else
			return false; // No attack break

		// Chance of break is higher with higher dmg
		init += Math.sqrt(13 * dmg);

		// Chance is affected by target MEN
		init -= (MENbonus[target.getStat().getMEN()] * 100 - 100);

		// Calculate all modifiers for ATTACK_CANCEL
		double rate = target.calcStat(Stats.ATTACK_CANCEL, init, null, null);

		// Adjust the rate to be between 1 and 99
		if (rate > 99)
			rate = 99;
		else if (rate < 1)
			rate = 1;

		return Rnd.get(100) < rate;
	}

	/** Calculate delay (in milliseconds) before next ATTACK */
	public final int calcPAtkSpd(@SuppressWarnings("unused")
	L2Character attacker, @SuppressWarnings("unused")
	L2Character target, double atkSpd, double base)
	{
		if (attacker instanceof L2PcInstance)
			base *= Config.ALT_ATTACK_DELAY;

		if (atkSpd < 10)
			atkSpd = 10;

		return (int) (base / atkSpd);
	}

	/** Calculate delay (in milliseconds) for skills cast */
	public final int calcAtkSpd(L2Character attacker, L2Skill skill, double time)
	{
		if (skill.isItemSkill() && Config.ALT_ITEM_SKILLS_NOT_INFLUENCED)
			return (int) time;
		else if (skill.isMagic())
			return (int) (time * 333 / attacker.getMAtkSpd());
		else
			return (int) (time * 333 / attacker.getPAtkSpd());
	}

	/** Returns true if hit missed (target evaded)
	*  Formula based on http://l2p.l2wh.com/nonskillattacks.html
	*/
	public boolean calcHitMiss(L2Character attacker, L2Character target)
	{
		int delta = attacker.getAccuracy() - target.getEvasionRate(attacker);
		int chance;
		if (delta >= 10)
			chance = 980;
		else
		{
			switch (delta)
			{
			case 9:
				chance = 975;
				break;
			case 8:
				chance = 970;
				break;
			case 7:
				chance = 965;
				break;
			case 6:
				chance = 960;
				break;
			case 5:
				chance = 955;
				break;
			case 4:
				chance = 945;
				break;
			case 3:
				chance = 935;
				break;
			case 2:
				chance = 925;
				break;
			case 1:
				chance = 915;
				break;
			case 0:
				chance = 905;
				break;
			case -1:
				chance = 890;
				break;
			case -2:
				chance = 875;
				break;
			case -3:
				chance = 860;
				break;
			case -4:
				chance = 845;
				break;
			case -5:
				chance = 830;
				break;
			case -6:
				chance = 815;
				break;
			case -7:
				chance = 800;
				break;
			case -8:
				chance = 785;
				break;
			case -9:
				chance = 770;
				break;
			case -10:
				chance = 755;
				break;
			case -11:
				chance = 735;
				break;
			case -12:
				chance = 715;
				break;
			case -13:
				chance = 695;
				break;
			case -14:
				chance = 675;
				break;
			case -15:
				chance = 655;
				break;
			case -16:
				chance = 625;
				break;
			case -17:
				chance = 595;
				break;
			case -18:
				chance = 565;
				break;
			case -19:
				chance = 535;
				break;
			case -20:
				chance = 505;
				break;
			case -21:
				chance = 455;
				break;
			case -22:
				chance = 405;
				break;
			case -23:
				chance = 355;
				break;
			case -24:
				chance = 305;
				break;
			default:
				chance = 275;
			}
			if (!attacker.isInFrontOfTarget())
			{
				if (attacker.isBehindTarget())
					chance *= 1.2;
				else
					// side
					chance *= 1.1;
				if (chance > 980)
					chance = 980;
			}
		}
		return chance < Rnd.get(1000);
	}

	/**
	 * Returns:<br>
	 * 0 = shield defense doesn't succeed<br>
	 * 1 = shield defense succeed<br>
	 * 2 = perfect block<br>
	 * 
	 * @param attacker
	 * @param target
	 * @param sendSysMsg
	 * @return
	 */
	public byte calcShldUse(L2Character attacker, L2Character target, boolean sendSysMsg)
	{
		double shldRate = target.calcStat(Stats.SHIELD_RATE, 0, attacker, null) * DEXbonus[target.getStat().getDEX()];

		if (shldRate == 0.0)
			return 0;

		double shldAngle = target.calcStat(Stats.SHIELD_ANGLE, 60, null, null);

		if (shldAngle < 360 && (!target.isFacing(attacker, (int) shldAngle)))
			return 0;

		// if attacker use bow and target wear shield, shield block rate is multiplied by 1.5 (50%)
		if (attacker != null && attacker.getActiveWeaponItem() != null && attacker.getActiveWeaponItem().getItemType() == L2WeaponType.BOW)
			shldRate *= 1.5;

		byte shldSuccess = 0;

		if (shldRate > 0 && 100 - Config.ALT_PERFECT_SHLD_BLOCK < Rnd.get(100))
		{
			shldSuccess = 2;
		}
		else if (shldRate > Rnd.get(100))
		{
			shldSuccess = 1;
		}
		
		if (sendSysMsg && target instanceof L2PcInstance)
		{
			L2PcInstance enemy = (L2PcInstance)target;
			switch (shldSuccess)
			{
				case 1:
					enemy.sendPacket(new SystemMessage(SystemMessageId.SHIELD_DEFENCE_SUCCESSFULL));
					break;
				case 2:
					enemy.sendPacket(new SystemMessage(SystemMessageId.YOUR_EXCELLENT_SHIELD_DEFENSE_WAS_A_SUCCESS));
					break;
			}
		}
		
		return shldSuccess;
	}

	public byte calcShldUse(L2Character attacker, L2Character target)
	{
		return calcShldUse(attacker, target, true);
	}

	// This should be deprecated and calcSkillSuccess() should be used instead
	public boolean calcMagicAffected(L2Character actor, L2Character target, L2Skill skill)
	{
		double defence = 0;
		//FIXME: CHECK/FIX THIS FORMULA UP!!
		double attack = 0;

		if (skill.isActive() && skill.isOffensive())
			defence = target.getMDef(actor, skill);

		if (actor instanceof L2PcInstance)
			attack = 3.7 * actor.getMAtk(target, skill) * calcSkillVulnerability(target, skill);
		else
			attack = 2 * actor.getMAtk(target, skill) * calcSkillVulnerability(target, skill);

		double d = attack - defence;
		d /= attack + defence;
		d += 0.5 * Rnd.nextGaussian();
		return d > 0;
	}

	public double calcSkillVulnerability(L2Character target, L2Skill skill)
	{
		return calcSkillVulnerability(target, skill, skill.getSkillType());
	}

	public double calcSkillVulnerability(L2Character target, L2Skill skill, L2SkillType type)
	{
		double multiplier = 1; // initialize...

		// Get the skill type to calculate its effect in function of base stats
		// of the L2Character target
		if (skill != null)
		{
			// first, get the natural template vulnerability values for the target
			Stats stat = skill.getStat();
			if (stat != null)
			{
				switch (stat)
				{
				case AGGRESSION:
					multiplier *= target.getTemplate().baseAggressionVuln;
					break;
				case BLEED:
					multiplier *= target.getTemplate().baseBleedVuln;
					break;
				case POISON:
					multiplier *= target.getTemplate().basePoisonVuln;
					break;
				case STUN:
					multiplier *= target.getTemplate().baseStunVuln;
					break;
				case ROOT:
					multiplier *= target.getTemplate().baseRootVuln;
					break;
				case MOVEMENT:
					multiplier *= target.getTemplate().baseMovementVuln;
					break;
				case CONFUSION:
					multiplier *= target.getTemplate().baseConfusionVuln;
					break;
				case SLEEP:
					multiplier *= target.getTemplate().baseSleepVuln;
					break;
				case FIRE:
					multiplier *= target.getTemplate().baseFireVuln;
					break;
				case WIND:
					multiplier *= target.getTemplate().baseWindVuln;
					break;
				case WATER:
					multiplier *= target.getTemplate().baseWaterVuln;
					break;
				case EARTH:
					multiplier *= target.getTemplate().baseEarthVuln;
					break;
				case HOLY:
					multiplier *= target.getTemplate().baseHolyVuln;
					break;
				case DARK:
					multiplier *= target.getTemplate().baseDarkVuln;
					break;
				}
			}

			// Next, calculate the elemental vulnerabilities
			switch (skill.getElement())
			{
			case L2Skill.ELEMENT_EARTH:
				multiplier = target.calcStat(Stats.EARTH_VULN, multiplier, target, skill);
				break;
			case L2Skill.ELEMENT_FIRE:
				multiplier = target.calcStat(Stats.FIRE_VULN, multiplier, target, skill);
				break;
			case L2Skill.ELEMENT_WATER:
				multiplier = target.calcStat(Stats.WATER_VULN, multiplier, target, skill);
				break;
			case L2Skill.ELEMENT_WIND:
				multiplier = target.calcStat(Stats.WIND_VULN, multiplier, target, skill);
				break;
			case L2Skill.ELEMENT_HOLY:
				multiplier = target.calcStat(Stats.HOLY_VULN, multiplier, target, skill);
				break;
			case L2Skill.ELEMENT_DARK:
				multiplier = target.calcStat(Stats.DARK_VULN, multiplier, target, skill);
				break;
			}

			// Finally, calculate L2SkillType vulnerabilities
			if (type != null)
			{
				switch (type)
				{
				case BLEED:
					multiplier = target.calcStat(Stats.BLEED_VULN, multiplier, target, null);
					break;
				case POISON:
					multiplier = target.calcStat(Stats.POISON_VULN, multiplier, target, null);
					break;
				case STUN:
					multiplier = target.calcStat(Stats.STUN_VULN, multiplier, target, null);
					break;
				case PARALYZE:
					multiplier = target.calcStat(Stats.PARALYZE_VULN, multiplier, target, null);
					break;
				case ROOT:
					multiplier = target.calcStat(Stats.ROOT_VULN, multiplier, target, null);
					break;
				case SLEEP:
					multiplier = target.calcStat(Stats.SLEEP_VULN, multiplier, target, null);
					break;
				case MUTE:
				case FEAR:
				case BETRAY:
				case AGGREDUCE_CHAR:
					multiplier = target.calcStat(Stats.DERANGEMENT_VULN, multiplier, target, null);
					break;
				case CONFUSION:
				case CONFUSE_MOB_ONLY:
					multiplier = target.calcStat(Stats.CONFUSION_VULN, multiplier, target, null);
					break;
				case DEBUFF:
				case WEAKNESS:
					multiplier = target.calcStat(Stats.DEBUFF_VULN, multiplier, target, null);
					break;
				}
			}
		}
		return multiplier;
	}

	public double calcSkillStatModifier(L2SkillType type, L2Character target)
	{
		double multiplier = 1;
		if (type == null)
			return multiplier;

		try
		{
			switch (type)
			{
			case STUN:
			case BLEED:
				multiplier = 2 - sqrtCONbonus[target.getStat().getCON()];
				break;
			case POISON:
			case SLEEP:
			case DEBUFF:
			case WEAKNESS:
			case ERASE:
			case ROOT:
			case MUTE:
			case FEAR:
			case BETRAY:
			case CONFUSION:
			case CONFUSE_MOB_ONLY:
			case AGGREDUCE_CHAR:
			case PARALYZE:
				multiplier = 2 - sqrtMENbonus[target.getStat().getMEN()];
				break;
			default:
				return multiplier;
			}
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			_log.warn("Character "+target.getName()+" has been set (by a GM?) a MEN or CON stat value out of accepted range");
		}
		if (multiplier < 0)
			multiplier = 0;
		return multiplier;
	}

	public boolean calcSkillSuccess(L2Character attacker, L2Character target, L2Skill skill, byte shld, boolean ss, boolean sps, boolean bss)
	{
		if (shld == 2) // perfect block
		{
			return false;
		}

		if (skill.isMagic() && target.isPreventedFromReceivingBuffs())
			return false;

		int value = (int) skill.getPower();
		int lvlDepend = skill.getLevelDepend();

		L2SkillType type = skill.getSkillType();

		if (type == L2SkillType.PDAM || type == L2SkillType.MDAM || type == L2SkillType.DRAIN || type == L2SkillType.WEAPON_SA)
		{
			value = skill.getEffectPower();
			type = skill.getEffectType();
		}

		// FIXME: Skills should be checked to be able to remove this dirty check
		if (type == null)
		{
			if (_log.isDebugEnabled())
				_log.debug("Skill ID: " + skill.getId() + " hasn't got definied type!");

			if (skill.getSkillType() == L2SkillType.PDAM)
				type = L2SkillType.STUN;
			else if (skill.getSkillType() == L2SkillType.MDAM /*|| type == L2SkillType.DRAIN*/) //FIXME: ???
				type = L2SkillType.PARALYZE;
		}

		if (value == 0)
		{
			if (_log.isDebugEnabled())
				_log.debug("Skill ID: " + skill.getId() + " hasn't got definied power!");
			value = 20; //To avoid unbalanced overpowered skills...
		}

		if (lvlDepend == 0)
		{
			if (_log.isDebugEnabled())
				_log.debug("Skill ID: " + skill.getId() + " hasn't got definied lvlDepend!");
			lvlDepend = 1; //To avoid unbalanced overpowered skills...
		}

		//FIXME: Temporary fix for NPC skills with MagicLevel not set
		// int lvlmodifier = (skill.getMagicLevel() - target.getLevel()) * lvlDepend;
		// int lvlmodifier = lvlDepend * ((skill.getMagicLevel() > 0 ? skill.getMagicLevel() : attacker.getLevel()) - target.getLevel());
		double statmodifier = calcSkillStatModifier(type, target);
		double resmodifier = calcSkillVulnerability(target, skill, type);

		int ssmodifier = 100;
		if (bss)
			ssmodifier = 150;
		else if (sps || ss)
			ssmodifier = 125;

		// Calculate BaseRate.
		int rate = (int) (value * statmodifier);// + lvlmodifier);

		// Add Matk/Mdef Bonus
		if (skill.isMagic())
		{
			int shldDef = (shld == 1 ? target.getShldDef() : 0);
			rate += (int) (Math.pow((double) attacker.getMAtk(target, skill) / (target.getMDef(attacker, skill) + shldDef), 0.1) * 100) - 100;
		}

		// Add Bonus for Sps/SS
		if (ssmodifier != 100)
		{
			if (rate > 10000 / (100 + ssmodifier))
				rate = 100 - (100 - rate) * 100 / ssmodifier;
			else
				rate = rate * ssmodifier / 100;
		}

		//lvl modifier.
		if (lvlDepend > 0)
		{
			double delta = 0;
			int attackerLvlmod = attacker.getLevel();
			int targetLvlmod = target.getLevel();
					
			if (attackerLvlmod >= 70)
				attackerLvlmod = ((attackerLvlmod - 69) * 2) + 70;
			if (targetLvlmod >= 70)
				targetLvlmod = ((targetLvlmod - 69) * 2) + 70;
					
			if (skill.getMagicLevel() == 0)
				delta = attackerLvlmod - targetLvlmod;
			else
				delta = ((skill.getMagicLevel() + attackerLvlmod) / 2) - targetLvlmod;

			double deltamod = 1;

			if (delta < -3)
			{
				if (delta <= -20)
					deltamod = 0.05;
				else
				{
					deltamod = 1 - ((-1) * (delta / 20));
					if (deltamod >= 1)
						deltamod = 0.05;
				}
			}
			else
				deltamod = 1 + ((delta + 3) / 75);

			if (deltamod < 0)
				deltamod *= -1;

			rate *= deltamod;
		}

		if (rate > 99)
			rate = 99;
		else if (rate < 1)
			rate = 1;

		//Finaly apply resists.
		rate *= resmodifier;

		if (_log.isDebugEnabled())
			_log.debug(skill.getName() + ": " + value + ", " + statmodifier + ", " + resmodifier + ", "
					+ ((int) (Math.pow((double) attacker.getMAtk(target, skill) / target.getMDef(attacker, skill), 0.2) * 100) - 100) + ", " + ssmodifier
					+ " ==> " + rate);
		return (Rnd.get(100) <= rate);
	}

	public boolean calcCubicSkillSuccess(L2CubicInstance attacker, L2Character target, L2Skill skill, byte shld)
	{
		if (shld == 2) // perfect block
		{
			return false;
		}

		L2SkillType type = skill.getSkillType();

		if (target.isRaid()
				&& (type == L2SkillType.CONFUSION || type == L2SkillType.MUTE || type == L2SkillType.PARALYZE
						|| type == L2SkillType.ROOT || type == L2SkillType.FEAR || type == L2SkillType.SLEEP
						|| type == L2SkillType.STUN || type == L2SkillType.DEBUFF || type == L2SkillType.AGGDEBUFF))
			return false; // these skills should not work on RaidBoss

		// if target reflect this skill then the effect will fail
		if (target.reflectSkill(skill))
			return false;

		int value = (int) skill.getPower();
		int lvlDepend = skill.getLevelDepend();

		if (type == L2SkillType.PDAM || type == L2SkillType.MDAM) // For additional effects on PDAM skills (like STUN, SHOCK,...)
		{
			value = skill.getEffectPower();
			type = skill.getEffectType();
		}

		// TODO: Temporary fix for skills with Power = 0 or LevelDepend not set
		if (value == 0)
			value = (type == L2SkillType.PARALYZE) ? 50 : (type == L2SkillType.FEAR) ? 40 : 80;
		if (lvlDepend == 0)
			lvlDepend = (type == L2SkillType.PARALYZE || type == L2SkillType.FEAR) ? 1 : 2;

		// TODO: Temporary fix for NPC skills with MagicLevel not set
		// int lvlmodifier = (skill.getMagicLevel() - target.getLevel()) * lvlDepend;
		int lvlmodifier = ((skill.getMagicLevel() > 0 ? skill.getMagicLevel() : attacker.getOwner().getLevel()) - target.getLevel()) * lvlDepend;
		double statmodifier = calcSkillStatModifier(type, target);
		double resmodifier = calcSkillVulnerability(target, skill);

		int rate = (int) ((value * statmodifier + lvlmodifier) * resmodifier);
		if (skill.isMagic())
		{
			int shldDef = (shld == 1 ? target.getShldDef() : 0);
			rate += (int) (Math.pow((double) attacker.getMAtk() / (target.getMDef(attacker.getOwner(), skill) + shldDef), 0.2) * 100) - 100;
		}

		if (rate > 99)
			rate = 99;
		else if (rate < 1)
			rate = 1;
		
		if (Config.DEVELOPER)
			_log.info(skill.getName() + ": " +
					value + ", " + statmodifier +
					", " + lvlmodifier + ", " +
					resmodifier + ", " +
					((int) (Math.pow((double) attacker.getMAtk()
							/ target.getMDef(attacker.getOwner(), skill), 0.2) * 100) - 100) + " ==> " +
							rate);
		return (Rnd.get(100) < rate);
	}

	public boolean calcMagicSuccess(L2Character attacker, L2Character target, L2Skill skill)
	{
		double lvlDifference = (target.getLevel() - (skill.getMagicLevel() > 0 ? skill.getMagicLevel() : attacker.getLevel()));
		int rate = Math.round((float) (Math.pow(1.3, lvlDifference) * 100));

		return (Rnd.get(10000) > rate);
	}

	public boolean calculateUnlockChance(L2Skill skill)
	{
		int level = skill.getLevel();
		int chance = 0;
		switch (level)
		{
		case 1:
			chance = 30;
			break;

		case 2:
			chance = 50;
			break;

		case 3:
			chance = 75;
			break;

		case 4:
		case 5:
		case 6:
		case 7:
		case 8:
		case 9:
		case 10:
		case 11:
		case 12:
		case 13:
		case 14:
			chance = 100;
			break;
		}
		if (Rnd.get(120) > chance)
		{
			return false;
		}

		return true;
	}

	public double calcManaDam(L2Character attacker, L2Character target, L2Skill skill, boolean ss, boolean bss)
	{
		//Mana Burnt = (SQR(M.Atk)*Power*(Target Max MP/97))/M.Def
		double mAtk = attacker.getMAtk(target, skill);
		double mDef = target.getMDef(attacker, skill);
		double mp = target.getMaxMp();
		if (bss)
			mAtk *= 4;
		else if (ss)
			mAtk *= 2;

		double damage = (Math.sqrt(mAtk) * skill.getPower(attacker) * (mp / 97)) / mDef;
		damage *= calcSkillVulnerability(target, skill);
		if (target instanceof L2PlayableInstance) //aura flare de-buff, etc
			damage *= skill.getPvpMulti();
		return damage;
	}

	public double calculateSkillResurrectRestorePercent(double baseRestorePercent, int casterWIT)
	{
		double restorePercent = baseRestorePercent;
		double modifier = WITbonus[casterWIT];

		if (restorePercent != 100 && restorePercent != 0)
		{

			restorePercent = baseRestorePercent * modifier;

			if (restorePercent - baseRestorePercent > 20.0)
				restorePercent = baseRestorePercent + 20.0;
		}

		if (restorePercent > 100)
			restorePercent = 100;
		if (restorePercent < baseRestorePercent)
			restorePercent = baseRestorePercent;

		return restorePercent;
	}

	public double getSTRBonus(L2Character activeChar)
	{
		return STRbonus[activeChar.getStat().getSTR()];
	}

	public boolean calcPhysicalSkillEvasion(L2Character target, L2Skill skill)
	{
		if (skill.isMagic() && skill.getSkillType() != L2SkillType.BLOW)
			return false;

		return Rnd.get(100) < target.calcStat(Stats.P_SKILL_EVASION, 0, null, skill);
	}
}
