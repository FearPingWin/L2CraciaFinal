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

import com.l2jfree.Config;
import com.l2jfree.gameserver.ThreadPoolManager;
import com.l2jfree.gameserver.handler.IItemHandler;
import com.l2jfree.gameserver.handler.ItemHandler;
import com.l2jfree.gameserver.instancemanager.FortSiegeManager;
import com.l2jfree.gameserver.model.L2ItemInstance;
import com.l2jfree.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfree.gameserver.model.itemcontainer.Inventory;
import com.l2jfree.gameserver.network.SystemMessageId;
import com.l2jfree.gameserver.network.serverpackets.ActionFailed;
import com.l2jfree.gameserver.network.serverpackets.ItemList;
import com.l2jfree.gameserver.network.serverpackets.ShowCalculator;
import com.l2jfree.gameserver.network.serverpackets.SystemMessage;
import com.l2jfree.gameserver.templates.item.L2Armor;
import com.l2jfree.gameserver.templates.item.L2ArmorType;
import com.l2jfree.gameserver.templates.item.L2Item;
import com.l2jfree.gameserver.templates.item.L2Weapon;
import com.l2jfree.gameserver.templates.item.L2WeaponType;
import com.l2jfree.gameserver.util.FloodProtector;
import com.l2jfree.gameserver.util.FloodProtector.Protected;
import com.l2jfree.lang.L2System;

/**
 * This class ...
 * 
 * @version $Revision: 1.18.2.7.2.9 $ $Date: 2005/03/27 15:29:30 $
 */
public final class UseItem extends L2GameClientPacket
{
	public final static Log		_log			= LogFactory.getLog(UseItem.class.getName());
	private static final String	_C__14_USEITEM	= "[C] 14 UseItem";

	private int					_objectId;

	/** Weapon Equip Task */
	public class WeaponEquipTask implements Runnable
	{
		L2ItemInstance	item;
		L2PcInstance	activeChar;

		public WeaponEquipTask(L2ItemInstance it, L2PcInstance character)
		{
			item = it;
			activeChar = character;
		}

		public void run()
		{
			//If character is still engaged in strike we should not change weapon
			if (activeChar.isAttackingNow())
				return;
			// Equip or unEquip
			activeChar.useEquippableItem(item, false);
		}
	}

	@Override
	protected void readImpl()
	{
		_objectId = readD();
	}

	@Override
	protected void runImpl()
	{

		L2PcInstance activeChar = getClient().getActiveChar();

		if (activeChar == null)
			return;

		// Flood protect UseItem
		if (!FloodProtector.tryPerformAction(activeChar, Protected.USEITEM))
			return;

		if (activeChar.getPrivateStoreType() != 0)
		{
			activeChar.sendPacket(new SystemMessage(SystemMessageId.CANNOT_TRADE_DISCARD_DROP_ITEM_WHILE_IN_SHOPMODE));
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}

		if (activeChar.getActiveTradeList() != null)
			activeChar.cancelActiveTrade();

		// NOTE: disabled due to deadlocks
		// synchronized (activeChar.getInventory())
		// 	{
		L2ItemInstance item = activeChar.getInventory().getItemByObjectId(_objectId);

		if (item == null)
			return;

		if (item.isWear())
		{
			// No unequipping wear-items
			return;
		}
		if (item.getItem().getType2() == L2Item.TYPE2_QUEST)
		{
			SystemMessage sm = new SystemMessage(SystemMessageId.CANNOT_USE_QUEST_ITEMS);
			activeChar.sendPacket(sm);
			sm = null;
			return;
		}
		int itemId = item.getItemId();
		/*
		 * Alt game - Karma punishment // SOE
		 * 736  	Scroll of Escape
		 * 1538  	Blessed Scroll of Escape
		 * 1829  	Scroll of Escape: Clan Hall
		 * 1830  	Scroll of Escape: Castle
		 * 3958  	L2Day - Blessed Scroll of Escape
		 * 5858  	Blessed Scroll of Escape: Clan Hall
		 * 5859  	Blessed Scroll of Escape: Castle
		 * 6663  	Scroll of Escape: Orc Village
		 * 6664  	Scroll of Escape: Silenos Village
		 * 7117  	Scroll of Escape to Talking Island
		 * 7118  	Scroll of Escape to Elven Village
		 * 7119  	Scroll of Escape to Dark Elf Village
		 * 7120  	Scroll of Escape to Orc Village
		 * 7121  	Scroll of Escape to Dwarven Village
		 * 7122  	Scroll of Escape to Gludin Village
		 * 7123  	Scroll of Escape to the Town of Gludio
		 * 7124  	Scroll of Escape to the Town of Dion
		 * 7125  	Scroll of Escape to Floran
		 * 7126  	Scroll of Escape to Giran Castle Town
		 * 7127  	Scroll of Escape to Hardin's Private Academy
		 * 7128  	Scroll of Escape to Heine
		 * 7129  	Scroll of Escape to the Town of Oren
		 * 7130  	Scroll of Escape to Ivory Tower
		 * 7131  	Scroll of Escape to Hunters Village
		 * 7132  	Scroll of Escape to Aden Castle Town
		 * 7133  	Scroll of Escape to the Town of Goddard
		 * 7134  	Scroll of Escape to the Rune Township
		 * 7135  	Scroll of Escape to the Town of Schuttgart.
		 * 7554  	Scroll of Escape to Talking Island
		 * 7555  	Scroll of Escape to Elven Village
		 * 7556  	Scroll of Escape to Dark Elf Village
		 * 7557  	Scroll of Escape to Orc Village
		 * 7558  	Scroll of Escape to Dwarven Village
		 * 7559  	Scroll of Escape to Giran Castle Town
		 * 7618  	Scroll of Escape - Ketra Orc Village
		 * 7619  	Scroll of Escape - Varka Silenos Village
		 * 10129    Scroll of Escape : Fortress
		 * 10130    Blessed Scroll of Escape : Fortress
		 */
		if (!Config.ALT_GAME_KARMA_PLAYER_CAN_TELEPORT
				&& activeChar.getKarma() > 0
				&& (itemId == 736 || itemId == 1538 || itemId == 1829 || itemId == 1830 || itemId == 3958 || itemId == 5858 || itemId == 5859 || itemId == 6663
						|| itemId == 6664 || (itemId >= 7117 && itemId <= 7135) || (itemId >= 7554 && itemId <= 7559) || itemId == 7618 || itemId == 7619
						|| itemId == 10129 || itemId == 10130))
			return;

		// Items that cannot be used
		if (itemId == 57)
			return;

		if (activeChar.isFishing() && (itemId < 6535 || itemId > 6540))
		{
			// You cannot do anything else while fishing
			SystemMessage sm = new SystemMessage(SystemMessageId.CANNOT_DO_WHILE_FISHING_3);
			getClient().getActiveChar().sendPacket(sm);
			sm = null;
			return;
		}

		// Char cannot use item when dead
		if (activeChar.isDead())
		{
			SystemMessage sm = new SystemMessage(SystemMessageId.S1_CANNOT_BE_USED);
			sm.addItemName(item);
			getClient().getActiveChar().sendPacket(sm);
			sm = null;
			return;
		}

		// Char cannot use pet items
		if ((item.getItem() instanceof L2Armor && item.getItem().getItemType() == L2ArmorType.PET)
				|| (item.getItem() instanceof L2Weapon && item.getItem().getItemType() == L2WeaponType.PET))
		{
			SystemMessage sm = new SystemMessage(SystemMessageId.CANNOT_EQUIP_PET_ITEM); // You cannot equip a pet item.
			sm.addItemName(item);
			getClient().getActiveChar().sendPacket(sm);
			sm = null;
			return;
		}

		if (_log.isDebugEnabled())
			_log.info(activeChar.getObjectId() + ": use item " + _objectId);

		if (!item.isEquipped())
		{
			if (!item.getItem().checkCondition(activeChar, activeChar))
				return;
		}

		if (item.isEquipable())
		{
			// No unequipping/equipping while the player is in special conditions
			if (activeChar.isStunned() || activeChar.isSleeping() || activeChar.isParalyzed() || activeChar.isAlikeDead())
			{
				activeChar.sendMessage("Your status does not allow you to do that.");
				return;
			}

			// Don't allow hero equipment and restricted items during Olympiad
			if (activeChar.isInOlympiadMode() && (item.isHeroItem() || item.isOlyRestrictedItem()))
			{
				activeChar.sendPacket(new SystemMessage(SystemMessageId.THIS_ITEM_CANT_BE_EQUIPPED_FOR_THE_OLYMPIAD_EVENT));
				return;
			}

			switch (item.getItem().getBodyPart())
			{
			case L2Item.SLOT_LR_HAND:
			case L2Item.SLOT_L_HAND:
			case L2Item.SLOT_R_HAND:
			{
				// prevent players to equip weapon while wearing combat flag
				if (activeChar.getActiveWeaponItem() != null && activeChar.getActiveWeaponItem().getItemId() == 9819)
				{
					activeChar.sendPacket(new SystemMessage(SystemMessageId.NO_CONDITION_TO_EQUIP));
					return;
				}
				// Prevent player to remove the weapon on special conditions
				if (activeChar.isCastingNow() || activeChar.isCastingSimultaneouslyNow())
				{
					activeChar.sendPacket(new SystemMessage(SystemMessageId.CANNOT_USE_ITEM_WHILE_USING_MAGIC));
					return;
				}

				if (activeChar.isMounted())
				{
					activeChar.sendPacket(new SystemMessage(SystemMessageId.NO_CONDITION_TO_EQUIP));
					return;
				}

				if (activeChar.isDisarmed())
				{
					activeChar.sendPacket(new SystemMessage(SystemMessageId.NO_CONDITION_TO_EQUIP));
					return;
				}

				// Don't allow weapon/shield equipment if a cursed weapon is equiped
				if (activeChar.isCursedWeaponEquipped())
				{
					return;
				}

				// Don't allow other Race to Wear Kamael exclusive Weapons.
				if (!item.isEquipped() && item.getItem() instanceof L2Weapon && !activeChar.isGM())
				{
					if (activeChar.isKamaelic())
					{
						if (item.getItemType() == L2ArmorType.HEAVY)
						{
							activeChar.sendPacket(new SystemMessage(SystemMessageId.NO_CONDITION_TO_EQUIP));
							return;
						}
						if (item.getItemType() == L2ArmorType.MAGIC)
						{
							activeChar.sendPacket(new SystemMessage(SystemMessageId.NO_CONDITION_TO_EQUIP));
							return;
						}
						if (item.getItemType() == L2WeaponType.NONE)
						{
							activeChar.sendPacket(new SystemMessage(SystemMessageId.NO_CONDITION_TO_EQUIP));
							return;
						}
					}
					else
					{
						if (item.getItemType() == L2WeaponType.CROSSBOW)
						{
							activeChar.sendPacket(new SystemMessage(SystemMessageId.NO_CONDITION_TO_EQUIP));
							return;
						}
						if (item.getItemType() == L2WeaponType.RAPIER)
						{
							activeChar.sendPacket(new SystemMessage(SystemMessageId.NO_CONDITION_TO_EQUIP));
							return;
						}
						if (item.getItemType() == L2WeaponType.ANCIENT_SWORD)
						{
							activeChar.sendPacket(new SystemMessage(SystemMessageId.NO_CONDITION_TO_EQUIP));
							return;
						}
					}
				}
				break;
			}
			case L2Item.SLOT_CHEST:
			case L2Item.SLOT_BACK:
			case L2Item.SLOT_GLOVES:
			case L2Item.SLOT_FEET:
			case L2Item.SLOT_HEAD:
			case L2Item.SLOT_FULL_ARMOR:
			case L2Item.SLOT_LEGS:
			{
				if (activeChar.getRace() == com.l2jfree.gameserver.model.base.Race.Kamael
						&& (item.getItem().getItemType() == L2ArmorType.HEAVY || item.getItem().getItemType() == L2ArmorType.MAGIC))
				{
					activeChar.sendPacket(new SystemMessage(SystemMessageId.NO_CONDITION_TO_EQUIP));
					return;
				}
				break;
			}
			case L2Item.SLOT_DECO:
			{
				if (!item.isEquipped() && activeChar.getInventory().getMaxTalismanCount() == 0)
				{
					activeChar.sendPacket(new SystemMessage(SystemMessageId.NO_CONDITION_TO_EQUIP));
					return;
				}
			}
			}

			if (activeChar.isCursedWeaponEquipped() && itemId == 6408) // Don't allow to put formal wear
			{
				return;
			}
			if (activeChar.isAttackingNow())
			{
				ThreadPoolManager.getInstance().scheduleGeneral(new WeaponEquipTask(item, activeChar),
					activeChar.getAttackEndTime() - L2System.milliTime());
				return;
			}
			// Equip or unEquip
			if (FortSiegeManager.getInstance().isCombat(item.getItemId()))
				return; //no message
			activeChar.useEquippableItem(item, true);
		}
		else
		{
			L2Weapon weaponItem = activeChar.getActiveWeaponItem();
			int itemid = item.getItemId();
			//_log.finest("item not equipable id:"+ item.getItemId());
			if (itemid == 4393)
			{
				activeChar.sendPacket(new ShowCalculator(4393));
			}
			else if ((weaponItem != null && weaponItem.getItemType() == L2WeaponType.ROD)
					&& ((itemid >= 6519 && itemid <= 6527) || (itemid >= 7610 && itemid <= 7613) || (itemid >= 7807 && itemid <= 7809)
							|| (itemid >= 8484 && itemid <= 8486) || (itemid >= 8505 && itemid <= 8513)))
			{
				activeChar.getInventory().setPaperdollItem(Inventory.PAPERDOLL_LHAND, item);
				activeChar.broadcastUserInfo();
				// Send a Server->Client packet ItemList to this L2PcINstance to update left hand equipement
				ItemList il = new ItemList(activeChar, false);
				sendPacket(il);
				return;
			}
			else
			{
				IItemHandler handler = ItemHandler.getInstance().getItemHandler(item.getItemId());

				if (handler == null)
				{
					if (_log.isDebugEnabled())
						_log.warn("No item handler registered for item ID " + item.getItemId() + ".");
				}
				else
					handler.useItem(activeChar, item);
			}
		}
		//		}
	}

	@Override
	public String getType()
	{
		return _C__14_USEITEM;
	}
}