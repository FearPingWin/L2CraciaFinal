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
package com.l2jfree.gameserver.network.packets.client;

import static com.l2jfree.gameserver.gameobjects.itemcontainer.PlayerInventory.MAX_ADENA;

import java.util.ArrayList;
import java.util.List;

import com.l2jfree.Config;
import com.l2jfree.gameserver.Shutdown;
import com.l2jfree.gameserver.Shutdown.DisableType;
import com.l2jfree.gameserver.gameobjects.L2Player;
import com.l2jfree.gameserver.instancemanager.MarketManager;
import com.l2jfree.gameserver.model.TradeList;
import com.l2jfree.gameserver.model.zone.L2Zone;
import com.l2jfree.gameserver.network.SystemMessageId;
import com.l2jfree.gameserver.network.packets.L2ClientPacket;
import com.l2jfree.gameserver.network.packets.server.ExPrivateStoreSetWholeMsg;
import com.l2jfree.gameserver.network.packets.server.ItemList;
import com.l2jfree.gameserver.network.packets.server.PrivateStoreManageListSell;
import com.l2jfree.gameserver.network.packets.server.PrivateStoreMsgSell;

public class SetPrivateStoreListSell extends L2ClientPacket
{
	private static final String _C__74_SETPRIVATESTORELISTSELL = "[C] 74 SetPrivateStoreListSell";
	
	private static final int BATCH_LENGTH = 12; // length of the one item
	private static final int BATCH_LENGTH_FINAL = 20;
	
	private boolean _packageSale;
	private Item[] _items = null;
	
	@Override
	protected void readImpl()
	{
		_packageSale = (readD() == 1);
		int count = readD();
		if (count < 1 || count > Config.MAX_ITEM_IN_PACKET
				|| count * (Config.PACKET_FINAL ? BATCH_LENGTH_FINAL : BATCH_LENGTH) != getByteBuffer().remaining())
			return;
		
		_items = new Item[count];
		for (int i = 0; i < count; i++)
		{
			int itemId = readD();
			long cnt = readCompQ();
			long price = readCompQ();
			
			if (itemId < 1 || cnt < 1 || price < 0)
			{
				_items = null;
				return;
			}
			_items[i] = new Item(itemId, cnt, price);
		}
	}
	
	@Override
	protected void runImpl()
	{
		L2Player player = getClient().getActiveChar();
		if (player == null)
			return;
		
		if (_items == null)
		{
			requestFailed(SystemMessageId.INCORRECT_ITEM_COUNT);
			player.setPrivateStoreType(L2Player.STORE_PRIVATE_NONE);
			player.broadcastUserInfo();
			return;
		}
		
		if (Shutdown.isActionDisabled(DisableType.TRANSACTION))
		{
			requestFailed(SystemMessageId.FUNCTION_INACCESSIBLE_NOW);
			return;
		}
		
		if (Config.GM_DISABLE_TRANSACTION && player.getAccessLevel() >= Config.GM_TRANSACTION_MIN
				&& player.getAccessLevel() <= Config.GM_TRANSACTION_MAX)
		{
			requestFailed(SystemMessageId.ACCOUNT_CANT_TRADE_ITEMS);
			return;
		}
		
		// Check maximum number of allowed slots for pvt shops
		if (_items.length > player.getPrivateSellStoreLimit())
		{
			sendPacket(new PrivateStoreManageListSell(player, _packageSale));
			requestFailed(SystemMessageId.YOU_HAVE_EXCEEDED_QUANTITY_THAT_CAN_BE_INPUTTED);
			return;
		}
		
		// Prevents player to start selling inside a nostore zone. By heX1r0
		if (player.isInsideZone(L2Zone.FLAG_NOSTORE))
		{
			sendPacket(new PrivateStoreManageListSell(player, _packageSale));
			requestFailed(SystemMessageId.NO_PRIVATE_STORE_HERE);
			return;
		}
		
		TradeList tradeList = player.getSellList();
		tradeList.clear();
		tradeList.setPackaged(_packageSale);
		
		long totalCost = player.getAdena();
		for (Item i : _items)
		{
			if (!i.addToTradeList(tradeList))
			{
				requestFailed(SystemMessageId.YOU_HAVE_EXCEEDED_QUANTITY_THAT_CAN_BE_INPUTTED);
				return;
			}
			
			totalCost += i.getPrice();
			if (totalCost > MAX_ADENA)
			{
				requestFailed(SystemMessageId.YOU_HAVE_EXCEEDED_QUANTITY_THAT_CAN_BE_INPUTTED);
				return;
			}
		}
		
		/* =========================
		 * РЫНОК ЧЕРЕЗ ИНВЕНТАРЬ (наш модальный режим)
		 * Если открыт наш режим — НЕ запускаем личный магазин,
		 * а переносим выбранные вещи в MARKET и создаём лоты.
		 * ========================= */
		if (player.isMarketModal())
		{
			try
			{
				// Синхронизируем список с инвентарём, чтобы получить конкретные objectId и допустимые count
				tradeList.updateItems();
				
				TradeList.TradeItem[] items = tradeList.getItems();
				final List<MarketManager.ItemRequest> reqs = new ArrayList<MarketManager.ItemRequest>(items.length);
				
				for (TradeList.TradeItem ti : items)
				{
					if (ti == null)
						continue;
					final int objectId = ti.getObjectId();
					final long count = ti.getCount();
					final long unitPrice = ti.getPrice();  // цена за 1 штуку
					final long totalPrice = unitPrice * count;   // в market_listings храним цену за весь стак
					
					reqs.add(new MarketManager.ItemRequest(objectId, count, totalPrice));
				}
				
				// Перенос в MARKET + INSERT в market_listings (менеджер сам обновит инвентарь пакетами)
				MarketManager.getInstance().commitListingsFromInventory(player, reqs);
				
				// Чистим временный sell list и выключаем модальный режим
				tradeList.clear();
				player.setPrivateStoreType(L2Player.STORE_PRIVATE_NONE);
				player.broadcastUserInfo();
				player.stopMarketModal();
				
				// На всякий случай обновим список предметов клиенту и подтвердим действие
				player.sendPacket(new ItemList(player, false));
				player.sendMessage("Лоты выставлены. Предметы перемещены на рынок.");
				
				sendAF();
				return; // ВАЖНО: личный магазин не запускаем
			}
			catch (Exception e)
			{
				// Вернём игрока к окну управления продажей и покажем ошибку
				sendPacket(new PrivateStoreManageListSell(player, _packageSale));
				player.sendMessage("Ошибка выставления: " + e.getMessage());
				player.stopMarketModal();
				
				sendAF();
				return;
			}
		}
		

		player.sitDown();
		
		if (_packageSale)
			player.setPrivateStoreType(L2Player.STORE_PRIVATE_PACKAGE_SELL);
		else
			player.setPrivateStoreType(L2Player.STORE_PRIVATE_SELL);
		
		player.broadcastUserInfo();
		
		if (_packageSale)
			player.broadcastPacket(new ExPrivateStoreSetWholeMsg(player));
		else
			player.broadcastPacket(new PrivateStoreMsgSell(player));
		
		sendAF();
	}
	
	private class Item
	{
		private final int _itemId;
		private final long _count;
		private final long _price;
		
		public Item(int id, long num, long pri)
		{
			_itemId = id;
			_count = num;
			_price = pri;
		}
		
		public boolean addToTradeList(TradeList list)
		{
			if ((MAX_ADENA / _count) < _price)
				return false;
			
			list.addItem(_itemId, _count, _price);
			return true;
		}
		
		public long getPrice()
		{
			return _count * _price;
		}
	}
	
	@Override
	public String getType()
	{
		return _C__74_SETPRIVATESTORELISTSELL;
	}
}
