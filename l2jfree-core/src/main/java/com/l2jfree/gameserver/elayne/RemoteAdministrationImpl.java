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
package com.l2jfree.gameserver.elayne;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import javolution.util.FastMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.l2jfree.Config;
import com.l2jfree.gameserver.Announcements;
import com.l2jfree.gameserver.Shutdown;
import com.l2jfree.gameserver.cache.HtmCache;
import com.l2jfree.gameserver.datatables.GmListTable;
import com.l2jfree.gameserver.datatables.ItemTable;
import com.l2jfree.gameserver.datatables.NpcTable;
import com.l2jfree.gameserver.datatables.SkillTable;
import com.l2jfree.gameserver.datatables.SpawnTable;
import com.l2jfree.gameserver.datatables.TeleportLocationTable;
import com.l2jfree.gameserver.instancemanager.DayNightSpawnManager;
import com.l2jfree.gameserver.instancemanager.Manager;
import com.l2jfree.gameserver.instancemanager.RaidBossSpawnManager;
import com.l2jfree.gameserver.model.L2Multisell;
import com.l2jfree.gameserver.model.L2World;
import com.l2jfree.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfree.gameserver.network.Disconnection;
import com.l2jfree.gameserver.network.SystemChatChannelId;
import com.l2jfree.gameserver.network.serverpackets.CreatureSay;

public class RemoteAdministrationImpl extends UnicastRemoteObject implements IRemoteAdministration
{
	private final static Log				_log				= LogFactory.getLog(RemoteAdministrationImpl.class.getName());

	private static final long				serialVersionUID	= -8523099127883669758L;

	private static RemoteAdministrationImpl	_instance;

	private IRemoteAdministration			obj;

	@SuppressWarnings("unused")
	private Registry						lReg;

	private String							pass;

	private int								port;

	public static RemoteAdministrationImpl getInstance()
	{
		if (_instance == null)
			try
			{
				_instance = new RemoteAdministrationImpl();
			}
			catch (RemoteException e)
			{
				_log.error("RemoteAdministrationImpl: Problems ocurred while starting RMI Server.", e);
			}
		return _instance;
	}

	public RemoteAdministrationImpl() throws RemoteException
	{
		super();
		pass = Config.RMI_SERVER_PASSWORD.toLowerCase();
		port = Config.RMI_SERVER_PORT;
	}

	public void startServer()
	{
		if (Config.ALLOW_RMI_SERVER && pass != null && !pass.isEmpty() && port != 0)
		{
			try
			{
				lReg = LocateRegistry.createRegistry(port);
				obj = new RemoteAdministrationImpl();

				Naming.rebind("//localhost:" + port + "/Elayne", obj);
				_log.info("RMI Server bound in registry: Port:" + port + ", Password: " + pass + ".");
			}
			catch (Exception e)
			{
				_log.error("RemoteAdministrationImpl error: ", e);
			}
		}
		else
			_log.info("RMI Server is currently disabled.");
	}

	/**
	 * @see com.l2jfree.gameserver.elayne.IRemoteAdministration#getOnlineUsersCount()
	 */
	public int getOnlineUsersCount(String password) throws RemoteException
	{
		if (!password.equals(pass))
			return 0;
		return L2World.getInstance().getAllPlayersCount();
	}

	/**
	 * @see com.l2jfree.gameserver.elayne.IRemoteAdministration#getPlayerInformation(java.lang.String)
	 */
	public IRemotePlayer getPlayerInformation(String password, String playerName) throws RemoteException
	{
		if (!password.equals(pass))
			return null;
		L2PcInstance player = L2World.getInstance().getPlayer(playerName);
		if (player != null)
			return new RemotePlayerImpl(player);
		return null;
	}

	/**
	 * @see com.l2jfree.gameserver.elayne.IRemoteAdministration#announceToAll(java.lang.String)
	 */
	public void announceToAll(String password, String announcement) throws RemoteException
	{
		if (!password.equals(pass))
			return;
		Announcements.getInstance().announceToAll(announcement);
	}

	/**
	 * @see com.l2jfree.gameserver.elayne.IRemoteAdministration#abortServerRestart()
	 */
	public void abortServerRestart(String password) throws RemoteException
	{
		if (password.equals(pass))
			Shutdown.getInstance().abort("127.0.0.1");
	}

	/**
	 * @see com.l2jfree.gameserver.elayne.IRemoteAdministration#kickPlayerFromServer(java.lang.String)
	 */
	public int kickPlayerFromServer(String password, String playerName) throws RemoteException
	{
		if (password.equals(pass))
		{
			L2PcInstance player = L2World.getInstance().getPlayer(playerName);
			if (player != null)
			{
				player.sendMessage("You are kicked out by a GM.");
				new Disconnection(player).defaultSequence(false);
				return 1;
			}
			return 2;
		}

		return 3;
	}

	/**
	 * @see com.l2jfree.gameserver.elayne.IRemoteAdministration#reload(int)
	 */
	public void reload(String password, int reloadProcedure) throws RemoteException
	{
		if (password.equals(pass))
		{
			switch (reloadProcedure)
			{
			case 1:
				L2Multisell.getInstance().reload();
				break;
			case 2:
				SkillTable.reload();
				break;
			case 3:
				NpcTable.getInstance().reloadAll();
				break;
			case 4:
				HtmCache.getInstance().reload();
				break;
			case 5:
				ItemTable.reload();
				break;
			case 6:
				Manager.reloadAll();
				break;
			case 7:
				break;
			case 8:
				TeleportLocationTable.getInstance().reloadAll();
				break;
			case 9:
				RaidBossSpawnManager.getInstance().cleanUp();
				DayNightSpawnManager.getInstance().cleanUp();
				L2World.getInstance().deleteVisibleNpcSpawns();
				NpcTable.getInstance().reloadAll();
				SpawnTable.getInstance().reloadAll();
				RaidBossSpawnManager.getInstance().reloadBosses();
				break;
			}
		}
	}

	/**
	 * @see com.l2jfree.gameserver.elayne.IRemoteAdministration#scheduleServerRestart(int)
	 */
	public void scheduleServerRestart(String password, int secondsUntilRestart) throws RemoteException
	{
		if (password.equals(pass))
			Shutdown.getInstance().startShutdown("127.0.0.1", secondsUntilRestart, Shutdown.ShutdownModeType.SHUTDOWN);
	}

	/**
	 * @see com.l2jfree.gameserver.elayne.IRemoteAdministration#scheduleServerShutDown(int)
	 */
	public void scheduleServerShutDown(String password, int secondsUntilShutDown) throws RemoteException
	{
		if (password.equals(pass))
			Shutdown.getInstance().startShutdown("127.0.0.1", secondsUntilShutDown, Shutdown.ShutdownModeType.RESTART);

	}

	/**
	 * @see com.l2jfree.gameserver.elayne.IRemoteAdministration#sendMessageToGms(java.lang.String)
	 */
	public int sendMessageToGms(String password, String message) throws RemoteException
	{
		if (!password.equals(pass))
			return 0;
		CreatureSay cs = new CreatureSay(0, SystemChatChannelId.Chat_Alliance, "Message From Elayne GM Tool", message);
		GmListTable.broadcastToGMs(cs);
		return GmListTable.getInstance().getAllGms(true).size();
	}

	/**
	 * @see com.l2jfree.gameserver.elayne.IRemoteAdministration#sendPrivateMessage(java.lang.String, java.lang.String)
	 */
	public int sendPrivateMessage(String password, String player, String message) throws RemoteException
	{
		if (!password.equals(pass))
			return 2;
		L2PcInstance reciever = L2World.getInstance().getPlayer(player);
		CreatureSay cs = new CreatureSay(0, SystemChatChannelId.Chat_Tell, "Elayne GM Tool MSG", message);
		if (reciever != null)
		{
			reciever.sendPacket(cs);
			return 1;
		}

		return 2;
	}

	/**
	 * @see com.l2jfree.gameserver.elayne.IRemoteAdministration#getOnlinePlayersDetails(java.lang.String)
	 */
	public FastMap<String, IRemotePlayer> getOnlinePlayersDetails(String rmiPassword) throws RemoteException
	{
		if (!rmiPassword.equals(pass))
			return null;
		return null;
	}
}
