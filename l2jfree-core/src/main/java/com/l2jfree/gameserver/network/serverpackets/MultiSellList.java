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
package com.l2jfree.gameserver.network.serverpackets;

import com.l2jfree.gameserver.datatables.ItemTable;
import com.l2jfree.gameserver.model.L2Multisell.MultiSellEntry;
import com.l2jfree.gameserver.model.L2Multisell.MultiSellIngredient;
import com.l2jfree.gameserver.model.L2Multisell.MultiSellListContainer;
import com.l2jfree.gameserver.templates.item.L2Item;


/**
 * This class ...
 * 
 * @version $Revision: 1.2 $ $Date: 2004/06/27 08:12:59 $
 */
public class MultiSellList extends L2GameServerPacket
{
    private static final String _S__D0_MULTISELLLIST = "[S] D0 MultiSellList";

    protected int _listId, _page, _finished;
    protected MultiSellListContainer _list;
    
    public MultiSellList(MultiSellListContainer list, int page, int finished)
    {
    	_list = list;
    	_listId = list.getListId();
    	_page = page;
    	_finished = finished;
    }
    
    @Override
    protected void writeImpl()
    {
        writeC(0xd0);
        writeD(_listId);    // list id
        writeD(_page);		// page
        writeD(_finished);	// finished
        writeD(0x28);	// size of pages
        writeD(_list == null ? 0 : _list.getEntries().size()); //list lenght
        
        if (_list != null)
        {
            for (MultiSellEntry ent : _list.getEntries())
            {
            	writeD(ent.getEntryId());
                writeC(ent.stackable());
                writeH(0x00);
                writeD(0x00);
                writeD(0x00);
                writeD(0x00);
                writeD(0x00);
                writeD(0x00);
                writeD(0x00);
                writeD(0x00);
                writeD(0x00);
                writeD(0x00);
                writeD(0x00);
                writeH(ent.getProducts().size());
                writeH(ent.getIngredients().size());
    
                for (MultiSellIngredient i: ent.getProducts())
                {
                    L2Item tpl = ItemTable.getInstance().getTemplate(i.getItemId());
                    if (tpl == null)
                        writeD(0x00);
                    else
                        writeD(tpl.getItemDisplayId());

                    writeD(0x00);
                    if (tpl == null)
                        writeH(0x00);
                    else
                        writeH(tpl.getType2());

                    writeD(i.getItemCount());
                    writeH(i.getEnchantmentLevel());
                    writeD(i.getAugmentationId());
                    writeD(i.getManaLeft());
                    writeD(0x00);
                    writeD(0x00);
                    writeD(0x00);
                    writeD(0x00);
                    writeD(0x00);
                    writeD(0x00);
                    writeD(0x00);
                    writeD(0x00);
                }

                for (MultiSellIngredient i : ent.getIngredients())
                {
                    int items = i.getItemId();
                    int typeE = 65535;
                    if (items != -200 && items != -300)
                    {
                        L2Item tpl = ItemTable.getInstance().getTemplate(items);
                        if (tpl == null)
                        {
                            writeD(items);
                        }
                        else
                        {
                            writeD(tpl.getItemDisplayId());
                            typeE = tpl.getType2();
                        }
                    }
                    else
                        writeD(items);

                    writeH(typeE);

                    writeD(i.getItemCount()); //Count
                    writeH(i.getEnchantmentLevel()); //Enchant Level
                    writeD(i.getAugmentationId());
                    writeD(i.getManaLeft());
                    writeD(0x00);
                    writeD(0x00);
                    writeD(0x00);
                    writeD(0x00);
                    writeD(0x00);
                    writeD(0x00);
                    writeD(0x00);
                    writeD(0x00);
                }
            }
        }
    }

    @Override
    public String getType()
    {
        return _S__D0_MULTISELLLIST;
    }
}
