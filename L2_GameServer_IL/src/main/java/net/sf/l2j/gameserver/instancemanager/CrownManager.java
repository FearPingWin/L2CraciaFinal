/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package net.sf.l2j.gameserver.instancemanager;

import java.sql.PreparedStatement;

import net.sf.l2j.L2DatabaseFactory;
import net.sf.l2j.gameserver.datatables.CrownTable;
import net.sf.l2j.gameserver.model.L2Clan;
import net.sf.l2j.gameserver.model.L2ClanMember;
import net.sf.l2j.gameserver.model.L2ItemInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.entity.Castle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** 
 * @author evill33t
 * Reworked by NB4L1
 */
public class CrownManager
{
	private static final Log _log = LogFactory.getLog(CrownManager.class.getName());
	private static CrownManager _instance;
	
	public static final CrownManager getInstance()
	{
		if (_instance == null)
			_instance = new CrownManager();
		return _instance;
	}
	
	public void CrownManager()
	{
		_log.info("CrownManager: initialized");
	}
	
	public void checkCrowns(L2Clan clan)
	{
		if (clan == null)
			return;
		
		for (L2ClanMember member : clan.getMembers())
		{
			if (member != null && member.isOnline() && member.getPlayerInstance() != null)
			{
				checkCrowns(member.getPlayerInstance());
			}
		}
	}
	
	public void checkCrowns(L2PcInstance activeChar)
	{
		if (activeChar == null)
			return;
		
		boolean isLeader = false;
		int crownId = -1;
		
		L2Clan activeCharClan = activeChar.getClan();
		
		if (activeCharClan != null)
		{
			Castle activeCharCastle = CastleManager.getInstance().getCastleByOwner(activeCharClan);
			
			if (activeCharCastle != null)
			{
				crownId = CrownTable.getCrownId(activeCharCastle.getCastleId());
			}
			
			if (activeCharClan.getLeader().getObjectId() == activeChar.getObjectId())
			{
				isLeader = true;
			}
		}
		
		if (crownId > 0)
		{
			if (isLeader && activeChar.getInventory().getItemByItemId(6841) == null)
			{
				activeChar.getInventory().addItem("Crown", 6841, 1, activeChar, null);
				activeChar.getInventory().updateDatabase();
			}
			
			if (activeChar.getInventory().getItemByItemId(crownId) == null)
			{
				activeChar.getInventory().addItem("Crown", crownId, 1, activeChar, null);
				activeChar.getInventory().updateDatabase();
			}
		}
		
		for (L2ItemInstance item : activeChar.getInventory().getItems())
		{
			if (CrownTable.getCrownList().contains(item.getItemId()) &&
				!(
					isLeader &&
					item.getItemId() == 6841 &&
					crownId > 0
				) &&
				item.getItemId() != crownId)
			{
				activeChar.destroyItem("Removing Crown", item, activeChar, true);
				activeChar.getInventory().updateDatabase();
			}
		}
	}
}