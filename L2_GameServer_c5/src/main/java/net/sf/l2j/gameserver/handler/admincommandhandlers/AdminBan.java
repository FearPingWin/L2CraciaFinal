/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package net.sf.l2j.gameserver.handler.admincommandhandlers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import net.sf.l2j.Config;
import net.sf.l2j.L2DatabaseFactory;
import net.sf.l2j.gameserver.ClientThread;
import net.sf.l2j.gameserver.LoginServerThread;
import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.model.GMAudit;
import net.sf.l2j.gameserver.model.L2Object;
import net.sf.l2j.gameserver.model.L2World;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.serverpackets.LeaveWorld;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AdminBan implements IAdminCommandHandler {
    
    private final static Log _log = LogFactory.getLog(AdminBan.class);
    
    private static String[] _adminCommands = {"admin_ban", "admin_unban","admin_jail","admin_unjail"};
    private static final int REQUIRED_LEVEL = Config.GM_BAN;

    public boolean useAdminCommand(String command, L2PcInstance activeChar)
    {
        if (!Config.ALT_PRIVILEGES_ADMIN)
        {
            if (!(checkLevel(activeChar.getAccessLevel())))
            {
                return false;
            }
        }
                
        StringTokenizer st = new StringTokenizer(command);
        st.nextToken();
        String plyr = "";
        L2PcInstance player = null;
        if (command.startsWith("admin_ban"))
        {   
            try
            {
                player = L2World.getInstance().getPlayer(st.nextToken());
            }
            catch(Exception e)
            {
                L2Object target = activeChar.getTarget();
                if (target!=null && target instanceof L2PcInstance)
                    player = (L2PcInstance)target;
                else
                    activeChar.sendMessage("Wrong parameter or target");
            }
            
            if (player!=null && !player.equals(activeChar)) // you cannot ban yourself!
            {
                LoginServerThread.getInstance().sendAccessLevel(player.getAccountName(), -100);
                kickPlayer(player);
            }
        }
        else if (command.startsWith("admin_unban"))
        {
            try
            {
                player = L2World.getInstance().getPlayer(st.nextToken());
                if (player!=null)
                    LoginServerThread.getInstance().sendAccessLevel(player.getAccountName(), 0);
            }
            catch(Exception e)
            {
                _log.error (e.getMessage(),e);
            }
        }
        else if (command.startsWith("admin_jail"))
        {
            try
            {
                plyr = st.nextToken();
                int delay = 0;
                try
                {
                    delay = Integer.parseInt(st.nextToken());
                } catch (NumberFormatException nfe) {
                } catch (NoSuchElementException nsee) {}
                L2PcInstance playerObj = L2World.getInstance().getPlayer(plyr);

                if (playerObj != null)
                {
                    playerObj.setInJail(true, delay);
                    activeChar.sendMessage("Character "+player+" jailed for "+(delay>0 ? delay+" minutes." : "ever!"));
                } else
                    jailOfflinePlayer(activeChar, player.getName(), delay);
            } catch (NoSuchElementException nsee) 
            {
                activeChar.sendMessage("Specify a character name.");
            } catch(Exception e)
            {
                _log.error (e.getMessage(),e);
            }            
        }
        else if (command.startsWith("admin_unjail"))
        {
            try
            {
                plyr = st.nextToken();
                L2PcInstance playerObj = L2World.getInstance().getPlayer(plyr);

                if (playerObj != null)
                {
                    playerObj.setInJail(false, 0);
                    activeChar.sendMessage("Character "+player+" removed from jail");
                } else
                    unjailOfflinePlayer(activeChar, player.getName());
            } catch (NoSuchElementException nsee) 
            {
                activeChar.sendMessage("Specify a character name.");
            } catch(Exception e)
            {
                _log.error (e.getMessage(),e);
            }            
        }
        
        if (!"".equals(player))
            GMAudit.auditGMAction(activeChar.getName(), command, player.getName(), "");

        return true;
    }
    
    private void jailOfflinePlayer(L2PcInstance activeChar, String name, int delay)
    {
        Connection con = null;
        try
        {
            con = L2DatabaseFactory.getInstance().getConnection();
            
            PreparedStatement statement = con.prepareStatement("UPDATE characters SET x=?, y=?, z=?, in_jail=?, jail_timer=? WHERE char_name=?");
            statement.setInt(1, -114356);
            statement.setInt(2, -249645);
            statement.setInt(3, -2984);
            statement.setInt(4, 1);
            statement.setLong(5, delay * 60000);
            statement.setString(6, name);

            statement.execute();
            int count = statement.getUpdateCount();
            statement.close();

            if (count == 0)
                activeChar.sendMessage("Character not found!");
            else
                activeChar.sendMessage("Character "+name+" jailed for "+(delay>0 ? delay+" minutes." : "ever!"));
        } catch (SQLException se)
        {
            activeChar.sendMessage("SQLException while jailing player");
            _log.error (se.getMessage(),se);
        } finally
        {
            try { con.close(); } catch (Exception e) {}
        }
    }
    
    private void unjailOfflinePlayer(L2PcInstance activeChar, String name)
    {
        Connection con = null;
        try
        {
            con = L2DatabaseFactory.getInstance().getConnection();
            
            PreparedStatement statement = con.prepareStatement("UPDATE characters SET x=?, y=?, z=?, in_jail=?, jail_timer=? WHERE char_name=?");
            statement.setInt(1, 17836);
            statement.setInt(2, 170178);
            statement.setInt(3, -3507);
            statement.setInt(4, 0);
            statement.setLong(5, 0);
            statement.setString(6, name);

            statement.execute();
            int count = statement.getUpdateCount();
            statement.close();

            if (count == 0)
                activeChar.sendMessage("Character not found!");
            else
                activeChar.sendMessage("Character "+name+" removed from jail");
        } catch (SQLException se)
        {
            activeChar.sendMessage("SQLException while jailing player");
            _log.error (se.getMessage(),se);
        } finally
        {
            try { con.close(); } catch (Exception e) {}
        }
    }
    
    private void kickPlayer (L2PcInstance player)
    {
        try {
            ClientThread.saveCharToDisk(player);
            player.sendPacket(new LeaveWorld());
            player.deleteMe();
            player.logout();
            } catch (Throwable t)   {}
 
        try {
            player.closeNetConnection();
            } catch (Throwable t)   {} 
    }
  
    public String[] getAdminCommandList() {
        return _adminCommands;
    }
    
    private boolean checkLevel(int level) {
        return (level >= REQUIRED_LEVEL);
    }
}
