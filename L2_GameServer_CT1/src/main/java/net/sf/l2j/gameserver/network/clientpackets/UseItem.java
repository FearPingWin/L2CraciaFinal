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
package net.sf.l2j.gameserver.network.clientpackets;

import java.util.Arrays;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.ai.CtrlIntention;
import net.sf.l2j.gameserver.handler.IItemHandler;
import net.sf.l2j.gameserver.handler.ItemHandler;
import net.sf.l2j.gameserver.instancemanager.CastleManager;
import net.sf.l2j.gameserver.model.Inventory;
import net.sf.l2j.gameserver.model.L2Clan;
import net.sf.l2j.gameserver.model.L2ItemInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.base.Race;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.EtcStatusUpdate;
import net.sf.l2j.gameserver.network.serverpackets.InventoryUpdate;
import net.sf.l2j.gameserver.network.serverpackets.ItemList;
import net.sf.l2j.gameserver.network.serverpackets.ShowCalculator;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;
import net.sf.l2j.gameserver.network.serverpackets.UserInfo;
import net.sf.l2j.gameserver.templates.L2ArmorType;
import net.sf.l2j.gameserver.templates.L2Item;
import net.sf.l2j.gameserver.templates.L2Weapon;
import net.sf.l2j.gameserver.templates.L2WeaponType;
import net.sf.l2j.gameserver.templates.L2ArmorType;
import net.sf.l2j.gameserver.util.FloodProtector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class ...
 * 
 * @version $Revision: 1.18.2.7.2.9 $ $Date: 2005/03/27 15:29:30 $
 */
public class UseItem extends L2GameClientPacket
{
	private final static Log	_log			= LogFactory.getLog(UseItem.class.getName());
	private static final String	_C__14_USEITEM	= "[C] 14 UseItem";
	
	private int					_objectId;
	private int					_unknown;
	
	/**
	 * packet type id 0x14 format: cd
	 * 
	 * @param decrypt
	 */
	@Override
	protected void readImpl()
	{
		_objectId = readD();
		_unknown = readD();
	}
	
	@Override
	protected void runImpl()
	{
		
		L2PcInstance activeChar = getClient().getActiveChar();
		
		if (activeChar == null)
			return;
		
		// Flood protect UseItem
		if (!FloodProtector.getInstance().tryPerformAction(activeChar.getObjectId(), FloodProtector.PROTECTED_USEITEM))
			return;
		
		if (activeChar.getPrivateStoreType() != 0)
		{
			activeChar.sendPacket(new SystemMessage(SystemMessageId.CANNOT_TRADE_DISCARD_DROP_ITEM_WHILE_IN_SHOPMODE));
			activeChar.actionFailed();
			return;
		}
		
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
			activeChar.sendPacket(new SystemMessage(SystemMessageId.CANNOT_USE_QUEST_ITEMS));
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
		 */
		if (!Config.ALT_GAME_KARMA_PLAYER_CAN_TELEPORT
				&& activeChar.getKarma() > 0
				&& (itemId == 736 || itemId == 1538 || itemId == 1829 || itemId == 1830 || itemId == 3958 || itemId == 5858 || itemId == 5859 || itemId == 6663
						|| itemId == 6664 || (itemId >= 7117 && itemId <= 7135) || (itemId >= 7554 && itemId <= 7559) || itemId == 7618 || itemId == 7619))
			return;

		L2Clan cl = activeChar.getClan();
		//A shield that can only be used by the members of a clan that owns a castle.
		if ((cl == null||cl.getHasCastle() == 0) && itemId == 7015 && Config.CASTLE_SHIELD)
		{
			activeChar.sendPacket(new SystemMessage(SystemMessageId.NO_CONDITION_TO_EQUIP));
			return;
		}

		//A shield that can only be used by the members of a clan that owns a clan hall.
		if ((cl == null|| cl.getHasHideout() == 0) && itemId == 6902 && Config.CLANHALL_SHIELD)
		{
			activeChar.sendPacket(new SystemMessage(SystemMessageId.NO_CONDITION_TO_EQUIP));
			return;
		}

		//Apella armor used by clan members may be worn by a Baron or a higher level Aristocrat.
		if ((itemId >=7860 && itemId <=7879) && Config.APELLA_ARMORS && (cl == null||activeChar.getPledgeClass() < 5))
		{
			activeChar.sendPacket(new SystemMessage(SystemMessageId.NO_CONDITION_TO_EQUIP));
			return;
		}

		//Clan Oath armor used by all clan members
		if ((itemId >=7850 && itemId <=7859) && Config.OATH_ARMORS && (cl == null))
		{
			activeChar.sendPacket(new SystemMessage(SystemMessageId.NO_CONDITION_TO_EQUIP));
			return;
		}

		//The Lord's Crown used by castle lords only
		if (itemId ==6841 && Config.CASTLE_CROWN && (cl == null||(cl.getHasCastle() == 0||!activeChar.isClanLeader())))
		{
			activeChar.sendPacket(new SystemMessage(SystemMessageId.NO_CONDITION_TO_EQUIP));
			return;
		}

		//Castle circlets used by the members of a clan that owns a castle, academy members are excluded.
		if (Config.CASTLE_CIRCLETS &&((itemId >= 6834 && itemId <= 6840)||itemId == 8182||itemId == 8183))
		{
			if (cl ==null)
			{
				activeChar.sendPacket(new SystemMessage(SystemMessageId.NO_CONDITION_TO_EQUIP));
				return;
			}
			else
			{
				int circletId = CastleManager.getInstance().getCircletByCastleId(cl.getHasCastle());
				if (activeChar.getSubPledgeType() == -1||circletId != itemId)
				{
					activeChar.sendPacket(new SystemMessage(SystemMessageId.NO_CONDITION_TO_EQUIP));
					return;
				}
			}
		}

		if(activeChar.getPkKills() > 0 && item.getItemId() >= 7816 && item.getItemId() <= 7831)
		{
			activeChar.sendPacket(new SystemMessage(SystemMessageId.YOU_ARE_UNABLE_TO_EQUIP_THIS_ITEM_WHEN_YOUR_PK_COUNT_IS_GREATER_THAN_OR_EQUAL_TO_ONE));
			return;
		}

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
		if (item.getItem().isForWolf() || item.getItem().isForHatchling() || item.getItem().isForStrider() || item.getItem().isForBabyPet())
		{
			SystemMessage sm = new SystemMessage(SystemMessageId.CANNOT_EQUIP_PET_ITEM); // You cannot equip a pet item.
			sm.addItemName(item);
			getClient().getActiveChar().sendPacket(sm);
			sm = null;
			return;
		}
		
		if (_log.isDebugEnabled())
			_log.debug(activeChar.getObjectId() + ": use item " + _objectId);
		
		if (item.isEquipable())
		{
			if (activeChar.isDisarmed())
				return;
			
			if (item.isArmor() && !item.getArmorItem().allowEquip(activeChar.getRace().ordinal(), activeChar.getClassId().getId(), activeChar.getAppearance().getSex()))
			{
				activeChar.sendPacket(new SystemMessage(SystemMessageId.NO_CONDITION_TO_EQUIP));
				return;
			}
			else if (item.isWeapon() && !item.getWeaponItem().allowEquip(activeChar.getRace().ordinal(), activeChar.getClassId().getId(), activeChar.getAppearance().getSex()))
			{
				activeChar.sendPacket(new SystemMessage(SystemMessageId.NO_CONDITION_TO_EQUIP));
				return;
			}
			
			if (activeChar.isKamaelic())
			{
				if (item.getItemType() == L2ArmorType.HEAVY && !Config.KAMAEL_CAN_USE_HEAVY)
				{
					activeChar.sendPacket(new SystemMessage(SystemMessageId.NO_CONDITION_TO_EQUIP));
					return;
				}
				if (item.getItemType() == L2ArmorType.MAGIC && !Config.KAMAEL_CAN_USE_MAGIC)
				{
					activeChar.sendPacket(new SystemMessage(SystemMessageId.NO_CONDITION_TO_EQUIP));
					return;
				}
				if (item.getItemType() == L2WeaponType.NONE && !Config.KAMAEL_CAN_USE_SHIELD)
				{
					activeChar.sendPacket(new SystemMessage(SystemMessageId.NO_CONDITION_TO_EQUIP));
					return;
				}
			}
			else
			{
				if (item.getItemType() == L2WeaponType.CROSSBOW && !Config.HUMAN_CAN_USE_CROSSBOW)
				{
					activeChar.sendPacket(new SystemMessage(SystemMessageId.NO_CONDITION_TO_EQUIP));
					return;
				}
				if (item.getItemType() == L2WeaponType.RAPIER && !Config.HUMAN_CAN_USE_RAPIER)
				{
					activeChar.sendPacket(new SystemMessage(SystemMessageId.NO_CONDITION_TO_EQUIP));
					return;
				}
				if (item.getItemType() == L2WeaponType.ANCIENT_SWORD && !Config.HUMAN_CAN_USE_ANCIENT)
				{
					activeChar.sendPacket(new SystemMessage(SystemMessageId.NO_CONDITION_TO_EQUIP));
					return;
				}
			}
			
			// No unequipping/equipping while the player is in special conditions
			if (activeChar.isStunned() || activeChar.isSleeping() || activeChar.isParalyzed() || activeChar.isAlikeDead())
			{
				activeChar.sendMessage("Your status does not allow you to do that.");
				return;
			}
			
			int bodyPart = item.getItem().getBodyPart();
			
			// Prevent player to remove the weapon on special conditions
			if (bodyPart == L2Item.SLOT_LR_HAND || bodyPart == L2Item.SLOT_L_HAND || bodyPart == L2Item.SLOT_R_HAND)
			{
				if (activeChar.isCastingNow())
				{
					activeChar.sendPacket(new SystemMessage(SystemMessageId.CANNOT_USE_ITEM_WHILE_USING_MAGIC));
					return;
				}
				if (activeChar.isAttackingNow() || activeChar.isMounted() || (activeChar._inEventCTF && activeChar._haveFlagCTF))
				{
					if (activeChar._inEventCTF && activeChar._haveFlagCTF)
						activeChar.sendMessage("This item can not be equipped when you have the flag.");
					return;
				}
			}

			if (activeChar.isDisarmed()
				&& (bodyPart == L2Item.SLOT_LR_HAND
					|| bodyPart == L2Item.SLOT_L_HAND
					|| bodyPart == L2Item.SLOT_R_HAND))
			{
				activeChar.sendPacket(new SystemMessage(SystemMessageId.NO_CONDITION_TO_EQUIP));
				return;
			}

			// Don't allow weapon/shield equipment if a cursed weapon is equiped
			if (activeChar.isCursedWeaponEquipped()
					&& ((bodyPart == L2Item.SLOT_LR_HAND || bodyPart == L2Item.SLOT_L_HAND || bodyPart == L2Item.SLOT_R_HAND) || itemId == 6408)) // Don't allow to put formal wear
				return;
			
			activeChar.abortCast();
			
			// Don't allow weapon/shield hero equipment during Olympiads
			if (activeChar.isInOlympiadMode() && (bodyPart == L2Item.SLOT_LR_HAND || bodyPart == L2Item.SLOT_L_HAND || bodyPart == L2Item.SLOT_R_HAND)
					&& item.isHeroItem())
			{
				activeChar.sendPacket(new SystemMessage(SystemMessageId.THIS_ITEM_CANT_BE_EQUIPPED_FOR_THE_OLYMPIAD_EVENT));
				return;
			}

			// Equip or unEquip
			boolean isEquiped = item.isEquipped();
			SystemMessage sm = null;
			if(!isEquiped)
			{
				L2ItemInstance ae = activeChar.getInventory().getPaperdollItemByL2ItemId(bodyPart);
				L2ItemInstance tempItem;
				if(ae != null)
				{
					//check if the item replaces a wear-item
					if (ae.isWear())
					{
						// dont allow an item to replace a wear-item
						return;
					}
					else if (bodyPart == 0x4000) // left+right hand equipment
					{
						// this may not remove left OR right hand equipment
						tempItem = activeChar.getInventory().getPaperdollItem(7);
						if (tempItem != null && tempItem.isWear()) return;

						tempItem = activeChar.getInventory().getPaperdollItem(8);
						if (tempItem != null && tempItem.isWear()) return;
					}
					else if (bodyPart == 0x8000) // fullbody armor
					{
						// this may not remove chest or leggins
						tempItem = activeChar.getInventory().getPaperdollItem(10);
						if (tempItem != null && tempItem.isWear()) return;

						tempItem = activeChar.getInventory().getPaperdollItem(11);
						if (tempItem != null && tempItem.isWear()) return;
					}

					//Find Arrows if arrows are equipped in LHand:
					if (activeChar.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND) == 
						activeChar.getInventory().findArrowForBow(ae.getItem()))
					{
						//Remove arrows if they're equipped:
						activeChar.getInventory().unEquipItemInBodySlotAndRecord(Inventory.PAPERDOLL_LHAND);
						activeChar.getInventory().setPaperdollItem(Inventory.PAPERDOLL_LHAND, null);
					}
					activeChar.getInventory().unEquipItemInBodySlotAndRecord(bodyPart);
					activeChar.checkSSMatch(item, ae);
				}
				activeChar.getInventory().equipItemAndRecord(item);

				if (item.getEnchantLevel() > 0)
				{
					sm = new SystemMessage(SystemMessageId.S1_S2_EQUIPPED);
					sm.addNumber(item.getEnchantLevel());
					sm.addItemName(item);
				}
				else
				{
					sm = new SystemMessage(SystemMessageId.S1_EQUIPPED);
					sm.addItemName(item);
				}
				activeChar.sendPacket(sm);
				item.decreaseMana(false);
			}
			else
			{
				if (bodyPart == L2Item.SLOT_L_EAR || bodyPart == L2Item.SLOT_LR_EAR ||
					bodyPart == L2Item.SLOT_L_FINGER || bodyPart == L2Item.SLOT_LR_FINGER)
						activeChar.getInventory().setPaperdollItem(item.getLocationSlot(), null);

				activeChar.getInventory().unEquipItemInBodySlotAndRecord(bodyPart);
				if (item.getEnchantLevel() > 0)
				{
					sm = new SystemMessage(SystemMessageId.EQUIPMENT_S1_S2_REMOVED);
					sm.addNumber(item.getEnchantLevel());
					sm.addItemName(item);
				}
				else
				{
					sm = new SystemMessage(SystemMessageId.S1_DISARMED);
					sm.addItemName(item);
				}
				activeChar.sendPacket(sm);
			}
			activeChar.refreshExpertisePenalty();

			if(item.getItem().getType2() == 0)
				activeChar.checkIfWeaponIsAllowed();

			activeChar.abortAttack();

			activeChar.sendPacket(new EtcStatusUpdate(activeChar));
			// if an "invisible" item has changed (Jewels, helmet),
			// we dont need to send broadcast packet to all other users
			if ((item.getItem().getBodyPart() & L2Item.SLOT_HEAD) > 0 || (item.getItem().getBodyPart() & L2Item.SLOT_NECK) > 0
					|| (item.getItem().getBodyPart() & L2Item.SLOT_L_EAR) > 0 || (item.getItem().getBodyPart() & L2Item.SLOT_R_EAR) > 0
					|| (item.getItem().getBodyPart() & L2Item.SLOT_L_FINGER) > 0 || (item.getItem().getBodyPart() & L2Item.SLOT_R_FINGER) > 0)
			{
				activeChar.sendPacket(new UserInfo(activeChar));
			}
			else
			{
				activeChar.broadcastUserInfo();
			}
			activeChar.sendPacket(new ItemList(activeChar, false));
		}
		else
		{
			L2Weapon weaponItem = activeChar.getActiveWeaponItem();
			int itemid = item.getItemId();
			// _log.debug("item not equipable id:"+ item.getItemId());
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
				IItemHandler handler = ItemHandler.getInstance().getItemHandler(itemId);
				
				if (handler == null)
					_log.debug("No item handler registered for item ID " + itemId + ".");
				else
					handler.useItem(activeChar, item);
			}
		}
	}

	@Override
	public String getType()
	{
		return _C__14_USEITEM;
	}
}
