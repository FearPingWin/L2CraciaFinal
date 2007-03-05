/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package net.sf.l2j.gameserver;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.l2j.L2DatabaseFactory;
import net.sf.l2j.gameserver.datatables.ItemTable;
import net.sf.l2j.gameserver.datatables.NpcTable;
import net.sf.l2j.gameserver.model.L2ItemInstance;
import net.sf.l2j.gameserver.model.L2TradeList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *  This class manages buylists from database
 * 
 * @version $Revision: 1.5.4.13 $ $Date: 2005/04/06 16:13:38 $
 */
public class TradeController
{
	private final static Log _log = LogFactory.getLog(TradeController.class.getName());
	private static TradeController _instance;

	private int _nextListId;
	private FastMap<Integer, L2TradeList> _lists;

	public static TradeController getInstance()
	{
		if (_instance == null)
		{
			_instance = new TradeController();
		}
		return _instance;
	}

	private TradeController()
	{
		_lists = new FastMap<Integer, L2TradeList>();

        java.sql.Connection con = null;

        try
        {
            con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement1 = con.prepareStatement("SELECT " + L2DatabaseFactory.getInstance().safetyString(new String[]
                { "shop_id", "npc_id" }) + " FROM merchant_shopids");
            ResultSet rset1 = statement1.executeQuery();
            while (rset1.next())
            {
                PreparedStatement statement = con.prepareStatement("SELECT " + L2DatabaseFactory.getInstance().safetyString(new String[]
                    { "item_id", "price", "shop_id", "order" }) + " FROM merchant_buylists WHERE shop_id=? ORDER BY "
                        + L2DatabaseFactory.getInstance().safetyString(new String[]
                            { "order" }) + " ASC");
                statement.setString(1, String.valueOf(rset1.getInt("shop_id")));
                ResultSet rset = statement.executeQuery();
                
                L2TradeList buylist = new L2TradeList(rset1.getInt("shop_id"));
                
                int _itemId = 0;
                int _itemCount = 0;
                int _price = 0;
                boolean _isGm = rset1.getString("npc_id").equals("gm"); 
                
                if (!_isGm && NpcTable.getInstance().getTemplate(rset1.getInt("npc_id")) == null)
                    _log.warn("TradeController: Merchant id " + rset1.getString("npc_id") + " with buylist " + buylist.getListId() + " not exist.");

                try
                {
                    while (rset.next())
                    {
                        _itemId = rset.getInt("item_id");
                        _price = rset.getInt("price");
                        L2ItemInstance buyItem = ItemTable.getInstance().createDummyItem(_itemId);
                        if (buyItem == null) continue;
                        _itemCount++;
                        buyItem.setPriceToSell(_price);
                        buylist.addItem(buyItem);
                        if (!_isGm && buyItem.getReferencePrice()>_price)
                            _log.warn("TradeController: Reference price of item " + _itemId + " in  buylist " + buylist.getListId() + " higher tnen sell price.");
                        
                    }
                } catch (Exception e)
                {
                    _log.warn("TradeController: Empty buylist " + buylist.getListId() + ".");
                }
                
                if (_itemCount>0)
                {
                    _lists.put(new Integer(buylist.getListId()), buylist);
                    _nextListId = Math.max(_nextListId, buylist.getListId() + 1);
                    buylist.setNpcId(rset1.getString("npc_id"));
                }
                else     
                    _log.warn("TradeController: Problem with buylist " + buylist.getListId() + " item " + _itemId + ".");
                
                rset.close();
                statement.close();
            }
            rset1.close();
            statement1.close();

            _log.info("TradeController: Loaded " + _lists.size() + " Buylists.");
        } catch (Exception e)
        {
            // problem with initializing buylists, go to next one
            _log.warn("TradeController: Buylists could not be initialized.",e);
        } finally
        {
            try
            {
                con.close();
            } catch (Exception e)
            {}
        }
	}

	public L2TradeList getBuyList(int listId)
	{
		return _lists.get(listId);
	}

	public FastList<L2TradeList> getBuyListByNpcId(int npcId)
	{
		FastList<L2TradeList> lists = new FastList<L2TradeList>();

		for (L2TradeList list : _lists.values())
		{
			if (list.getNpcId().equals("gm"))
				continue;
			if (npcId == Integer.parseInt(list.getNpcId()))
				lists.add(list);
		}

		return lists;
	}

	/**
	 * @return
	 */
	public synchronized int getNextId()
	{
		return _nextListId++;
	}
}
