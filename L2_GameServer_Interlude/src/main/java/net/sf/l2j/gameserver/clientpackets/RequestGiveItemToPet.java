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
package net.sf.l2j.gameserver.clientpackets;

import java.nio.ByteBuffer;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.ClientThread;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2PetInstance;
import net.sf.l2j.gameserver.serverpackets.SystemMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class ...
 * 
 * @version $Revision: 1.3.2.1.2.5 $ $Date: 2005/03/29 23:15:33 $
 */
public class RequestGiveItemToPet extends ClientBasePacket
{
	private static final String REQUESTCIVEITEMTOPET__C__8B = "[C] 8B RequestGiveItemToPet";
	private final static Log _log = LogFactory.getLog(RequestGetItemFromPet.class.getName());

	private final int _objectId;
	private final int _amount;
	
	public RequestGiveItemToPet(ByteBuffer buf, ClientThread client)
	{
		super(buf, client);
		_objectId = readD();
		_amount   = readD();
	}

	void runImpl()
	{
		L2PcInstance player = getClient().getActiveChar(); 
        if (player == null || player.getPet() == null || !(player.getPet() instanceof L2PetInstance)) return;

        // Alt game - Karma punishment
        if (!Config.ALT_GAME_KARMA_PLAYER_CAN_TRADE && player.getKarma() > 0) return;

        if (player.getPrivateStoreType() != 0)
        {
            player.sendMessage("Cannot exchange items while trading");
            return;
        }

        L2PetInstance pet = (L2PetInstance)player.getPet(); 
		if (pet.isDead())
		{
			sendPacket(new SystemMessage(SystemMessage.CANNOT_GIVE_ITEMS_TO_DEAD_PET));
			return;
		}

		if(_amount < 0)
		{
			return;
		}
		
        if(!player.getInventory().getItemByObjectId(_objectId).isAvailable(player, true))
        {
            sendPacket(new SystemMessage(972));
            return;
        }
        
        if (Config.ALT_STRICT_HERO_SYSTEM && player.getInventory().getItemByObjectId(_objectId).isHeroitem())
        {
            sendPacket(new SystemMessage(SystemMessage.ITEM_NOT_FOR_PETS));
            return;
        }
        
		if (player.transferItem("Transfer", _objectId, _amount, pet.getInventory(), pet) == null)
			_log.warn("Invalid Item transfer request: " + pet.getName() + "(pet) --> " + player.getName());
	}

	public String getType()
	{
		return REQUESTCIVEITEMTOPET__C__8B;
	}
}
