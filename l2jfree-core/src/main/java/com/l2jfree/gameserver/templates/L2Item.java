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
package com.l2jfree.gameserver.templates;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.l2jfree.Config;

/**
 * This class contains all informations concerning the item (weapon, armor, etc).<BR>
 * Mother class of :
 * <LI>L2Armor</LI>
 * <LI>L2EtcItem</LI>
 * <LI>L2Weapon</LI> 
 * @version $Revision: 1.7.2.2.2.5 $ $Date: 2005/04/06 18:25:18 $
 */
public abstract class L2Item
{
	protected final static Log	_log								= LogFactory.getLog(L2Item.class.getName());

	public static final int		TYPE1_WEAPON_RING_EARRING_NECKLACE	= 0;
	public static final int		TYPE1_SHIELD_ARMOR					= 1;
	public static final int		TYPE1_ITEM_QUESTITEM_ADENA			= 4;

	public static final int		TYPE2_WEAPON						= 0;
	public static final int		TYPE2_SHIELD_ARMOR					= 1;
	public static final int		TYPE2_ACCESSORY						= 2;
	public static final int		TYPE2_QUEST							= 3;
	public static final int		TYPE2_MONEY							= 4;
	public static final int		TYPE2_OTHER							= 5;
	public static final int		TYPE2_PET_WOLF						= 6;
	public static final int		TYPE2_PET_HATCHLING					= 7;
	public static final int		TYPE2_PET_STRIDER					= 8;
	public static final int		TYPE2_PET_BABY						= 9;
	public static final int		TYPE2_PET_GREATWOLF					= 10;

	public static final int		SLOT_NONE							= 0x00000;
	public static final int		SLOT_UNDERWEAR						= 0x00001;
	public static final int		SLOT_R_EAR							= 0x00002;
	public static final int		SLOT_L_EAR							= 0x00004;
	public static final int		SLOT_LR_EAR							= 0x00006;
	public static final int		SLOT_NECK							= 0x00008;
	public static final int		SLOT_R_FINGER						= 0x00010;
	public static final int		SLOT_L_FINGER						= 0x00020;
	public static final int		SLOT_LR_FINGER						= 0x00030;
	public static final int		SLOT_HEAD							= 0x00040;
	public static final int		SLOT_R_HAND							= 0x00080;
	public static final int		SLOT_L_HAND							= 0x00100;
	public static final int		SLOT_GLOVES							= 0x00200;
	public static final int		SLOT_CHEST							= 0x00400;
	public static final int		SLOT_LEGS							= 0x00800;
	public static final int		SLOT_FEET							= 0x01000;
	public static final int		SLOT_BACK							= 0x02000;
	public static final int		SLOT_LR_HAND						= 0x04000;
	public static final int		SLOT_FULL_ARMOR						= 0x08000;
	public static final int		SLOT_HAIR							= 0x10000;
	public static final int		SLOT_ALLDRESS						= 0x20000;
	public static final int		SLOT_HAIR2							= 0x40000;
	public static final int		SLOT_HAIRALL						= 0x80000;
	public static final int		SLOT_R_BRACELET						= 0x100000;
	public static final int		SLOT_L_BRACELET						= 0x200000;
	public static final int		SLOT_DECO							= 0x400000;
	public static final int		SLOT_WOLF							= -100;
	public static final int		SLOT_HATCHLING						= -101;
	public static final int		SLOT_STRIDER						= -102;
	public static final int		SLOT_BABYPET						= -103;
	public static final int		SLOT_GREATWOLF						= -104;

	public static final int		MATERIAL_STEEL						= 0x00;										// ??
	public static final int		MATERIAL_FINE_STEEL					= 0x01;										// ??
	public static final int		MATERIAL_BLOOD_STEEL				= 0x02;										// ??
	public static final int		MATERIAL_BRONZE						= 0x03;										// ??
	public static final int		MATERIAL_SILVER						= 0x04;										// ??
	public static final int		MATERIAL_GOLD						= 0x05;										// ??
	public static final int		MATERIAL_MITHRIL					= 0x06;										// ??
	public static final int		MATERIAL_ORIHARUKON					= 0x07;										// ??
	public static final int		MATERIAL_PAPER						= 0x08;										// ??
	public static final int		MATERIAL_WOOD						= 0x09;										// ??
	public static final int		MATERIAL_CLOTH						= 0x0a;										// ??
	public static final int		MATERIAL_LEATHER					= 0x0b;										// ??
	public static final int		MATERIAL_BONE						= 0x0c;										// ??
	public static final int		MATERIAL_HORN						= 0x0d;										// ??
	public static final int		MATERIAL_DAMASCUS					= 0x0e;										// ??
	public static final int		MATERIAL_ADAMANTAITE				= 0x0f;										// ??
	public static final int		MATERIAL_CHRYSOLITE					= 0x10;										// ??
	public static final int		MATERIAL_CRYSTAL					= 0x11;										// ??
	public static final int		MATERIAL_LIQUID						= 0x12;										// ??
	public static final int		MATERIAL_SCALE_OF_DRAGON			= 0x13;										// ??
	public static final int		MATERIAL_DYESTUFF					= 0x14;										// ??
	public static final int		MATERIAL_COBWEB						= 0x15;										// ??
	public static final int		MATERIAL_SEED						= 0x15;										// ??

	public static final int		CRYSTAL_NONE						= 0x00;										// ??
	public static final int		CRYSTAL_D							= 0x01;										// ??
	public static final int		CRYSTAL_C							= 0x02;										// ??
	public static final int		CRYSTAL_B							= 0x03;										// ??
	public static final int		CRYSTAL_A							= 0x04;										// ??
	public static final int		CRYSTAL_S							= 0x05;										// ??
	public static final int		CRYSTAL_S80							= 0x06;										// ??

	private static final int[]	crystalItemId						=
																	{ 0, 1458, 1459, 1460, 1461, 1462, 1462 };
	private static final int[]	crystalEnchantBonusArmor			=
																	{ 0, 11, 6, 11, 19, 25, 25 };
	private static final int[]	crystalEnchantBonusWeapon			=
																	{ 0, 90, 45, 67, 144, 250, 250 };

	private final int			_itemId;
	private final int			_itemDisplayId;
	private final String		_name;
	private final int			_type1;																			// needed for item list (inventory)
	private final int			_type2;																			// different lists for armor, weapon, etc
	private final int			_weight;
	private final boolean		_crystallizable;
	private final boolean		_stackable;
	private final int			_materialType;
	private final int			_crystalType;																		// default to none-grade 
	private final int			_duration;
	private final int			_bodyPart;
	private final int			_referencePrice;
	private final int			_crystalCount;
	private final boolean		_sellable;
	private final boolean		_dropable;
	private final boolean		_destroyable;
	private final boolean		_tradeable;

	protected final AbstractL2ItemType	_type;

	/**
	 * Constructor of the L2Item that fill class variables.<BR><BR>
	 * <U><I>Variables filled :</I></U><BR>
	 * <LI>type</LI>
	 * <LI>_itemId</LI>
	 * <LI>_name</LI>
	 * <LI>_type1 & _type2</LI>
	 * <LI>_weight</LI>
	 * <LI>_crystallizable</LI>
	 * <LI>_stackable</LI>
	 * <LI>_materialType & _crystalType & _crystlaCount</LI>
	 * <LI>_duration</LI>
	 * <LI>_bodypart</LI>
	 * <LI>_referencePrice</LI>
	 * <LI>_sellable</LI>
	 * <LI>_dropable</LI>
	 * <LI>_destroyable</LI>
	 * <LI>_tradeable</LI>
	 * @param type : Enum designating the type of the item
	 * @param set : StatsSet corresponding to a set of couples (key,value) for description of the item
	 */
	protected L2Item(AbstractL2ItemType type, StatsSet set)
	{
		_type = type;
		_itemId = set.getInteger("item_id");
		_itemDisplayId = set.getInteger("item_display_id");
		_name = set.getString("name");
		_type1 = set.getInteger("type1"); // needed for item list (inventory)
		_type2 = set.getInteger("type2"); // different lists for armor, weapon, etc
		_weight = set.getInteger("weight");
		_crystallizable = set.getBool("crystallizable");
		_stackable = set.getBool("stackable", false);
		_materialType = set.getInteger("material");
		_crystalType = set.getInteger("crystal_type", CRYSTAL_NONE); // default to none-grade 
		_duration = set.getInteger("duration");
		_bodyPart = set.getInteger("bodypart");
		_referencePrice = set.getInteger("price");
		_crystalCount = set.getInteger("crystal_count", 0);
		_sellable = set.getBool("sellable", true);
		_dropable = set.getBool("dropable", true);
		_destroyable = set.getBool("destroyable", true);
		_tradeable = set.getBool("tradeable", true);
	}

	/**
	 * Returns the itemType.
	 * @return Enum
	 */
	public AbstractL2ItemType getItemType()
	{
		return _type;
	}

	/**
	 * Returns the duration of the item
	 * @return int
	 */
	public final int getDuration()
	{
		return _duration;
	}

	/**
	 * Returns the ID of the iden
	 * @return int
	 */
	public final int getItemId()
	{
		return _itemId;
	}

	public final int getItemDisplayId()
	{
		return _itemDisplayId;
	}

	public abstract int getItemMask();

	/**
	 * Return the type of material of the item
	 * @return int
	 */
	public final int getMaterialType()
	{
		return _materialType;
	}

	/**
	 * Returns the type 2 of the item
	 * @return int
	 */
	public final int getType2()
	{
		return _type2;
	}

	/**
	 * Returns the weight of the item
	 * @return int
	 */
	public final int getWeight()
	{
		return _weight;
	}

	/**
	 * Returns if the item is crystallizable
	 * @return boolean
	 */
	public final boolean isCrystallizable()
	{
		return _crystallizable;
	}

	/**
	 * Return the type of crystal if item is crystallizable
	 * Check isS80 to get S80 item types
	 * @return int (if the item is S80, it will return S-grade)
	 */
	public final int getCrystalType()
	{
		return _crystalType;
	}

	/**
	 * Return the type of crystal if item is crystallizable
	 * @return int
	 */
	public final int getCrystalItemId()
	{
		return crystalItemId[_crystalType];
	}

	/**
	 * Returns the grade of the item.<BR><BR>
	 * <U><I>Concept :</I></U><BR>
	 * In fact, this fucntion returns the type of crystal of the item.
	 * @return int
	 */
	public final int getItemGrade()
	{
		return getCrystalType();
	}

	/**
	 * Returns the quantity of crystals for crystallization
	 * @return int
	 */
	public final int getCrystalCount()
	{
		return _crystalCount;
	}

	/**
	 * Returns the quantity of crystals for crystallization on specific enchant level
	 * @return int
	 */
	public final int getCrystalCount(int enchantLevel)
	{
		if (enchantLevel > 3)
			switch (_type2)
			{
			case TYPE2_SHIELD_ARMOR:
			case TYPE2_ACCESSORY:
				return _crystalCount + crystalEnchantBonusArmor[getCrystalType()] * (3 * enchantLevel - 6);
			case TYPE2_WEAPON:
				return _crystalCount + crystalEnchantBonusWeapon[getCrystalType()] * (2 * enchantLevel - 3);
			default:
				return _crystalCount;
			}
		else if (enchantLevel > 0)
			switch (_type2)
			{
			case TYPE2_SHIELD_ARMOR:
			case TYPE2_ACCESSORY:
				return _crystalCount + crystalEnchantBonusArmor[getCrystalType()] * enchantLevel;
			case TYPE2_WEAPON:
				return _crystalCount + crystalEnchantBonusWeapon[getCrystalType()] * enchantLevel;
			default:
				return _crystalCount;
			}
		else
			return _crystalCount;
	}

	/**
	 * Returns the name of the item
	 * @return String
	 */
	public final String getName()
	{
		return _name;
	}

	/**
	 * Return the part of the body used with the item.
	 * @return int
	 */
	public final int getBodyPart()
	{
		return _bodyPart;
	}

	/**
	 * Returns the type 1 of the item
	 * @return int
	 */
	public final int getType1()
	{
		return _type1;
	}

	/**
	 * Returns if the item is stackable
	 * @return boolean
	 */
	public final boolean isStackable()
	{
		return _stackable;
	}

	/**
	 * Returns if the item is consumable
	 * @return boolean
	 */
	public boolean isConsumable()
	{
		return false;
	}

	/**
	 * Returns if the item is a heroitem
	 * @return boolean
	 */
	public boolean isHeroItem()
	{
		return ((_itemId >= 6611 && _itemId <= 6621) || (_itemId >= 9388 && _itemId <= 9390) || _itemId == 6842);
	}

	public boolean isCommonItem()
	{
		return ((_itemId >= 12006 && _itemId <= 12361) || (_itemId >= 11605 && _itemId <= 12308));
	}

	public boolean isEquipable()
	{
		return this.getBodyPart() != 0 && !(this.getItemType() instanceof L2EtcItemType);
	}

	/**
	 * Returns the price of reference of the item
	 * @return int
	 */
	public final int getReferencePrice()
	{
		return (isConsumable() ? (int) (_referencePrice * Config.RATE_CONSUMABLE_COST) : _referencePrice);
	}

	/**
	 * Returns if the item can be sold
	 * @return boolean
	 */
	public final boolean isSellable()
	{
		return _sellable;
	}

	/**
	 * Returns if the item can dropped
	 * @return boolean
	 */
	public final boolean isDropable()
	{
		return _dropable;
	}

	/**
	 * Returns if the item can destroy
	 * @return boolean
	 */
	public final boolean isDestroyable()
	{
		return _destroyable;
	}

	/**
	 * Returns if the item can add to trade
	 * @return boolean
	 */
	public final boolean isTradeable()
	{
		return _tradeable;
	}

	/**
	 * Returns if item is for hatchling
	 * @return boolean
	 */
	public boolean isForHatchling()
	{
		return (_type2 == TYPE2_PET_HATCHLING);
	}

	/**
	 * Returns if item is for strider
	 * @return boolean
	 */
	public boolean isForStrider()
	{
		return (_type2 == TYPE2_PET_STRIDER);
	}

	/**
	 * Returns if item is for wolf
	 * @return boolean
	 */
	public boolean isForWolf()
	{
		return (_type2 == TYPE2_PET_WOLF);
	}

	/**
	 * Returns if item is for Great wolf
	 * @return boolean
	 */
	public boolean isForGreatWolf()
	{
		return (_type2 == TYPE2_PET_GREATWOLF);
	}

	/**
	 * Returns if item is for wolf
	 * @return boolean
	 */
	public boolean isForBabyPet()
	{
		return (_type2 == TYPE2_PET_BABY);
	}

	/**
	* Returns the name of the item
	* @return String
	*/
	@Override
	public String toString()
	{
		return _name;
	}
}