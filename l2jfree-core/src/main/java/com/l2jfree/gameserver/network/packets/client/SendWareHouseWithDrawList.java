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

import com.l2jfree.Config;
import com.l2jfree.gameserver.Shutdown;
import com.l2jfree.gameserver.Shutdown.DisableType;
import com.l2jfree.gameserver.gameobjects.L2Npc;
import com.l2jfree.gameserver.gameobjects.L2Player;
import com.l2jfree.gameserver.gameobjects.itemcontainer.ClanWarehouse;
import com.l2jfree.gameserver.gameobjects.itemcontainer.ItemContainer;
import com.l2jfree.gameserver.gameobjects.itemcontainer.MarketContainer;
import com.l2jfree.gameserver.model.clan.L2Clan;
import com.l2jfree.gameserver.model.items.L2ItemInstance;
import com.l2jfree.gameserver.model.items.L2ItemInstance.ItemLocation;
import com.l2jfree.gameserver.model.world.L2World;
import com.l2jfree.gameserver.gameobjects.L2Object;
import com.l2jfree.gameserver.network.SystemMessageId;
import com.l2jfree.gameserver.network.packets.L2ClientPacket;
import com.l2jfree.gameserver.network.packets.server.InventoryUpdate;
import com.l2jfree.gameserver.network.packets.server.ItemList;
import com.l2jfree.gameserver.network.packets.server.StatusUpdate;
import com.l2jfree.gameserver.util.FloodProtector;
import com.l2jfree.gameserver.util.FloodProtector.Protected;

/**
 * 32  SendWareHouseWithDrawList  cd (dd)
 * 
 * Добавлено: поддержка модального режима "забрать с РЫНКА".
 * Если у игрока активен isMarketWithdrawModal(), вместо склада
 * источником выступает контейнер MARKET.
 */
public class SendWareHouseWithDrawList extends L2ClientPacket
{
	private static final String _C__32_SENDWAREHOUSEWITHDRAWLIST = "[C] 32 SendWareHouseWithDrawList";
	
	private static final int BATCH_LENGTH = 8;   // length of one item (final=false)
	private static final int BATCH_LENGTH_FINAL = 12; // при PACKET_FINAL
	
	private WarehouseItem _items[] = null;
	
	@Override
	protected void readImpl()
	{
		int count = readD();
		if (count <= 0 || count > Config.MAX_ITEM_IN_PACKET
				|| count * (Config.PACKET_FINAL ? BATCH_LENGTH_FINAL : BATCH_LENGTH) != getByteBuffer().remaining())
		{
			return;
		}
		
		_items = new WarehouseItem[count];
		for (int i = 0; i < count; i++)
		{
			int objId = readD();
			long cnt = readCompQ();
			if (objId < 1 || cnt < 0)
			{
				_items = null;
				return;
			}
			_items[i] = new WarehouseItem(objId, cnt);
		}
	}
	
	@Override
	protected void runImpl()
	{
		L2Player player = getClient().getActiveChar();
		if (player == null)
			return;
		else if (!FloodProtector.tryPerformAction(player, Protected.TRANSACTION))
			return;
		
		if (_items == null)
		{
			sendAF();
			return;
		}
		
		if (Shutdown.isActionDisabled(DisableType.TRANSACTION))
		{
			requestFailed(SystemMessageId.FUNCTION_INACCESSIBLE_NOW);
			return;
		}

		/* =========================================================
		 * РЕЖИМ РЫНКА: забираем из MARKET, а не из WAREHOUSE
		 * ========================================================= */
		if (player.isMarketWithdrawModal())
		{
			// (1) Предварительные расчёты веса и слотов
			int weight = 0;
			int slots = 0;

			for (WarehouseItem wi : _items)
			{
				L2Object obj = L2World.getInstance().findObject(wi.getObjectId());
				if (!(obj instanceof L2ItemInstance))
				{
					requestFailed(SystemMessageId.NO_ITEM_DEPOSITED_IN_WH);
					return;
				}
				L2ItemInstance item = (L2ItemInstance)obj;

				if (item.getOwnerId() != player.getObjectId() || item.getLocation() != ItemLocation.MARKET)
				{
					requestFailed(SystemMessageId.NO_ITEM_DEPOSITED_IN_WH);
					return;
				}
				if (wi.getCount() < 1 || item.getCount() < wi.getCount())
				{
					requestFailed(SystemMessageId.NO_ITEM_DEPOSITED_IN_WH);
					return;
				}

				weight += (int)(wi.getCount() * item.getItem().getWeight());
				if (!item.isStackable())
					slots += wi.getCount();
				else if (player.getInventory().getItemByItemId(item.getItemId()) == null)
					slots++;
			}

			// Лимиты
			if (!player.getInventory().validateCapacity(slots))
			{
				requestFailed(SystemMessageId.SLOTS_FULL);
				return;
			}
			if (!player.getInventory().validateWeight(weight))
			{
				requestFailed(SystemMessageId.WEIGHT_LIMIT_EXCEEDED);
				return;
			}

			// (2) Перенос
			InventoryUpdate playerIU = Config.FORCE_INVENTORY_UPDATE ? null : new InventoryUpdate();
			MarketContainer box = new MarketContainer(player);

			for (WarehouseItem wi : _items)
			{
				// прикрепим живой инстанс к MARKET-контейнеру
				L2Object obj = L2World.getInstance().findObject(wi.getObjectId());
				if (!(obj instanceof L2ItemInstance))
				{
					requestFailed(SystemMessageId.NO_ITEM_DEPOSITED_IN_WH);
					return;
				}
				L2ItemInstance oldItem = (L2ItemInstance)obj;
				if (oldItem.getOwnerId() != player.getObjectId() || oldItem.getLocation() != ItemLocation.MARKET)
				{
					requestFailed(SystemMessageId.NO_ITEM_DEPOSITED_IN_WH);
					return;
				}
				if (oldItem.getCount() < wi.getCount())
				{
					requestFailed(SystemMessageId.NO_ITEM_DEPOSITED_IN_WH);
					return;
				}

				// Добавим в контейнер и перенесём в инвентарь
				box.attachExisting(oldItem.getObjectId());
				L2ItemInstance newItem = box.transferItem(
						"MarketWithdraw", oldItem.getObjectId(), wi.getCount(), player.getInventory(), player, null);

				if (newItem == null)
				{
					requestFailed(SystemMessageId.NO_ITEM_DEPOSITED_IN_WH);
					_log.warn("Error withdrawing a market object for char " + player.getName() + " (newitem == null)");
					return;
				}

				if (playerIU != null)
				{
					// если объединение со стеком — modified, иначе new
					if (newItem.getCount() > wi.getCount())
						playerIU.addModifiedItem(newItem);
					else
						playerIU.addNewItem(newItem);
				}

				// (3) Обновим market_listings: если остатка нет — CANCELLED, иначе уменьшим count
				try (java.sql.Connection con = com.l2jfree.L2DatabaseFactory.getInstance().getConnection())
				{
					// осталось ли что-то в MARKET по этому object_id?
					L2ItemInstance remain = box.getItemByObjectId(wi.getObjectId());
					if (remain == null || remain.getCount() <= 0)
					{
						try (java.sql.PreparedStatement ps = con.prepareStatement(
								"UPDATE market_listings SET status='CANCELLED' WHERE item_object_id=? AND owner_char_id=? AND status='LISTED'"))
						{
							ps.setInt(1, wi.getObjectId());
							ps.setInt(2, player.getObjectId());
							ps.executeUpdate();
						}
					}
					else
					{
						try (java.sql.PreparedStatement ps = con.prepareStatement(
								"UPDATE market_listings SET count=count-? WHERE item_object_id=? AND owner_char_id=? AND status='LISTED' AND count>=?"))
						{
							ps.setLong(1, wi.getCount());
							ps.setInt(2, wi.getObjectId());
							ps.setInt(3, player.getObjectId());
							ps.setLong(4, wi.getCount());
							ps.executeUpdate();
						}
					}
				}
				catch (Exception e)
				{
					_log.warn("Market listings update failed for char " + player.getName() + ": " + e.getMessage(), e);
				}
			}

			// (4) Отправим обновления клиенту
			if (playerIU != null)
				player.sendPacket(playerIU);
			else
				player.sendPacket(new ItemList(player, false));

			StatusUpdate su = new StatusUpdate(player.getObjectId());
			su.addAttribute(StatusUpdate.CUR_LOAD, player.getCurrentLoad());
			sendPacket(su);

			// выйти из режима рынка
			player.stopMarketWithdrawModal();
			sendAF();
			return;
		}

		/* =========================================================
		 * Ниже — СТАНДАРТНОЕ ПОВЕДЕНИЕ СКЛАДА (без изменений)
		 * ========================================================= */
		
		ItemContainer warehouse = player.getActiveWarehouse();
		if (warehouse == null)
		{
			requestFailed(SystemMessageId.TRY_AGAIN_LATER);
			return;
		}
		
		L2Npc manager = player.getLastFolkNPC();
		if ((manager == null || !manager.isWarehouse() || !player.isInsideRadius(manager, L2Npc.INTERACTION_DISTANCE,
				false, false)) && !player.isGM())
		{
			requestFailed(SystemMessageId.WAREHOUSE_TOO_FAR);
			return;
		}
		
		if (warehouse instanceof ClanWarehouse && Config.GM_DISABLE_TRANSACTION
				&& player.getAccessLevel() >= Config.GM_TRANSACTION_MIN
				&& player.getAccessLevel() <= Config.GM_TRANSACTION_MAX)
		{
			requestFailed(SystemMessageId.ACCOUNT_CANT_TRADE_ITEMS);
			return;
		}
		
		// Alt game - Karma punishment
		if (!Config.ALT_GAME_KARMA_PLAYER_CAN_USE_WAREHOUSE && player.getKarma() > 0)
		{
			sendAF();
			return;
		}
		
		if (Config.ALT_MEMBERS_CAN_WITHDRAW_FROM_CLANWH)
		{
			if (warehouse instanceof ClanWarehouse && !L2Clan.checkPrivileges(player, L2Clan.CP_CL_VIEW_WAREHOUSE))
			{
				requestFailed(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
				return;
			}
		}
		else if (warehouse instanceof ClanWarehouse && !player.isClanLeader())
		{
			requestFailed(SystemMessageId.ONLY_THE_CLAN_LEADER_IS_ENABLED);
			return;
		}
		
		int weight = 0;
		int slots = 0;
		
		for (WarehouseItem i : _items)
		{
			// Calculate needed slots
			L2ItemInstance item = warehouse.getItemByObjectId(i.getObjectId());
			if (item == null || item.getCount() < i.getCount())
			{
				requestFailed(SystemMessageId.NO_ITEM_DEPOSITED_IN_WH);
				return;
			}
			
			weight += i.getCount() * item.getItem().getWeight();
			if (!item.isStackable())
				slots += i.getCount();
			else if (player.getInventory().getItemByItemId(item.getItemId()) == null)
				slots++;
		}
		
		// Item Max Limit Check
		if (!player.getInventory().validateCapacity(slots))
		{
			requestFailed(SystemMessageId.SLOTS_FULL);
			return;
		}
		
		// Weight limit Check
		if (!player.getInventory().validateWeight(weight))
		{
			requestFailed(SystemMessageId.WEIGHT_LIMIT_EXCEEDED);
			return;
		}
		
		// Proceed to the transfer
		InventoryUpdate playerIU = Config.FORCE_INVENTORY_UPDATE ? null : new InventoryUpdate();
		for (WarehouseItem i : _items)
		{
			L2ItemInstance oldItem = warehouse.getItemByObjectId(i.getObjectId());
			if (oldItem == null || oldItem.getCount() < i.getCount())
			{
				requestFailed(SystemMessageId.NO_ITEM_DEPOSITED_IN_WH);
				_log.warn("Error withdrawing a warehouse object for char " + player.getName() + " (olditem == null)");
				return;
			}
			L2ItemInstance newItem =
					warehouse.transferItem(warehouse.getName(), i.getObjectId(), i.getCount(), player.getInventory(),
							player, manager);
			if (newItem == null)
			{
				requestFailed(SystemMessageId.NO_ITEM_DEPOSITED_IN_WH);
				_log.warn("Error withdrawing a warehouse object for char " + player.getName() + " (newitem == null)");
				return;
			}
			
			if (playerIU != null)
			{
				if (newItem.getCount() > i.getCount())
					playerIU.addModifiedItem(newItem);
				else
					playerIU.addNewItem(newItem);
			}
		}
		
		// Send updated item list to the player
		if (playerIU != null)
			player.sendPacket(playerIU);
		else
			player.sendPacket(new ItemList(player, false));
		
		// Update current load status on player
		StatusUpdate su = new StatusUpdate(player.getObjectId());
		su.addAttribute(StatusUpdate.CUR_LOAD, player.getCurrentLoad());
		sendPacket(su);
		
		sendAF();
	}
	
	private class WarehouseItem
	{
		private final int _objectId;
		private final long _count;
		
		public WarehouseItem(int id, long num)
		{
			_objectId = id;
			_count = num;
		}
		
		public int getObjectId()
		{
			return _objectId;
		}
		
		public long getCount()
		{
			return _count;
		}
	}
	
	@Override
	public String getType()
	{
		return _C__32_SENDWAREHOUSEWITHDRAWLIST;
	}
}
