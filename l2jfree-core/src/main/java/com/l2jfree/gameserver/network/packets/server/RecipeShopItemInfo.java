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
package com.l2jfree.gameserver.network.packets.server;

import com.l2jfree.gameserver.gameobjects.L2Player;
import com.l2jfree.gameserver.network.packets.L2ServerPacket;

/**
 * ddddd
 * @version $Revision: 1.1.2.3.2.3 $ $Date: 2005/03/27 15:29:39 $
 */
public class RecipeShopItemInfo extends L2ServerPacket
{
	private static final String _S__DA_RecipeShopItemInfo = "[S] da RecipeShopItemInfo";

	// режим игрока (старый) или НПЦ (новый)
	private final boolean _npcMode;

	// данные для режима игрока
	private final L2Player _crafter;

	// общие поля
	private final int _manufacturerObjId;
	private final int _recipeId;
	private final int _curMp;
	private final int _maxMp;
	private final int _price; // int по протоколу (последнее writeD)

	/** Старый конструктор: производитель — игрок, цена = 0xFFFFFFFF */
	public RecipeShopItemInfo(L2Player crafter, int recipeId)
	{
		_npcMode = false;
		_crafter = crafter;
		_manufacturerObjId = crafter.getObjectId();
		_recipeId = recipeId;
		_curMp = (int)crafter.getStatus().getCurrentMp();
		_maxMp = crafter.getMaxMp();
		_price = 0xFFFFFFFF; // как было раньше
	}

	/** Новый конструктор: производитель — НПЦ (или кастом), MP задаются явно, цена — из БД */
	public RecipeShopItemInfo(int manufacturerObjId, int recipeId, int currentMp, int maxMp, int price)
	{
		_npcMode = true;
		_crafter = null;
		_manufacturerObjId = manufacturerObjId;
		_recipeId = recipeId;
		_curMp = currentMp;
		_maxMp = maxMp;
		_price = price;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0xe0);
		if (_npcMode)
		{
			writeD(_manufacturerObjId);
			writeD(_recipeId);
			writeD(_curMp);
			writeD(_maxMp);
			writeD(_price);
		}
		else
		{
			writeD(_manufacturerObjId);
			writeD(_recipeId);
			writeD(_curMp);
			writeD(_maxMp);
			writeD(_price); // = 0xFFFFFFFF
		}
	}

	@Override
	public String getType()
	{
		return _S__DA_RecipeShopItemInfo;
	}
}
