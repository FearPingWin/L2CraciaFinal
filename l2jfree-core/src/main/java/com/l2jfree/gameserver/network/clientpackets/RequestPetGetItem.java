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
package com.l2jfree.gameserver.network.clientpackets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.l2jfree.gameserver.ai.CtrlIntention;
import com.l2jfree.gameserver.instancemanager.MercTicketManager;
import com.l2jfree.gameserver.model.L2ItemInstance;
import com.l2jfree.gameserver.model.L2Object;
import com.l2jfree.gameserver.model.L2World;
import com.l2jfree.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfree.gameserver.model.actor.instance.L2PetInstance;
import com.l2jfree.gameserver.model.actor.instance.L2SummonInstance;
import com.l2jfree.gameserver.network.serverpackets.ActionFailed;

/**
 * This class ...
 * 
 * @version $Revision: 1.2.4.4 $ $Date: 2005/03/29 23:15:33 $
 */
public class RequestPetGetItem extends L2GameClientPacket
{

	private final static Log _log = LogFactory.getLog(RequestPetGetItem.class.getName());

	private static final String _C__8f_REQUESTPETGETITEM= "[C] 8F RequestPetGetItem";

	private int _objectId;

	@Override
	protected void readImpl()
	{
		_objectId = readD();
	}

	@Override
	protected void runImpl()
	{
		L2PcInstance player = getClient().getActiveChar();
		if (player == null)
			return;

		// Get object from knownlist
		L2Object obj = player.getKnownList().getKnownObject(_objectId);

		// Get object from world
		if (obj == null)
		{
			obj = L2World.getInstance().findObject(_objectId);
			_log.warn("Player "+player.getName()+" requested pet to pickup item from outside of his knownlist.");
		}

		if (!(obj instanceof L2ItemInstance))
			return;

		L2ItemInstance item = (L2ItemInstance)obj;

		if(player.getPet() == null || player.getPet() instanceof L2SummonInstance)
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}

		int castleId = MercTicketManager.getInstance().getTicketCastleId(item.getItemId());
		if (castleId > 0)
		{
			sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}

		L2PetInstance pet = (L2PetInstance)player.getPet();
		if (pet.isDead() || pet.isOutOfControl())
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		pet.getAI().setIntention(CtrlIntention.AI_INTENTION_PICK_UP, item);
	}

	@Override
	public String getType()
	{
		return _C__8f_REQUESTPETGETITEM;
	}
}
