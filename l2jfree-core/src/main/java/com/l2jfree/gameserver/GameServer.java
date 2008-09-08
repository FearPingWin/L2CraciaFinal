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
package com.l2jfree.gameserver;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Calendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mmocore.network.SelectorConfig;
import org.mmocore.network.SelectorThread;

import com.l2jfree.Config;
import com.l2jfree.L2DatabaseFactory;
import com.l2jfree.gameserver.cache.CrestCache;
import com.l2jfree.gameserver.cache.HtmCache;
import com.l2jfree.gameserver.communitybbs.Manager.ForumsBBSManager;
import com.l2jfree.gameserver.datatables.ArmorSetsTable;
import com.l2jfree.gameserver.datatables.AugmentationData;
import com.l2jfree.gameserver.datatables.BuffTemplateTable;
import com.l2jfree.gameserver.datatables.CharTemplateTable;
import com.l2jfree.gameserver.datatables.ClanTable;
import com.l2jfree.gameserver.datatables.DoorTable;
import com.l2jfree.gameserver.datatables.EventDroplist;
import com.l2jfree.gameserver.datatables.ExtractableItemsData;
import com.l2jfree.gameserver.datatables.FishTable;
import com.l2jfree.gameserver.datatables.GmListTable;
import com.l2jfree.gameserver.datatables.HennaTable;
import com.l2jfree.gameserver.datatables.HennaTreeTable;
import com.l2jfree.gameserver.datatables.HeroSkillTable;
import com.l2jfree.gameserver.datatables.ItemTable;
import com.l2jfree.gameserver.datatables.LevelUpData;
import com.l2jfree.gameserver.datatables.NobleSkillTable;
import com.l2jfree.gameserver.datatables.NpcTable;
import com.l2jfree.gameserver.datatables.NpcWalkerRoutesTable;
import com.l2jfree.gameserver.datatables.PetDataTable;
import com.l2jfree.gameserver.datatables.SkillSpellbookTable;
import com.l2jfree.gameserver.datatables.SkillTable;
import com.l2jfree.gameserver.datatables.SkillTreeTable;
import com.l2jfree.gameserver.datatables.SpawnTable;
import com.l2jfree.gameserver.datatables.StaticObjects;
import com.l2jfree.gameserver.datatables.SummonItemsData;
import com.l2jfree.gameserver.datatables.TeleportLocationTable;
import com.l2jfree.gameserver.datatables.TradeListTable;
import com.l2jfree.gameserver.elayne.RemoteAdministrationImpl;
import com.l2jfree.gameserver.geodata.GeoClient;
import com.l2jfree.gameserver.geoeditorcon.GeoEditorListener;
import com.l2jfree.gameserver.handler.AdminCommandHandler;
import com.l2jfree.gameserver.handler.ChatHandler;
import com.l2jfree.gameserver.handler.ItemHandler;
import com.l2jfree.gameserver.handler.SkillHandler;
import com.l2jfree.gameserver.handler.UserCommandHandler;
import com.l2jfree.gameserver.handler.VoicedCommandHandler;
import com.l2jfree.gameserver.idfactory.IdFactory;
import com.l2jfree.gameserver.instancemanager.AuctionManager;
import com.l2jfree.gameserver.instancemanager.BoatManager;
import com.l2jfree.gameserver.instancemanager.CastleManager;
import com.l2jfree.gameserver.instancemanager.CastleManorManager;
import com.l2jfree.gameserver.instancemanager.ClanHallManager;
import com.l2jfree.gameserver.instancemanager.CoupleManager;
import com.l2jfree.gameserver.instancemanager.CrownManager;
import com.l2jfree.gameserver.instancemanager.CursedWeaponsManager;
import com.l2jfree.gameserver.instancemanager.DayNightSpawnManager;
import com.l2jfree.gameserver.instancemanager.DimensionalRiftManager;
import com.l2jfree.gameserver.instancemanager.FactionManager;
import com.l2jfree.gameserver.instancemanager.FactionQuestManager;
import com.l2jfree.gameserver.instancemanager.FortManager;
import com.l2jfree.gameserver.instancemanager.FortSiegeManager;
import com.l2jfree.gameserver.instancemanager.FourSepulchersManager;
import com.l2jfree.gameserver.instancemanager.GrandBossSpawnManager;
import com.l2jfree.gameserver.instancemanager.InstanceManager;
import com.l2jfree.gameserver.instancemanager.IrcManager;
import com.l2jfree.gameserver.instancemanager.ItemsOnGroundManager;
import com.l2jfree.gameserver.instancemanager.MapRegionManager;
import com.l2jfree.gameserver.instancemanager.MercTicketManager;
import com.l2jfree.gameserver.instancemanager.PetitionManager;
import com.l2jfree.gameserver.instancemanager.QuestManager;
import com.l2jfree.gameserver.instancemanager.RaidBossSpawnManager;
import com.l2jfree.gameserver.instancemanager.RaidPointsManager;
import com.l2jfree.gameserver.instancemanager.SiegeManager;
import com.l2jfree.gameserver.instancemanager.TownManager;
import com.l2jfree.gameserver.instancemanager.TransformationManager;
import com.l2jfree.gameserver.instancemanager.ZoneManager;
import com.l2jfree.gameserver.instancemanager.grandbosses.AntharasManager;
import com.l2jfree.gameserver.instancemanager.grandbosses.BaiumManager;
import com.l2jfree.gameserver.instancemanager.grandbosses.BaylorManager;
import com.l2jfree.gameserver.instancemanager.grandbosses.FrintezzaManager;
import com.l2jfree.gameserver.instancemanager.grandbosses.SailrenManager;
import com.l2jfree.gameserver.instancemanager.grandbosses.ValakasManager;
import com.l2jfree.gameserver.instancemanager.grandbosses.VanHalterManager;
import com.l2jfree.gameserver.instancemanager.lastimperialtomb.LastImperialTombManager;
import com.l2jfree.gameserver.instancemanager.leaderboards.ArenaManager;
import com.l2jfree.gameserver.instancemanager.leaderboards.FishermanManager;
import com.l2jfree.gameserver.model.AutoChatHandler;
import com.l2jfree.gameserver.model.AutoSpawnHandler;
import com.l2jfree.gameserver.model.L2Manor;
import com.l2jfree.gameserver.model.L2World;
import com.l2jfree.gameserver.model.entity.Hero;
import com.l2jfree.gameserver.model.restriction.ObjectRestrictions;
import com.l2jfree.gameserver.network.L2GameClient;
import com.l2jfree.gameserver.network.L2GamePacketHandler;
import com.l2jfree.gameserver.script.faenor.FaenorScriptEngine;
import com.l2jfree.gameserver.scripting.CompiledScriptCache;
import com.l2jfree.gameserver.scripting.L2ScriptEngineManager;
import com.l2jfree.gameserver.skills.SkillsEngine;
import com.l2jfree.gameserver.taskmanager.KnownListUpdateTaskManager;
import com.l2jfree.gameserver.taskmanager.TaskManager;
import com.l2jfree.gameserver.threadmanager.DeadlockDetector;
import com.l2jfree.gameserver.threadmanager.RunnableStatsManager;
import com.l2jfree.gameserver.util.DynamicExtension;
import com.l2jfree.gameserver.util.FloodProtector;
import com.l2jfree.gameserver.util.PathCreator;
import com.l2jfree.gameserver.util.Util;
import com.l2jfree.status.Status;
import com.l2jfree.versionning.Version;

public class GameServer
{
	private static final Log					_log			= LogFactory.getLog(GameServer.class);
	private static final Calendar				_serverStarted	= Calendar.getInstance();
	private static final Version				version			= new Version();
	private static SelectorThread<L2GameClient>	_selectorThread;

	public GameServer() throws Throwable
	{
		long serverLoadStart = System.currentTimeMillis();
		Config.load();

		Util.printSection("Database");
		L2DatabaseFactory.getInstance();
		Util.printSection("Preparations");
		L2JfreeInfo.showStartupInfo();
		new PathCreator();
		Util.printSection("World");
		L2World.getInstance();
		if (Config.IS_TELNET_ENABLED)
			new Status().start();
		else
			_log.info("Telnet Server is currently disabled.");
		MapRegionManager.getInstance();
		Announcements.getInstance();
		if (!IdFactory.getInstance().isInitialized())
		{
			_log.fatal("Could not read object IDs from DB. Please Check Your Data.");
			throw new Exception("Could not initialize the ID factory");
		}
		_log.info("IdFactory: Free ObjectID's remaining: " + IdFactory.getInstance().size());
		RunnableStatsManager.getInstance();
		ThreadPoolManager.getInstance().startPurgeTask(600000L);
		if (Config.DEADLOCKCHECK_INTERVAL > 0)
			DeadlockDetector.getInstance();

		GeoClient.getInstance();

		StaticObjects.getInstance();
		GameTimeController.getInstance();
		TeleportLocationTable.getInstance();
		BoatManager.getInstance();
		InstanceManager.getInstance();
		Util.printSection("Skills");
		SkillTreeTable.getInstance();
		SkillsEngine.getInstance();
		SkillTable.getInstance();
		NobleSkillTable.getInstance();
		_log.info("NobleSkills initialized");
		HeroSkillTable.getInstance();
		_log.info("HeroSkills initialized");
		Util.printSection("Items");
		ItemTable.getInstance();
		ArmorSetsTable.getInstance();
		AugmentationData.getInstance();
		if (Config.SP_BOOK_NEEDED)
		{
			SkillSpellbookTable.getInstance();
		}
		SummonItemsData.getInstance();
		ExtractableItemsData.getInstance();
		if (Config.ALLOW_FISHING)
		{
			FishTable.getInstance();
		}
		ItemsOnGroundManager.getInstance();
		if (Config.AUTODESTROY_ITEM_AFTER > 0 || Config.HERB_AUTO_DESTROY_TIME > 0)
		{
			ItemsAutoDestroy.getInstance();
		}
		Util.printSection("Characters");
		CharTemplateTable.getInstance();
		LevelUpData.getInstance();
		HennaTable.getInstance();
		HennaTreeTable.getInstance();
		if (Config.ALLOW_WEDDING)
		{
			CoupleManager.getInstance();
		}
		CursedWeaponsManager.getInstance();
		ClanTable.getInstance();
		CrestCache.getInstance();
		Hero.getInstance();
		Util.printSection("NPCs");
		NpcTable.getInstance();
		HtmCache.getInstance();
		BuffTemplateTable.getInstance();
		if (Config.ALLOW_NPC_WALKERS)
		{
			NpcWalkerRoutesTable.getInstance().load();
		}
		PetDataTable.getInstance().loadPetsData();
		Util.printSection("Entities and zones");
		CrownManager.getInstance();
		TownManager.getInstance();
		ClanHallManager.getInstance();
		DoorTable.getInstance();
		CastleManager.getInstance();
		SiegeManager.getInstance();
		FortManager.getInstance();
		FortSiegeManager.getInstance();
		ZoneManager.getInstance();
		MercTicketManager.getInstance();
		DoorTable.getInstance().registerToClanHalls();
		Util.printSection("Quests");
		QuestManager.getInstance();
		TransformationManager.getInstance();
		Util.printSection("Events/ScriptEngine");
		try
		{
			File scripts = new File(Config.DATAPACK_ROOT.getAbsolutePath(), "data/scripts.cfg");
			L2ScriptEngineManager.getInstance().executeScriptList(scripts);
		}
		catch (IOException ioe)
		{
			_log.fatal("Failed loading scripts.cfg, no script going to be loaded");
		}
		try
		{
			CompiledScriptCache compiledScriptCache = L2ScriptEngineManager.getInstance().getCompiledScriptCache();
			if (compiledScriptCache == null)
			{
				_log.info("Compiled Scripts Cache is disabled.");
			}
			else
			{
				compiledScriptCache.purge();
				if (compiledScriptCache.isModified())
				{
					compiledScriptCache.save();
					_log.info("Compiled Scripts Cache was saved.");
				}
				else
				{
					_log.info("Compiled Scripts Cache is up-to-date.");
				}
			}

		}
		catch (IOException e)
		{
			_log.fatal("Failed to store Compiled Scripts Cache.", e);
		}

		QuestManager.getInstance().report();
		TransformationManager.getInstance().report();

		EventDroplist.getInstance();
		FaenorScriptEngine.getInstance();

		if (Config.ARENA_ENABLED)
			ArenaManager.getInstance().engineInit();
		if (Config.FISHERMAN_ENABLED)
			FishermanManager.getInstance().engineInit();
		Util.printSection("Spawns");
		SpawnTable.getInstance();
		DayNightSpawnManager.getInstance().notifyChangeMode();
		RaidBossSpawnManager.getInstance();
		GrandBossSpawnManager.getInstance();
		RaidPointsManager.getInstance();
		AutoChatHandler.getInstance();
		AutoSpawnHandler.getInstance();
		Util.printSection("Economy");
		TradeListTable.getInstance();
		CastleManorManager.getInstance();
		L2Manor.getInstance();
		AuctionManager.getInstance();
		Util.printSection("SevenSigns");
		SevenSigns.getInstance();
		SevenSignsFestival.getInstance();
		Util.printSection("Olympiad");
		Olympiad.getInstance();
		Util.printSection("Dungeons");
		DimensionalRiftManager.getInstance();
		FourSepulchersManager.getInstance().init();
		Util.printSection("Bosses");
		AntharasManager.getInstance().init();
		BaiumManager.getInstance().init();
		BaylorManager.getInstance().init();
		SailrenManager.getInstance().init();
		ValakasManager.getInstance().init();
		VanHalterManager.getInstance().init();
		LastImperialTombManager.getInstance().init();
		FrintezzaManager.getInstance().init();

		Util.printSection("Extensions");
		if (Config.FACTION_ENABLED)
		{
			Util.printSection("Factions");
			FactionManager.getInstance();
			FactionQuestManager.getInstance();
		}
		try
		{
			DynamicExtension.getInstance();
		}
		catch (Exception ex)
		{
			_log.warn("DynamicExtension could not be loaded and initialized", ex);
		}

		Util.printSection("Handlers");
		ItemHandler.getInstance();
		SkillHandler.getInstance();
		AdminCommandHandler.getInstance();
		UserCommandHandler.getInstance();
		VoicedCommandHandler.getInstance();
		ChatHandler.getInstance();

		Util.printSection("Misc");
		ObjectRestrictions.getInstance();
		TaskManager.getInstance();
		GmListTable.getInstance();
		RemoteAdministrationImpl.getInstance().startServer();
		PetitionManager.getInstance();
		if (Config.ONLINE_PLAYERS_ANNOUNCE_INTERVAL > 0)
		{
			OnlinePlayers.getInstance();
		}
		FloodProtector.getInstance();
		ForumsBBSManager.getInstance();
		KnownListUpdateTaskManager.getInstance();

		Runtime.getRuntime().addShutdownHook(Shutdown.getInstance());

		System.gc();

		Util.printSection("ServerThreads");
		LoginServerThread.getInstance().start();

		L2GamePacketHandler gph = new L2GamePacketHandler();
		SelectorConfig<L2GameClient> sc = new SelectorConfig<L2GameClient>(null, null, gph, gph);
		sc.setMaxSendPerPass(12);
		sc.setSelectorSleepTime(20);
		_selectorThread = new SelectorThread<L2GameClient>(sc, gph, gph, null);
		_selectorThread.openServerSocket(InetAddress.getByName(Config.GAMESERVER_HOSTNAME), Config.PORT_GAME);
		_selectorThread.start();

		if (Config.IRC_ENABLED)
			IrcManager.getInstance().getConnection().sendChan("GameServer Started");

		if (Config.ACCEPT_GEOEDITOR_CONN)
			GeoEditorListener.getInstance();

		Util.printSection("l2jfree");
		version.loadInformation(GameServer.class);
		_log.info("Revision: " + version.getVersionNumber());
		// _log.info("Build date: "+version.getBuildDate());
		_log.info("Compiler version: " + version.getBuildJdk());
		_log.info("Operating System: " + Util.getOSName() + " " + Util.getOSVersion() + " " + Util.getOSArch());
		_log.info("Available CPUs: " + Util.getAvailableProcessors());

		printMemUsage();
		_log.info("Maximum Numbers of Connected Players: " + Config.MAXIMUM_ONLINE_USERS);
		_log.info("Server Loaded in " + ((System.currentTimeMillis() - serverLoadStart) / 1000) + " seconds");

		Util.printSection("GameServerLog");
		if (Config.ENABLE_JYTHON_SHELL)
		{
			Util.printSection("JythonShell");
			Util.JythonShell();
		}
	}

	public static String getVersionNumber()
	{
		return version.getVersionNumber();
	}

	public static void printMemUsage()
	{
		Util.printSection("Memory");
		for (String line : Util.getMemUsage())
			_log.info(line);
	}

	public static SelectorThread<L2GameClient> getSelectorThread()
	{
		return _selectorThread;
	}

	public static Calendar getStartedTime()
	{
		return _serverStarted;
	}

	public static void main(String[] args) throws Throwable
	{
		System.setProperty("python.home", ".");
		System.setProperty("line.separator", "\r\n");

		new GameServer();
	}
}