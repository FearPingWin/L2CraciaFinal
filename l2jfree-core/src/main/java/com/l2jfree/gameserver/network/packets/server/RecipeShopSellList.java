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

import java.util.List;
import com.l2jfree.gameserver.gameobjects.L2Player;
import com.l2jfree.gameserver.model.items.manufacture.L2ManufactureItem;
import com.l2jfree.gameserver.model.items.manufacture.L2ManufactureList;
import com.l2jfree.gameserver.network.packets.L2ServerPacket;

/**
 * This class ...
 * dddd d(ddd)
 * @version $Revision: 1.1.2.1.2.3 $ $Date: 2005/03/27 15:29:39 $
 */
public class RecipeShopSellList extends L2ServerPacket
{
	private static final String _S__D9_RecipeShopSellList = "[S] d9 RecipeShopSellList";
	
	private final L2Player _buyer;
	private final L2Player _manufacturer;
	
	private final boolean _npcMode;
	private final int _manufacturerObjId;
	private final L2ManufactureList _customList;
	private final long _adena;
	
	public RecipeShopSellList(L2Player buyer, L2Player manufacturer)
	{
		_buyer = buyer;
		_manufacturer = manufacturer;
		_npcMode = false;
		_manufacturerObjId = manufacturer.getObjectId();
		_customList = null;
		_adena = buyer != null ? buyer.getAdena() : 0L;
	}
	
	/** NPC mode: buyer provided, manufacturer by objectId, custom list provided */
	public RecipeShopSellList(L2Player buyer, int manufacturerObjId, L2ManufactureList list)
	{
		_buyer = buyer;
		_manufacturer = null;
		_npcMode = true;
		_manufacturerObjId = manufacturerObjId;
		_customList = list;
		_adena = buyer != null ? buyer.getAdena() : 0L;
	}
	
	/** NPC mode: buyer adena provided explicitly (useful if buyer object not passed) */
	public RecipeShopSellList(int manufacturerObjId, long buyerAdena, L2ManufactureList list)
	{
		_buyer = null;
		_manufacturer = null;
		_npcMode = true;
		_manufacturerObjId = manufacturerObjId;
		_customList = list;
		_adena = buyerAdena;
	}
	
	@Override
	protected final void writeImpl()
	{
		if (_npcMode)
		{
			final List<L2ManufactureItem> list = _customList != null ? _customList.getList() : java.util.Collections.<L2ManufactureItem>emptyList();
			
			writeC(0xdf);
			writeD(_manufacturerObjId);
			writeD(0); // MP
			writeD(0); // Max MP
			writeCompQ(_adena);
			
			writeD(list.size());
			for (L2ManufactureItem mi : list)
			{
				writeD(mi.getRecipeId());
				writeD(0x00);
				writeCompQ(mi.getCost());
			}
			return;
		}
		
		L2ManufactureList createList = _manufacturer != null ? _manufacturer.getCreateList() : null;
		if (createList == null)
			return;
		
		writeC(0xdf);
		writeD(_manufacturer.getObjectId());
		writeD((int)_manufacturer.getStatus().getCurrentMp());
		writeD(_manufacturer.getMaxMp());
		writeCompQ(_buyer != null ? _buyer.getAdena() : 0L);
		
		int count = createList.size();
		writeD(count);
		for (int i = 0; i < count; i++)
		{
			L2ManufactureItem temp = createList.getList().get(i);
			writeD(temp.getRecipeId());
			writeD(0x00);
			writeCompQ(temp.getCost());
		}
	}
	
	@Override
	public String getType()
	{
		return _S__D9_RecipeShopSellList;
	}
}
