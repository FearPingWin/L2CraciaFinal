package com.l2jfree.gameserver.handler.itemhandlers;

import com.l2jfree.gameserver.datatables.SkillTable;
import com.l2jfree.gameserver.handler.IItemHandler;
import com.l2jfree.gameserver.model.L2ItemInstance;
import com.l2jfree.gameserver.model.L2Skill;
import com.l2jfree.gameserver.model.actor.L2Playable;
import com.l2jfree.gameserver.model.actor.instance.L2PcInstance;

public class ItemSkills implements IItemHandler
{
	private static final int[]	ITEM_IDS	=
											{
			6403,
			6406,
			6407,
			13268,
			13269,
			22039,
			22040,
			22041,
			22042,
			22043,
			22044,
			22045,
			22046,
			22047,
			22048,
			22049,
			22050,
			22051,
			22052,
			22053,
			22089,
			22090,
			22091,
			22092,
			22093,
			22094,
			22095,
			22096,
			22097,
			22098,
			22099,
			22100,
			22101,
			22102,
			22103,
			22104,
			22105,
			22106,
			22107,
			22108,
			22109,
			22110,
			22111,
			22112,
			22113,
			22114,
			22115,
			22116,
			22117,
			22118,
			22119,
			22120,
			22121,
			22122,
			22123,
			22124,
			22125,
			22126,
			22127,
			22128,
			22129,
			22130,
			22131,
			22132,
			22133,
			22134,
			22135,
			22136,
			22137,
			22138,
			22139,
			22140,
			22141,
			22142,
			22143,
			22149,
			22150,
			22151,
			22152,
			22153							};

	/**
	 * 
	 * @see com.l2jfree.gameserver.handler.IItemHandler#useItem(com.l2jfree.gameserver.model.actor.L2Playable, com.l2jfree.gameserver.model.L2ItemInstance)
	 */
	public void useItem(L2Playable playable, L2ItemInstance item)
	{
		if (!(playable instanceof L2PcInstance))
			return; // prevent Class cast exception
		L2PcInstance activeChar = (L2PcInstance) playable;
		int skillId = 0;
		int skillLvl = 1;
		int itemId = item.getItemId();
		switch (itemId)
		{
		case 6403:
			skillId = 2023;
			break;
		case 6406:
			skillId = 2024;
			break;
		case 6407:
			skillId = 2025;
			break;
		case 13268:
			skillId = 2604;
			break;
		case 13269:
			skillId = 2605;
			break;
		case 22089:
		case 22090:
		case 22091:
		case 22092:
		case 22093:
			skillId = 26067;
			skillLvl = itemId - 22088;
			break;
		case 22094:
		case 22095:
		case 22096:
		case 22097:
		case 22098:
			skillId = 26068;
			skillLvl = itemId - 22093;
			break;
		case 22099:
		case 22100:
		case 22101:
		case 22102:
		case 22103:
			skillId = 26069;
			skillLvl = itemId - 22098;
			break;
		case 22104:
		case 22105:
		case 22106:
		case 22107:
		case 22108:
			skillId = 26070;
			skillLvl = itemId - 22103;
			break;
		case 22109:
		case 22110:
		case 22111:
		case 22112:
		case 22113:
			skillId = 26068;
			skillLvl = itemId - 22103;
			break;
		case 22114:
		case 22115:
		case 22116:
		case 22117:
		case 22118:
			skillId = 26069;
			skillLvl = itemId - 22108;
			break;
		case 22119:
		case 22120:
		case 22121:
		case 22122:
		case 22123:
			skillId = 26070;
			skillLvl = itemId - 22113;
			break;
		case 22124:
		case 22125:
		case 22126:
		case 22127:
		case 22128:
		case 22129:
		case 22130:
		case 22131:
		case 22132:
		case 22133:
		case 22134:
		case 22135:
		case 22136:
		case 22137:
		case 22138:
		case 22139:
		case 22140:
			skillId = 26071;
			skillLvl = itemId - 22123;
			break;
		case 22141:
		case 22142:
		case 22143:
			skillId = 26072;
			skillLvl = itemId - 22140;
			break;
		case 22149:
		case 22150:
		case 22151:
		case 22152:
		case 22153:
			skillId = 26073;
			skillLvl = itemId - 22148;
			break;
		}

		L2Skill skill = SkillTable.getInstance().getInfo(skillId, skillLvl);
		if (skill != null)
			activeChar.useMagic(skill, false, false);
	}

	/**
	 * @see com.l2jfree.gameserver.handler.IItemHandler#getItemIds()
	 */
	public int[] getItemIds()
	{
		return ITEM_IDS;
	}
}