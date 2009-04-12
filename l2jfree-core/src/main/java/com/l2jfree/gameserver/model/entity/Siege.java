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
package com.l2jfree.gameserver.model.entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;

import javolution.util.FastList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.l2jfree.Config;
import com.l2jfree.L2DatabaseFactory;
import com.l2jfree.gameserver.Announcements;
import com.l2jfree.gameserver.SevenSigns;
import com.l2jfree.gameserver.ThreadPoolManager;
import com.l2jfree.gameserver.datatables.ClanTable;
import com.l2jfree.gameserver.datatables.NpcTable;
import com.l2jfree.gameserver.idfactory.IdFactory;
import com.l2jfree.gameserver.instancemanager.MercTicketManager;
import com.l2jfree.gameserver.instancemanager.SiegeGuardManager;
import com.l2jfree.gameserver.instancemanager.SiegeManager;
import com.l2jfree.gameserver.instancemanager.TownManager;
import com.l2jfree.gameserver.instancemanager.SiegeManager.SiegeSpawn;
import com.l2jfree.gameserver.model.L2Clan;
import com.l2jfree.gameserver.model.L2Object;
import com.l2jfree.gameserver.model.L2SiegeClan;
import com.l2jfree.gameserver.model.L2Spawn;
import com.l2jfree.gameserver.model.L2World;
import com.l2jfree.gameserver.model.L2SiegeClan.SiegeClanType;
import com.l2jfree.gameserver.model.actor.instance.L2ControlTowerInstance;
import com.l2jfree.gameserver.model.actor.instance.L2NpcInstance;
import com.l2jfree.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfree.gameserver.model.mapregion.TeleportWhereType;
import com.l2jfree.gameserver.model.zone.L2SiegeZone;
import com.l2jfree.gameserver.model.zone.L2Zone;
import com.l2jfree.gameserver.network.SystemMessageId;
import com.l2jfree.gameserver.network.serverpackets.SiegeInfo;
import com.l2jfree.gameserver.network.serverpackets.SystemMessage;
import com.l2jfree.gameserver.network.serverpackets.UserInfo;
import com.l2jfree.gameserver.templates.chars.L2NpcTemplate;

public class Siege
{
    
    private final static Log _log = LogFactory.getLog(Siege.class.getName());
    
    // ==========================================================================================
    // Message to add/check
    //  id=17  msg=[Castle siege has begun.] c3_attr1=[SystemMsg_k.17]
    //  id=18  msg=[Castle siege is over.]   c3_attr1=[SystemMsg_k.18]
    //  id=288 msg=[The castle gate has been broken down.]  
    //  id=291 msg=[Clan $s1 is victorious over $s2's castle siege!]        
    //  id=292 msg=[$s1 has announced the castle siege time.]       
    //  - id=293 msg=[The registration term for $s1 has ended.]       
    //  - id=358 msg=[$s1 hour(s) until castle siege conclusion.]     
    //  - id=359 msg=[$s1 minute(s) until castle siege conclusion.]
    //  - id=360 msg=[Castle siege $s1 second(s) left!]       
    //  id=640 msg=[You have failed to refuse castle defense aid.]    
    //  id=641 msg=[You have failed to approve castle defense aid.]
    //  id=644 msg=[You are not yet registered for the castle siege.]       
    //  - id=645 msg=[Only clans with Level 4 and higher may register for a castle siege.]    
    //  id=646 msg=[You do not have the authority to modify the castle defender list.]     
    //  - id=688 msg=[The clan that owns the castle is automatically registered on the defending side.]       
    //  id=689 msg=[A clan that owns a castle cannot participate in another siege.]        
    //  id=690 msg=[You cannot register on the attacking side because you are part of an alliance with the clan that owns the castle.]     
    //  id=718 msg=[The castle gates cannot be opened and closed during a siege.]  
    //  - id=295 msg=[$s1's siege was canceled because there were no clans that participated.]        
    //  id=659 msg=[This is not the time for siege registration and so registrations cannot be accepted or rejected.]      
    //  - id=660 msg=[This is not the time for siege registration and so registration and cancellation cannot be done.]      
    //  id=663 msg=[The siege time has been declared for $s. It is not possible to change the time after a siege time has been declared. Do you want to continue?] 
    //  id=667 msg=[You are registering on the attacking side of the $s1 siege. Do you want to continue?]  
    //  id=668 msg=[You are registering on the defending side of the $s1 siege. Do you want to continue?]  
    //  id=669 msg=[You are canceling your application to participate in the $s1 siege battle. Do you want to continue?]
    //  id=707 msg=[You cannot teleport to a village that is in a siege.]  
    //  - id=711 msg=[The siege of $s1 has started.]
    //  - id=712 msg=[The siege of $s1 has finished.]
    //  id=844 msg=[The siege to conquer $s1 has begun.]    
    //  - id=845 msg=[The deadline to register for the siege of $s1 has passed.]      
    //  - id=846 msg=[The siege of $s1 has been canceled due to lack of interest.]    
    //  - id=856 msg=[The siege of $s1 has ended in a draw.]  
    //  id=285 msg=[Clan $s1 has succeeded in engraving the ruler!] 
    //  - id=287 msg=[The opponent clan has begun to engrave the ruler.]           

    public static enum TeleportWhoType {
        All, Attacker, DefenderNotOwner, Owner, Spectator
    }

    private int _controlTowerCount;
    private int _controlTowerMaxCount;

    // ===============================================================
    // Schedule task
    public class ScheduleEndSiegeTask implements Runnable
    {
        private Castle _castleInst;

        public ScheduleEndSiegeTask(Castle pCastle)
        {
            _castleInst = pCastle;
        }

        public void run()
        {
            if (!getIsInProgress()) return;

            long timeRemaining = _siegeEndDate.getTimeInMillis()
                - System.currentTimeMillis();
            if (timeRemaining > 3600000)
            {
                ThreadPoolManager.getInstance().scheduleGeneral(new ScheduleEndSiegeTask(_castleInst),
                                                                timeRemaining - 3600000); // Prepare task for 1 hr left.
            }
            else if ((timeRemaining <= 3600000) && (timeRemaining > 600000))
            {
                announceToPlayer(Math.round(timeRemaining / 60000) + " minute(s) until "
                    + getCastle().getName() + " siege conclusion.", true);
                ThreadPoolManager.getInstance().scheduleGeneral(new ScheduleEndSiegeTask(_castleInst),
                                                                timeRemaining - 600000); // Prepare task for 10 minute left.
            }
            else if ((timeRemaining <= 600000) && (timeRemaining > 300000))
            {
                announceToPlayer(Math.round(timeRemaining / 60000) + " minute(s) until "
                    + getCastle().getName() + " siege conclusion.", true);
                ThreadPoolManager.getInstance().scheduleGeneral(new ScheduleEndSiegeTask(_castleInst),
                                                                timeRemaining - 300000); // Prepare task for 5 minute left.
            }
            else if ((timeRemaining <= 300000) && (timeRemaining > 10000))
            {
                announceToPlayer(Math.round(timeRemaining / 60000) + " minute(s) until "
                    + getCastle().getName() + " siege conclusion.", true);
                ThreadPoolManager.getInstance().scheduleGeneral(new ScheduleEndSiegeTask(_castleInst),
                                                                timeRemaining - 10000); // Prepare task for 10 seconds count down
            }
            else if ((timeRemaining <= 10000) && (timeRemaining > 0))
            {
                announceToPlayer(getCastle().getName() + " siege "
                    + Math.round(timeRemaining / 1000) + " second(s) left!", true);
                ThreadPoolManager.getInstance().scheduleGeneral(new ScheduleEndSiegeTask(_castleInst),
                                                                timeRemaining); // Prepare task for second count down
            }
            else
            {
                _castleInst.getSiege().endSiege();
            }
        }
    }

    public class ScheduleStartSiegeTask implements Runnable
    {
        private Castle _castleInst;

        public ScheduleStartSiegeTask(Castle pCastle)
        {
            _castleInst = pCastle;
        }

        public void run()
        {
            if (_scheduledStartSiegeTask != null)
                _scheduledStartSiegeTask.cancel(false);
            if (getIsInProgress())
                return;

            if (!getIsTimeRegistrationOver())
            {
                long regTimeRemaining = getTimeRegistrationOverDate().getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
                if (regTimeRemaining > 0)
                {
                    _scheduledStartSiegeTask = ThreadPoolManager.getInstance().scheduleGeneral(new ScheduleStartSiegeTask(_castleInst), regTimeRemaining);
                    return;
                }

                endTimeRegistration(true);
            }

            long timeRemaining = getSiegeDate().getTimeInMillis()
                - System.currentTimeMillis();
            if (timeRemaining > 86400000)
            {
                _scheduledStartSiegeTask = ThreadPoolManager.getInstance().scheduleGeneral(new ScheduleStartSiegeTask(_castleInst),
                                                                timeRemaining - 86400000); // Prepare task for 24 before siege start to end registration
            }
            else if ((timeRemaining <= 86400000) && (timeRemaining > 13600000))
            {
                announceToPlayer("The registration term for " + getCastle().getName()
                    + " has ended.", false);
                _isRegistrationOver = true;
                clearSiegeWaitingClan();
                _scheduledStartSiegeTask = ThreadPoolManager.getInstance().scheduleGeneral(new ScheduleStartSiegeTask(_castleInst),
                                                                timeRemaining - 13600000); // Prepare task for 1 hr left before siege start.
            }
            else if ((timeRemaining <= 13600000) && (timeRemaining > 600000))
            {
                announceToPlayer(Math.round(timeRemaining / 60000) + " minute(s) until "
                    + getCastle().getName() + " siege begin.", false);
                _scheduledStartSiegeTask = ThreadPoolManager.getInstance().scheduleGeneral(new ScheduleStartSiegeTask(_castleInst),
                                                                timeRemaining - 600000); // Prepare task for 10 minute left.
            }
            else if ((timeRemaining <= 600000) && (timeRemaining > 300000))
            {
                announceToPlayer(Math.round(timeRemaining / 60000) + " minute(s) until "
                    + getCastle().getName() + " siege begin.", false);
                _scheduledStartSiegeTask = ThreadPoolManager.getInstance().scheduleGeneral(new ScheduleStartSiegeTask(_castleInst),
                                                                timeRemaining - 300000); // Prepare task for 5 minute left.
            }
            else if ((timeRemaining <= 300000) && (timeRemaining > 10000))
            {
                announceToPlayer(Math.round(timeRemaining / 60000) + " minute(s) until "
                    + getCastle().getName() + " siege begin.", false);
                _scheduledStartSiegeTask = ThreadPoolManager.getInstance().scheduleGeneral(new ScheduleStartSiegeTask(_castleInst),
                                                                timeRemaining - 10000); // Prepare task for 10 seconds count down
            }
            else if ((timeRemaining <= 10000) && (timeRemaining > 0))
            {
                announceToPlayer(getCastle().getName() + " siege "
                    + Math.round(timeRemaining / 1000) + " second(s) to start!", false);
                _scheduledStartSiegeTask = ThreadPoolManager.getInstance().scheduleGeneral(new ScheduleStartSiegeTask(_castleInst),
                                                                timeRemaining); // Prepare task for second count down
            }
            else
            {
                _castleInst.getSiege().startSiege();
            }
        }
    }

    // =========================================================
    // Data Field
    // Attacker and Defender
    private FastList<L2SiegeClan> _attackerClans = new FastList<L2SiegeClan>(); // L2SiegeClan

    private FastList<L2SiegeClan> _defenderClans = new FastList<L2SiegeClan>(); // L2SiegeClan
    private FastList<L2SiegeClan> _defenderWaitingClans = new FastList<L2SiegeClan>(); // L2SiegeClan

    // Castle setting
    private FastList<L2ControlTowerInstance> _controlTowers = new FastList<L2ControlTowerInstance>();
    
    private Castle _castle;
    private boolean _isInProgress = false;
    private boolean _isNormalSide = true; // true = Atk is Atk, false = Atk is Def
    protected boolean _isRegistrationOver = false;
    protected Calendar _siegeEndDate;
    private SiegeGuardManager _siegeGuardManager;
    protected ScheduledFuture<?> _scheduledStartSiegeTask = null;
    
    // =========================================================
    // Constructor
    public Siege(Castle castle)
    {
        _castle = castle;
        _siegeGuardManager = new SiegeGuardManager(getCastle());
        startAutoTask();
    }

    // =========================================================
    // Siege phases
    /**
     * When siege ends<BR><BR>
     */
    public void endSiege()
    {
        if (getIsInProgress())
        {
            announceToPlayer("The siege of " + getCastle().getName() + " has finished!", false);

            if (getCastle().getOwnerId() <= 0)
                announceToPlayer("The siege of " + getCastle().getName() + " has ended in a draw.", false);

            removeFlags(); // Removes all flags. Note: Remove flag before teleporting players
            teleportPlayer(Siege.TeleportWhoType.Attacker, TeleportWhereType.Town); // Teleport to the second closest town
            teleportPlayer(Siege.TeleportWhoType.DefenderNotOwner, TeleportWhereType.Town); // Teleport to the second closest town
            teleportPlayer(Siege.TeleportWhoType.Spectator, TeleportWhereType.Town); // Teleport to the second closest town
            _isInProgress = false; // Flag so that siege instance can be started
            updatePlayerSiegeStateFlags(true);
            getZone().updateSiegeStatus();
            saveCastleSiege(); // Save castle specific data
            clearSiegeClan(); // Clear siege clan from db
            removeControlTower(); // Remove all control tower from this castle
            _siegeGuardManager.unspawnSiegeGuard(); // Remove all spawned siege guard from this castle
            if (getCastle().getOwnerId() > 0) _siegeGuardManager.removeMercs();
            getCastle().spawnDoor(); // Respawn door to castle
        }
    }

    private void removeDefender(L2SiegeClan sc)
    {
        if (sc != null) getDefenderClans().remove(sc);
    }
    
    private void removeAttacker(L2SiegeClan sc)
    {
        if (sc != null) getAttackerClans().remove(sc);
    }
    
    private void addDefender(L2SiegeClan sc, SiegeClanType type)
    {
        if(sc == null) return; 
        sc.setType(type);
        getDefenderClans().add(sc);
    }
    
    private void addAttacker(L2SiegeClan sc)
    {
        if(sc == null) return; 
        sc.setType(SiegeClanType.ATTACKER);
        getAttackerClans().add(sc);
    }
    
    /**
     * When control of castle changed during siege<BR><BR>
     */
    public void midVictory()
    {
        if (getIsInProgress()) // Siege still in progress
        {
            if (getCastle().getOwnerId() > 0) _siegeGuardManager.removeMercs(); // Remove all merc entry from db

            if (getDefenderClans().size() == 0 && // If defender doesn't exist (Pc vs Npc)
                getAttackerClans().size() == 1 // Only 1 attacker
            )
            {
                L2SiegeClan sc_newowner = getAttackerClan(getCastle().getOwnerId());
                removeAttacker(sc_newowner);
                addDefender(sc_newowner, SiegeClanType.OWNER);
                endSiege();
                return;
            }
            if (getCastle().getOwnerId() > 0)
            {
                int allyId = ClanTable.getInstance().getClan(getCastle().getOwnerId()).getAllyId();
                if (getDefenderClans().size() == 0) // If defender doesn't exist (Pc vs Npc)
                // and only an alliance attacks
                {
                    // The player's clan is in an alliance
                    if (allyId != 0)
                    {
                        boolean allinsamealliance = true;
                        for (L2SiegeClan sc : getAttackerClans())
                        {
                            if(sc != null) 
                            {
                                if(ClanTable.getInstance().getClan(sc.getClanId()).getAllyId() != allyId)
                                    allinsamealliance = false;
                            }
                        }
                        if(allinsamealliance) 
                        {
                            L2SiegeClan sc_newowner = getAttackerClan(getCastle().getOwnerId());
                            removeAttacker(sc_newowner);
                            addDefender(sc_newowner, SiegeClanType.OWNER);
                            endSiege();
                            return;
                        }
                    }
                }
                
                for (L2SiegeClan sc : getDefenderClans())
                {
                    if(sc != null)
                    {
                        removeDefender(sc);
                        addAttacker(sc);
                    }
                }
                
                L2SiegeClan sc_newowner = getAttackerClan(getCastle().getOwnerId());
                removeAttacker(sc_newowner);
                addDefender(sc_newowner, SiegeClanType.OWNER);
                
                // The player's clan is in an alliance
                if (allyId != 0)
                {
                    L2Clan[] clanList = ClanTable.getInstance().getClans();
                    
                    for (L2Clan clan : clanList)
                    {
                        if (clan.getAllyId() == allyId)
                        {
                            L2SiegeClan sc = getAttackerClan(clan.getClanId());
                            if(sc != null)
                            {
                                removeAttacker(sc);
                                addDefender(sc, SiegeClanType.DEFENDER);
                            }
                        }
                    }
                }
                teleportPlayer(Siege.TeleportWhoType.Attacker, TeleportWhereType.SiegeFlag); // Teleport to the second closest town
                teleportPlayer(Siege.TeleportWhoType.Spectator, TeleportWhereType.Town);     // Teleport to the second closest town
                
                removeDefenderFlags();       // Removes defenders' flags
                getCastle().removeUpgrade(); // Remove all castle upgrade
                getCastle().spawnDoor(true); // Respawn door to castle but make them weaker (50% hp)
                removeControlTower(); // Remove all control tower from this castle                
                _controlTowerCount = 0;//Each new siege midvictory CT are completely respawned.
                _controlTowerMaxCount = 0;
                spawnControlTower(getCastle().getCastleId());
                updatePlayerSiegeStateFlags(false);
            }
        }
    }

    /**
     * When siege starts<BR><BR>
     */
    public void startSiege()
    {
        if (!getIsInProgress())
        {
            if (getAttackerClans().size() <= 0)
            {
                SystemMessage sm;
                if (getCastle().getOwnerId() <= 0) 
                   sm = new SystemMessage(SystemMessageId.SIEGE_OF_S1_HAS_BEEN_CANCELED_DUE_TO_LACK_OF_INTEREST);
                else
                    sm = new SystemMessage(SystemMessageId.S1_SIEGE_WAS_CANCELED_BECAUSE_NO_CLANS_PARTICIPATED);
                sm.addString(getCastle().getName());
                Announcements.getInstance().announceToAll(sm);
                saveCastleSiege();
                return;
            }

            _isNormalSide = true; // Atk is now atk
            _isInProgress = true; // Flag so that same siege instance cannot be started again 
            loadSiegeClan(); // Load siege clan from db
            updatePlayerSiegeStateFlags(false);
            teleportPlayer(Siege.TeleportWhoType.Attacker, TeleportWhereType.Town); // Teleport to the closest town
            //teleportPlayer(Siege.TeleportWhoType.Spectator, MapRegionTable.TeleportWhereType.Town);      // Teleport to the second closest town
            _controlTowerCount = 0;
            _controlTowerMaxCount = 0;  
            spawnControlTower(getCastle().getCastleId()); // Spawn control tower
            getCastle().spawnDoor(); // Spawn door
            spawnSiegeGuard(); // Spawn siege guard
            MercTicketManager.getInstance().deleteTickets(getCastle().getCastleId()); // remove the tickets from the ground
            getZone().updateSiegeStatus();

            // Schedule a task to prepare auto siege end
            _siegeEndDate = Calendar.getInstance();
            _siegeEndDate.add(Calendar.MINUTE, Config.SIEGE_LENGTH_MINUTES);
            ThreadPoolManager.getInstance().scheduleGeneral(new ScheduleEndSiegeTask(getCastle()), 1000); // Prepare auto end task

            announceToPlayer("The siege of " + getCastle().getName() + " has started!", false);
        }
    }

    // =========================================================
    // Method - Public
    /**
     * Announce to player.<BR><BR>
     * @param message The String of the message to send to player
     * @param inAreaOnly The boolean flag to show message to players in area only.
     */
    public void announceToPlayer(String message, boolean inAreaOnly)
    {
        // Get all players
        for (L2PcInstance player : L2World.getInstance().getAllPlayers())
        {
            if (!inAreaOnly || (inAreaOnly && checkIfInZone(player.getX(), player.getY(), player.getZ())))
                player.sendMessage(message);
        }
    }

    public void updatePlayerSiegeStateFlags(boolean clear)
    {
        L2Clan clan;
        for(L2SiegeClan siegeclan : getAttackerClans())
        {
            clan = ClanTable.getInstance().getClan(siegeclan.getClanId());
            for (L2PcInstance member : clan.getOnlineMembers(0))
            {
                if (clear) member.setSiegeState((byte)0);
                else member.setSiegeState((byte)1);
                member.sendPacket(new UserInfo(member));
                member.revalidateZone(true);
            }
        }
        for(L2SiegeClan siegeclan : getDefenderClans())
        {
            clan = ClanTable.getInstance().getClan(siegeclan.getClanId());
            for (L2PcInstance member : clan.getOnlineMembers(0))
            {
                if (clear) member.setSiegeState((byte)0);
                else member.setSiegeState((byte)2);
                member.sendPacket(new UserInfo(member));
                member.revalidateZone(true);
            }
        }
    }

    /**
     * Approve clan as defender for siege<BR><BR>
     * @param clanId The int of player's clan id
     */
    public void approveSiegeDefenderClan(int clanId)
    {
        if (clanId <= 0) return;
        saveSiegeClan(ClanTable.getInstance().getClan(clanId), 0, true);
        loadSiegeClan();
    }

    /** Return true if object is inside the zone */
    public boolean checkIfInZone(L2Object object)
    {
        return checkIfInZone(object.getX(), object.getY(), object.getZ());
    }

    /** Return true if object is inside the zone */
    public boolean checkIfInZone(int x, int y, int z)
    {
        Town town = TownManager.getInstance().getTown(x, y, z);
        return (getIsInProgress() &&
                    (getCastle().checkIfInZone(x, y, z) || getZone().isInsideZone(x, y)
                        || (town != null && getCastle().getCastleId() == town.getCastleId())));
    }

    /**
     * Return true if clan is attacker<BR><BR>
     * @param clan The L2Clan of the player
     */
    public boolean checkIsAttacker(L2Clan clan)
    {
        return (getAttackerClan(clan) != null);
    }

    /**
     * Return true if clan is defender<BR><BR>
     * @param clan The L2Clan of the player
     */
    public boolean checkIsDefender(L2Clan clan)
    {
        return (getDefenderClan(clan) != null);
    }

    /**
     * Return true if clan is defender waiting approval<BR><BR>
     * @param clan The L2Clan of the player
     */
    public boolean checkIsDefenderWaiting(L2Clan clan)
    {
        return (getDefenderWaitingClan(clan) != null);
    }

    /** Clear all registered siege clans from database for castle */
    public void clearSiegeClan()
    {
        Connection con = null;
        try
        {
            con = L2DatabaseFactory.getInstance().getConnection(con);
            PreparedStatement statement = con.prepareStatement("DELETE FROM siege_clans WHERE castle_id=?");
            statement.setInt(1, getCastle().getCastleId());
            statement.execute();
            statement.close();

            if (getCastle().getOwnerId() > 0) 
            {
                PreparedStatement statement2 = con.prepareStatement("DELETE FROM siege_clans WHERE clan_id=?");
                statement2.setInt(1, getCastle().getOwnerId());
                statement2.execute();
                statement2.close();
            }

            getAttackerClans().clear();
            getDefenderClans().clear();
            getDefenderWaitingClans().clear();
        }
        catch (Exception e)
        {
            _log.error("Exception: clearSiegeClan(): " + e.getMessage(),e);
        }
        finally { try { if (con != null) con.close(); } catch (SQLException e) { e.printStackTrace(); } }
    }

    /** Clear all siege clans waiting for approval from database for castle */
    public void clearSiegeWaitingClan()
    {
        Connection con = null;
        try
        {
            con = L2DatabaseFactory.getInstance().getConnection(con);
            PreparedStatement statement = con.prepareStatement("DELETE FROM siege_clans WHERE castle_id=? and type = 2");
            statement.setInt(1, getCastle().getCastleId());
            statement.execute();
            statement.close();

            getDefenderWaitingClans().clear();
        }
        catch (Exception e)
        {
            _log.error("Exception: clearSiegeWaitingClan(): " + e.getMessage(),e);
        }
        finally { try { if (con != null) con.close(); } catch (SQLException e) { e.printStackTrace(); } }
    }

    /** Return list of L2PcInstance registered as attacker in the zone. */
    public FastList<L2PcInstance> getAttackersInZone()
    {
        FastList<L2PcInstance> players = new FastList<L2PcInstance>();
        L2Clan clan;
        for(L2SiegeClan siegeclan : getAttackerClans())
        {
            clan = ClanTable.getInstance().getClan(siegeclan.getClanId());
            for (L2PcInstance player : clan.getOnlineMembers(0))
            {
                if (checkIfInZone(player.getX(), player.getY(), player.getZ())) players.add(player);
            }
        }
        return players;
    }

    /** Return list of L2PcInstance registered as defender but not owner in the zone. */
    public FastList<L2PcInstance> getDefendersButNotOwnersInZone()
    {
        FastList<L2PcInstance> players = new FastList<L2PcInstance>();
        L2Clan clan;
        for(L2SiegeClan siegeclan : getDefenderClans())
        {
            clan = ClanTable.getInstance().getClan(siegeclan.getClanId());
            if (clan.getClanId() == getCastle().getOwnerId()) continue;
            for (L2PcInstance player : clan.getOnlineMembers(0))
            {
                if (checkIfInZone(player.getX(), player.getY(), player.getZ())) players.add(player);
            }
        }
        return players;
    }

    /** Return list of L2PcInstance in the zone. */
    public FastList<L2PcInstance> getPlayersInZone()
    {
        FastList<L2PcInstance> players = new FastList<L2PcInstance>();

        for (L2PcInstance player : L2World.getInstance().getAllPlayers())
        {
            // quick check from player states, which don't include siege number however
            if (!player.isInsideZone(L2Zone.FLAG_SIEGE)) continue;
            if (checkIfInZone(player.getX(), player.getY(), player.getZ())) players.add(player);
        }

        return players;
    }

    /** Return list of L2PcInstance owning the castle in the zone. */
    public FastList<L2PcInstance> getOwnersInZone()
    {
        FastList<L2PcInstance> players = new FastList<L2PcInstance>();
        L2Clan clan;
        for(L2SiegeClan siegeclan : getDefenderClans())
        {
            clan = ClanTable.getInstance().getClan(siegeclan.getClanId());
            if (clan.getClanId() != getCastle().getOwnerId()) continue;
            for (L2PcInstance player : clan.getOnlineMembers(0))
            {
                if (checkIfInZone(player.getX(), player.getY(), player.getZ())) players.add(player);
            }
        }
        return players;
    }

    /** Return list of L2PcInstance not registered as attacker or defender in the zone. */
    public FastList<L2PcInstance> getSpectatorsInZone()
    {
        FastList<L2PcInstance> players = new FastList<L2PcInstance>();

        for (L2PcInstance player : L2World.getInstance().getAllPlayers())
        {
            // quick check from player states, which don't include siege number however
            if (!player.isInsideZone(L2Zone.FLAG_SIEGE) || player.getSiegeState() != 0) continue;
            if ( checkIfInZone(player.getX(), player.getY(), player.getZ()))
                players.add(player);
        }

        return players;
    }

    /** Control Tower was skilled 
     * @param ct */
    public void killedCT(L2NpcInstance ct)
    {
        _controlTowerCount--;
        if (_controlTowerCount < 0)
            _controlTowerCount = 0;
    }

    /** Remove the flag that was killed */
    public void killedFlag(L2NpcInstance flag)
    {
        if (flag == null)
            return;
        for (L2SiegeClan clan: getAttackerClans())
        {
            if (clan.removeFlag(flag)) return;
        }
    }

    /** Display list of registered clans */
    public void listRegisterClan(L2PcInstance player)
    {
        player.sendPacket(new SiegeInfo(getCastle()));
    }

    /**
     * Register clan as attacker<BR><BR>
     * @param player The L2PcInstance of the player trying to register
     */
    public void registerAttacker(L2PcInstance player)
    {
        registerAttacker(player, false);
    }

    public void registerAttacker(L2PcInstance player, boolean force)
    {
        if(!force)
        {
            int allyId = 0;
            if (getCastle().getOwnerId() != 0)
                allyId = ClanTable.getInstance().getClan(getCastle().getOwnerId()).getAllyId();

            if (allyId != 0 && player.getClan().getAllyId() == allyId)
            {
                player.sendMessage("You cannot register as an attacker because your alliance owns the castle");
                return; 
            }
        }

        if ((force && player.getClan() != null) || checkIfCanRegister(player, 1))
            saveSiegeClan(player.getClan(), 1, false); // Save to database
    }

    /**
     * Register clan as defender<BR><BR>
     * @param player The L2PcInstance of the player trying to register
     */
    public void registerDefender(L2PcInstance player)
    {
        registerDefender(player, false);
    }

    public void registerDefender(L2PcInstance player, boolean force)
    {
        if(!force)
        {
            if (getCastle().getOwnerId() <= 0)
            {
                player.sendMessage("You cannot register as a defender because "+getCastle().getName()+" is owned by NPC.");
                return;
            }
        }

        if ((force && player.getClan() != null) || checkIfCanRegister(player, 2))
            saveSiegeClan(player.getClan(), 2, false); // Save to database
    }

    /**
     * Remove clan from siege<BR><BR>
     * @param clanId The int of player's clan id
     */
    public void removeSiegeClan(int clanId)
    {
        if (clanId <= 0) return;

        Connection con = null;
        try
        {
            con = L2DatabaseFactory.getInstance().getConnection(con);
            PreparedStatement statement = con.prepareStatement("DELETE FROM siege_clans WHERE castle_id=? and clan_id=?");
            statement.setInt(1, getCastle().getCastleId());
            statement.setInt(2, clanId);
            statement.execute();
            statement.close();

            loadSiegeClan();
        }
        catch (Exception e)
        {
            _log.error(e.getMessage(), e);
        }
        finally { try { if (con != null) con.close(); } catch (SQLException e) { e.printStackTrace(); } }
    }

    /**
     * Remove clan from siege<BR><BR>
     * @param player The L2PcInstance of player/clan being removed 
     */
    public void removeSiegeClan(L2Clan clan)
    {
        if (clan == null || clan.getHasCastle() == getCastle().getCastleId()
            || !SiegeManager.getInstance().checkIsRegistered(clan, getCastle().getCastleId())) return;
        removeSiegeClan(clan.getClanId());
    }

    /**
     * Remove clan from siege<BR><BR>
     * @param player The L2PcInstance of player/clan being removed 
     */
    public void removeSiegeClan(L2PcInstance player)
    {
        removeSiegeClan(player.getClan());
    }

    /**
     * Start the auto tasks<BR><BR>
     */
    public void startAutoTask()
    {
        correctSiegeDateTime();

        _log.info("Siege of " + getCastle().getName() + ": "
            + getCastle().getSiegeDate().getTime());

        loadSiegeClan();

        // Schedule siege auto start
        if (_scheduledStartSiegeTask != null)
            _scheduledStartSiegeTask.cancel(false);
        _scheduledStartSiegeTask = ThreadPoolManager.getInstance().scheduleGeneral(new ScheduleStartSiegeTask(getCastle()), 1000);
    }

    /**
     * Teleport players
     */
    public void teleportPlayer(TeleportWhoType teleportWho, TeleportWhereType teleportWhere)
    {
        FastList<L2PcInstance> players;
        switch (teleportWho)
        {
            case Owner:
                players = getOwnersInZone();
                break;
            case Attacker:
                players = getAttackersInZone();
                break;
            case DefenderNotOwner:
                players = getDefendersButNotOwnersInZone();
                break;
            case Spectator:
                players = getSpectatorsInZone();
                break;
            default:
                players = getPlayersInZone();
        }

        for (L2PcInstance player : players)
        {
            if (player.isGM() || player.isInJail()) continue;
            player.teleToLocation(teleportWhere);
        }
    }

    // =========================================================
    // Method - Private
    /**
     * Add clan as attacker<BR><BR>
     * @param clanId The int of clan's id
     */
    private void addAttacker(int clanId)
    {
        getAttackerClans().add(new L2SiegeClan(clanId, SiegeClanType.ATTACKER)); // Add registered attacker to attacker list
    }

    /**
     * Add clan as defender<BR><BR>
     * @param clanId The int of clan's id
     */
    private void addDefender(int clanId)
    {
        getDefenderClans().add(new L2SiegeClan(clanId, SiegeClanType.DEFENDER)); // Add registered defender to defender list
    }

    /**
     * <p>Add clan as defender with the specified type</p>
     * @param clanId The int of clan's id
     * @param type the type of the clan
     */
    private void addDefender(int clanId, SiegeClanType type)
    {
        getDefenderClans().add(new L2SiegeClan(clanId, type));
    }

    /**
     * Add clan as defender waiting approval<BR><BR>
     * @param clanId The int of clan's id
     */
    private void addDefenderWaiting(int clanId)
    {
        getDefenderWaitingClans().add(new L2SiegeClan(clanId, SiegeClanType.DEFENDER_PENDING)); // Add registered defender to defender list
    }

    /**
     * Return true if the player can register.<BR><BR>
     * @param player The L2PcInstance of the player trying to register
     */
    private boolean checkIfCanRegister(L2PcInstance player, int typeId)
    {
        L2Clan clan = player.getClan();
        if(clan == null || clan.getLevel() < Config.SIEGE_CLAN_MIN_LEVEL)
        {
            player.sendMessage("Only clans with Level "+Config.SIEGE_CLAN_MIN_LEVEL+" and higher may register for a castle siege.");
            return false;
        }
        else if (getIsRegistrationOver()) 
        {
            player.sendMessage("The deadline to register for the siege of " + getCastle().getName() + " has passed.");
            return false;
        }
        else if (getIsInProgress()) 
        {
            player.sendMessage("This is not the time for siege registration and so registration and cancellation cannot be done.");
            return false;
        }
        else if (clan.getHasCastle() > 0) 
        {
            player.sendMessage("You cannot register because your clan already owns a castle.");
            return false;
        }
        else if (clan.getClanId() == getCastle().getOwnerId())
        {
            player.sendPacket(new SystemMessage(SystemMessageId.CLAN_THAT_OWNS_CASTLE_IS_AUTOMATICALLY_REGISTERED_DEFENDING));
            return false;
        }
        else if (checkIfAlreadyRegisteredForSameDay(player.getClan()))
        {
            player.sendPacket(new SystemMessage(SystemMessageId.APPLICATION_DENIED_BECAUSE_ALREADY_SUBMITTED_A_REQUEST_FOR_ANOTHER_SIEGE_BATTLE));
            return false;
        }
        else 
        {
            for(int i=0; i<10; i++)
            {
                if (SiegeManager.getInstance().checkIsRegistered(player.getClan(), i))
                {
                    player.sendPacket(new SystemMessage(SystemMessageId.ALREADY_REQUESTED_SIEGE_BATTLE));
                    return false;
                }
            }
        }

        if (typeId == 0 || typeId == 2 || typeId == -1)
        {
            if (getDefenderClans().size() + getDefenderWaitingClans().size() >= Config.SIEGE_MAX_DEFENDER)
            {
                player.sendMessage("The maximum number of defending clans has already been reached.");
                return false;
            }
        }
        if (typeId == 1)
        {
            if (getAttackerClans().size() >= Config.SIEGE_MAX_ATTACKER)
            {
                player.sendMessage("The maximum number of attacking clans has already been reached.");
                return false;
            }
        }

        return true;
    }

    /**
     * Return true if the clan has already registered to a siege for the same day.<BR><BR>
     * @param clan The L2Clan of the player trying to register
     */
    public boolean checkIfAlreadyRegisteredForSameDay(L2Clan clan)
    {
        for (Siege siege : SiegeManager.getInstance().getSieges())
        {
            if (siege == this) continue;
            if (siege.getSiegeDate().get(Calendar.DAY_OF_WEEK) == this.getSiegeDate().get(Calendar.DAY_OF_WEEK))
            {
                if (siege.checkIsAttacker(clan)) return true;
                if (siege.checkIsDefender(clan)) return true;
                if (siege.checkIsDefenderWaiting(clan)) return true;
            }
        }
        return false;
    }

    /**
     * Return the correct siege date as Calendar.<BR><BR>
     * @param siegeDate The Calendar siege date and time
     */
    public void correctSiegeDateTime()
    {
        boolean corrected = false;

        if (getCastle().getSiegeDate().getTimeInMillis() < System.currentTimeMillis())
        {
            // Since siege has past reschedule it to the next one
            // This is usually caused by server being down
            corrected = true;
            setNextSiegeDate();
        }

        if (!SevenSigns.getInstance().isDateInSealValidPeriod(getCastle().getSiegeDate()))
        {
            // no sieges in Quest period! reschedule it to the next SealValidationPeriod
            // This is usually caused by server being down
            corrected = true;
            setNextSiegeDate();
        }

        if (corrected)
            saveSiegeDate();
    }

    /** Load siege clans. */
    private void loadSiegeClan()
    {
        Connection con = null;
        try
        {
            getAttackerClans().clear();
            getDefenderClans().clear();
            getDefenderWaitingClans().clear();

            // Add castle owner as defender (add owner first so that they are on the top of the defender list)
            if (getCastle().getOwnerId() > 0)
                addDefender(getCastle().getOwnerId(), SiegeClanType.OWNER);

            PreparedStatement statement = null;
            ResultSet rs = null;

            con = L2DatabaseFactory.getInstance().getConnection(con);

            statement = con.prepareStatement("SELECT clan_id,type FROM siege_clans where castle_id=?");
            statement.setInt(1, getCastle().getCastleId());
            rs = statement.executeQuery();

            int typeId;
            while (rs.next())
            {
                typeId = rs.getInt("type");
                if (typeId == 0) addDefender(rs.getInt("clan_id"));
                else if (typeId == 1) addAttacker(rs.getInt("clan_id"));
                else if (typeId == 2) addDefenderWaiting(rs.getInt("clan_id"));
            }

            statement.close();
        }
        catch (Exception e)
        {
            _log.error("Exception: loadSiegeClan(): " + e.getMessage(),e);
        }
        finally { try { if (con != null) con.close(); } catch (SQLException e) { e.printStackTrace(); } }
    }

    /** Remove all control tower spawned. */
    private void removeControlTower()
    {
        if (_controlTowers != null)
        {
            // Remove all instance of control tower for this castle
            for (L2ControlTowerInstance ct : _controlTowers)
            {
                if (ct != null) ct.decayMe();
            }

            _controlTowers = null;
        }
    }

    /** Remove all flags. */
    private void removeFlags()
    {
        for (L2SiegeClan sc : getAttackerClans())
        {
            if (sc != null) sc.removeFlags();
        }
        for (L2SiegeClan sc : getDefenderClans())
        {
            if (sc != null) sc.removeFlags();
        }
    }
    
    /** Remove flags from defenders. */
    private void removeDefenderFlags()
    {
        for (L2SiegeClan sc : getDefenderClans())
        {
            if (sc != null) sc.removeFlags();
        }
    }
    
    /** Save castle siege related to database. */
    private void saveCastleSiege()
    {
        setNextSiegeDate(); // Set the next set date for 2 weeks from now
        // Schedule Time registration end
        getTimeRegistrationOverDate().setTimeInMillis(Calendar.getInstance().getTimeInMillis());
        getTimeRegistrationOverDate().add(Calendar.DAY_OF_MONTH, 1);
        getCastle().setIsTimeRegistrationOver(false);

        saveSiegeDate(); // Save the new date
        startAutoTask(); // Prepare auto start siege and end registration
    }

    /** Save siege date to database. */
    public void saveSiegeDate()
    {
        if (_scheduledStartSiegeTask != null)
        {
            _scheduledStartSiegeTask.cancel(true);
            _scheduledStartSiegeTask = ThreadPoolManager.getInstance().scheduleGeneral(new Siege.ScheduleStartSiegeTask(getCastle()), 1000);
        }

        Connection con = null;
        try
        {
            con = L2DatabaseFactory.getInstance().getConnection(con);
            PreparedStatement statement = con.prepareStatement("UPDATE castle SET siegeDate = ?, regTimeEnd = ?, regTimeOver = ?  WHERE id = ?");
            statement.setLong(1, getSiegeDate().getTimeInMillis());
            statement.setLong(2, getTimeRegistrationOverDate().getTimeInMillis());
            statement.setString(3, String.valueOf(getIsTimeRegistrationOver()));
            statement.setInt(4, getCastle().getCastleId());
            statement.execute();
            
            statement.close();            
        }
        catch (Exception e)
        {
            _log.error("Exception: saveSiegeDate(): " + e.getMessage(),e);
        }
        finally { try { if (con != null) con.close(); } catch (SQLException e) { e.printStackTrace(); } }
    }

    /**
     * Save registration to database.<BR><BR>
     * @param clan The L2Clan of player
     * @param typeId -1 = owner 0 = defender, 1 = attacker, 2 = defender waiting
     */
    private void saveSiegeClan(L2Clan clan, int typeId, boolean isUpdateRegistration)
    {
        if (clan.getHasCastle() > 0) return;

        Connection con = null;
        try
        {
            con = L2DatabaseFactory.getInstance().getConnection(con);
            PreparedStatement statement;
            if (!isUpdateRegistration)
            {
                statement = con.prepareStatement("INSERT INTO siege_clans (clan_id,castle_id,type,castle_owner) VALUES (?,?,?,0)");
                statement.setInt(1, clan.getClanId());
                statement.setInt(2, getCastle().getCastleId());
                statement.setInt(3, typeId);
                statement.execute();
                statement.close();
            }
            else
            {
                statement = con.prepareStatement("UPDATE siege_clans SET type = ? WHERE castle_id = ? AND clan_id = ?");
                statement.setInt(1, typeId);
                statement.setInt(2, getCastle().getCastleId());
                statement.setInt(3, clan.getClanId());
                statement.execute();
                statement.close();
            }

            if (typeId == 0 || typeId == -1)
            {
                addDefender(clan.getClanId());
                announceToPlayer(clan.getName()+" has been registered to defend "+getCastle().getName(), false);
            }
            else if (typeId == 1)
            {
                addAttacker(clan.getClanId());
                announceToPlayer(clan.getName()+" has been registered to attack "+getCastle().getName(), false);
            }
            else if (typeId == 2)
            {
                addDefenderWaiting(clan.getClanId());
                announceToPlayer(clan.getName()+" has requested to defend "+getCastle().getName(), false);
            }
        }
        catch (Exception e)
        {
            _log.error("Exception: saveSiegeClan(L2Clan clan, int typeId, boolean isUpdateRegistration): "
                + e.getMessage(),e);
        }
        finally { try { if (con != null) con.close(); } catch (SQLException e) { e.printStackTrace(); } }
    }

    /** Set the date for the next siege. */
    private void setNextSiegeDate()
    {
        while (getCastle().getSiegeDate().getTimeInMillis() < System.currentTimeMillis())
        {
            if (getCastle().getSiegeDate().get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY &&
                    getCastle().getSiegeDate().get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY)
                getCastle().getSiegeDate().set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
            // set the next siege day to the next weekend
            getCastle().getSiegeDate().add(Calendar.DAY_OF_MONTH, 7);
        }

        if (!SevenSigns.getInstance().isDateInSealValidPeriod(getCastle().getSiegeDate()))
            getCastle().getSiegeDate().add(Calendar.DAY_OF_MONTH, 7);

        _isRegistrationOver = false; // Allow registration for next siege
    }

    /** Spawn control tower. */
    private void spawnControlTower(int Id)
    {
        //Set control tower array size if one does not exist
        if (_controlTowers == null)
        	_controlTowers = new FastList<L2ControlTowerInstance>();

        for (SiegeSpawn _sp: SiegeManager.getInstance().getControlTowerSpawnList(Id))
        {
        	L2ControlTowerInstance ct;
        	
        	L2NpcTemplate template = NpcTable.getInstance().getTemplate(_sp.getNpcId());
        	 
            template.setBaseHpMax(_sp.getHp());
            
            ct = new L2ControlTowerInstance(IdFactory.getInstance().getNextId(), template);
            ct.getStatus().setCurrentHpMp(_sp.getHp(), ct.getMaxMp());
            ct.spawnMe(_sp.getLocation().getX(),_sp.getLocation().getY(),_sp.getLocation().getZ() + 20);

            _controlTowerCount++;
            _controlTowerMaxCount++;
            _controlTowers.add(ct);
        }
    }

    /**
     * Spawn siege guard.<BR><BR>
     */
    private void spawnSiegeGuard()
    {
        getSiegeGuardManager().spawnSiegeGuard();

        // Register guard to the closest Control Tower
        // When CT dies, so do all the guards that it controls
        if (!getSiegeGuardManager().getSiegeGuardSpawn().isEmpty() && !_controlTowers.isEmpty())
        {
            L2ControlTowerInstance closestCt;
            double distance, x, y, z;
            double distanceClosest = 0;
            for (L2Spawn spawn : getSiegeGuardManager().getSiegeGuardSpawn())
            {
                if (spawn == null) continue;
                closestCt = null;
                distanceClosest = 0;
                for (L2ControlTowerInstance ct : _controlTowers)
                {
                    if (ct == null) continue;
                    x = (spawn.getLocx() - ct.getX());
                    y = (spawn.getLocy() - ct.getY());
                    z = (spawn.getLocz() - ct.getZ());

                    distance = (x * x) + (y * y) + (z * z);

                    if (closestCt == null || distance < distanceClosest)
                    {
                        closestCt = ct;
                        distanceClosest = distance;
                    }
                }

                if (closestCt != null) closestCt.registerGuard(spawn);
            }
        }
    }

    public final L2SiegeClan getAttackerClan(L2Clan clan)
    {
        if (clan == null) return null;
        return getAttackerClan(clan.getClanId());
    }

    public final L2SiegeClan getAttackerClan(int clanId)
    {
        for (L2SiegeClan sc : getAttackerClans())
            if (sc != null && sc.getClanId() == clanId) return sc;
        return null;
    }

    public final FastList<L2SiegeClan> getAttackerClans()
    {
        if (_isNormalSide) return _attackerClans;
        return _defenderClans;
    }

    public final int getAttackerRespawnDelay()
    {
        return (Config.SIEGE_RESPAWN_DELAY_ATTACKER);
    }

    public final Castle getCastle()
    {
        return _castle;
    }

    public final L2SiegeClan getDefenderClan(L2Clan clan)
    {
        if (clan == null) return null;
        return getDefenderClan(clan.getClanId());
    }

    public final L2SiegeClan getDefenderClan(int clanId)
    {
        for (L2SiegeClan sc : getDefenderClans())
            if (sc != null && sc.getClanId() == clanId) return sc;
        return null;
    }

    public final FastList<L2SiegeClan> getDefenderClans()
    {
        if (_isNormalSide) return _defenderClans;
        return _attackerClans;
    }

    public final L2SiegeClan getDefenderWaitingClan(L2Clan clan)
    {
        if (clan == null) return null;
        return getDefenderWaitingClan(clan.getClanId());
    }

    public final L2SiegeClan getDefenderWaitingClan(int clanId)
    {
        for (L2SiegeClan sc : getDefenderWaitingClans())
            if (sc != null && sc.getClanId() == clanId) return sc;
        return null;
    }

    public final FastList<L2SiegeClan> getDefenderWaitingClans()
    {
        return _defenderWaitingClans;
    }

    public final boolean getIsInProgress()
    {
        return _isInProgress;
    }

    public final boolean getIsRegistrationOver()
    {
        return _isRegistrationOver;
    }

    public final boolean getIsTimeRegistrationOver()
    {
        return getCastle().getIsTimeRegistrationOver();
    }

    public final Calendar getSiegeDate()
    {
        return getCastle().getSiegeDate();
    }

    public final Calendar getTimeRegistrationOverDate()
    {
        return getCastle().getTimeRegistrationOverDate();
    }

    public void endTimeRegistration(boolean automatic)
    {
        getCastle().setIsTimeRegistrationOver(true);
        if (!automatic)
            saveSiegeDate();
    }

    public Set<L2NpcInstance> getFlag(L2Clan clan)
    {
        if (clan != null)
        {
            L2SiegeClan sc = getAttackerClan(clan);
            if (sc != null) return sc.getFlag();
        }
        return null;
    }

    public L2NpcInstance getClosestFlag(L2Object obj)
    {
    	if (( obj != null) && (obj instanceof L2PcInstance))
    	{
    		if (((L2PcInstance)obj).getClan() != null)
    		{
    			L2SiegeClan sc = getAttackerClan(((L2PcInstance)obj).getClan());
    			if (sc != null) return sc.getClosestFlag(obj);
    		}
    	}
    	return null;
    }
    
    public final SiegeGuardManager getSiegeGuardManager()
    {
        if (_siegeGuardManager == null)
        {
            _siegeGuardManager = new SiegeGuardManager(getCastle());
        }
        return _siegeGuardManager;
    }

    public final L2SiegeZone getZone()
    {
        return getCastle().getBattlefield();
    }
    
	public int getControlTowerCount()
	{
		return _controlTowerCount;
	}
}
