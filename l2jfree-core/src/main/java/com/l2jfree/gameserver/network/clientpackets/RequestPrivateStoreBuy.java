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

import com.l2jfree.Config;
import com.l2jfree.gameserver.Shutdown;
import com.l2jfree.gameserver.model.ItemRequest;
import com.l2jfree.gameserver.model.L2Object;
import com.l2jfree.gameserver.model.L2World;
import com.l2jfree.gameserver.model.TradeList;
import com.l2jfree.gameserver.model.TradeList.TradeItem;
import com.l2jfree.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfree.gameserver.network.SystemMessageId;
import com.l2jfree.gameserver.network.serverpackets.ActionFailed;
import com.l2jfree.gameserver.network.serverpackets.SystemMessage;
import com.l2jfree.gameserver.util.Util;

/**
 * This class ...
 *
 * @version $Revision: 1.2.2.1.2.5 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestPrivateStoreBuy extends L2GameClientPacket
{
    private static final String _C__79_REQUESTPRIVATESTOREBUY = "[C] 79 RequestPrivateStoreBuy";

    private int _storePlayerId;
    private int _count;
    private ItemRequest[] _items;

    @Override
    protected void readImpl()
    {
        _storePlayerId = readD();
        _count = readD();
        if (_count < 0  || _count * 12 > _buf.remaining() || _count > Config.MAX_ITEM_IN_PACKET)
            _count = 0;
        _items = new ItemRequest[_count];

        for (int i = 0; i < _count ; i++)
        {
            int objectId = readD(); 
            long count   = readD(); 
            if (count > Integer.MAX_VALUE) count = Integer.MAX_VALUE;
            int price    = readD(); 
            
            _items[i] = new ItemRequest(objectId, (int)count, price);
        }
    }

    @Override
    protected void runImpl()
    {
        L2PcInstance player = getClient().getActiveChar();
        if (player == null) return;

        if (Config.SAFE_REBOOT && Config.SAFE_REBOOT_DISABLE_TRANSACTION && Shutdown.getCounterInstance() != null 
           && Shutdown.getCounterInstance().getCountdown() <= Config.SAFE_REBOOT_TIME)
        {
            player.sendMessage("Transactions are not allowed during restart/shutdown.");
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        L2Object object = L2World.getInstance().findObject(_storePlayerId);
        if (!(object instanceof L2PcInstance)) return;

        if(player.isCursedWeaponEquipped())
            return;

        L2PcInstance storePlayer = (L2PcInstance)object;

        if (!(storePlayer.getPrivateStoreType() == L2PcInstance.STORE_PRIVATE_SELL || storePlayer.getPrivateStoreType() == L2PcInstance.STORE_PRIVATE_PACKAGE_SELL)) return;

        TradeList storeList = storePlayer.getSellList();
        if (storeList == null) return;
        
        if (Config.GM_DISABLE_TRANSACTION && player.getAccessLevel() >= Config.GM_TRANSACTION_MIN && player.getAccessLevel() <= Config.GM_TRANSACTION_MAX)
        {
            player.sendMessage("Unsufficient privileges.");
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        
        long priceTotal = 0;
        for(ItemRequest ir : _items)
        {
			if (ir.getCount() > Integer.MAX_VALUE || ir.getCount() < 0)
			{
	            String msgErr = "[RequestPrivateStoreBuy] player "+getClient().getActiveChar().getName()+" tried an overflow exploit, ban this player!";
	            Util.handleIllegalPlayerAction(getClient().getActiveChar(),msgErr,Config.DEFAULT_PUNISH);
			    return;
			}
			TradeItem sellersItem = storeList.getItem(ir.getObjectId());
			if(sellersItem == null)
			{
	            String msgErr = "[RequestPrivateStoreBuy] player "+getClient().getActiveChar().getName()+" tried to buy an item not sold in a private store (buy), ban this player!";
	            Util.handleIllegalPlayerAction(getClient().getActiveChar(),msgErr,Config.DEFAULT_PUNISH);
			    return;
			}
			if(ir.getPrice() != sellersItem.getPrice())
			{
	            String msgErr = "[RequestPrivateStoreBuy] player "+getClient().getActiveChar().getName()+" tried to change the seller's price in a private store (buy), ban this player!";
	            Util.handleIllegalPlayerAction(getClient().getActiveChar(),msgErr,Config.DEFAULT_PUNISH);
			    return;
			}
			priceTotal += ir.getPrice() * ir.getCount();
        }
        
        if(priceTotal < 0 || priceTotal > Integer.MAX_VALUE)
        {
            String msgErr = "[RequestPrivateStoreBuy] player "+getClient().getActiveChar().getName()+" tried an overflow exploit, ban this player!";
            Util.handleIllegalPlayerAction(getClient().getActiveChar(),msgErr,Config.DEFAULT_PUNISH);
            return;
        }
        
        if (player.getAdena() < priceTotal)
        {
            sendPacket(new SystemMessage(SystemMessageId.YOU_NOT_ENOUGH_ADENA));
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        if (storePlayer.getPrivateStoreType() == L2PcInstance.STORE_PRIVATE_PACKAGE_SELL)
        {
           if (storeList.getItemCount() > _count)
           {
               String msgErr = "[RequestPrivateStoreBuy] player "+getClient().getActiveChar().getName()+" tried to buy less items then sold by package-sell, ban this player for bot-usage!";
               Util.handleIllegalPlayerAction(getClient().getActiveChar(),msgErr,Config.DEFAULT_PUNISH);
               return;
           }
        }

        if (!storeList.privateStoreBuy(player, _items, (int) priceTotal))
        {
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        if (storeList.getItemCount() == 0)
        {
            storePlayer.setPrivateStoreType(L2PcInstance.STORE_PRIVATE_NONE);
            storePlayer.broadcastUserInfo();
        }

/*   Lease holders are currently not implemented
        else if (_seller != null)
        {
            // lease shop sell
            L2MerchantInstance seller = (L2MerchantInstance)_seller;
            L2ItemInstance ladena = seller.getLeaseAdena();
            for (TradeItem ti : buyerlist) {
                L2ItemInstance li = seller.getLeaseItemByObjectId(ti.getObjectId());
                if (li == null) {
                    if (ti.getObjectId() == ladena.getObjectId())
                    {
                        buyer.addAdena(ti.getCount());
                        ladena.setCount(ladena.getCount()-ti.getCount());
                        ladena.updateDatabase();
                    }
                    continue;
                }
                int cnt = li.getCount();
                if (cnt < ti.getCount())
                    ti.setCount(cnt);
                if (ti.getCount() <= 0)
                    continue;
                L2ItemInstance inst = ItemTable.getInstance().createItem(li.getItemId());
                inst.setCount(ti.getCount());
                inst.setEnchantLevel(li.getEnchantLevel());
                buyer.getInventory().addItem(inst);
                li.setCount(li.getCount()-ti.getCount());
                li.updateDatabase();
                ladena.setCount(ladena.getCount()+ti.getCount()*ti.getOwnersPrice());
                ladena.updateDatabase();
            }
        }*/
    }

    @Override
    public String getType()
    {
        return _C__79_REQUESTPRIVATESTOREBUY;
    }
}
