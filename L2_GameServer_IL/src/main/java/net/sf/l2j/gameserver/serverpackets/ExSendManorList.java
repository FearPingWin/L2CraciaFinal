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
package net.sf.l2j.gameserver.serverpackets;

import javolution.util.FastList;

/**
 * Format : (h) d [dS]
 * h this H is the sub-id (1B)
 * d: number of manors
 * [
 * d: manor id ? or just incremental (i coded as if it was incremental)
 * S: manor name
 * ]
 * @author -Wooden-
 *
 */
public class ExSendManorList extends L2GameServerPacket
{
    private static final String _S__FE_1B_EXSENDMANORLIST = "[S] FE:1B ExSendManorList";
    FastList<String> _manors;
    FastList<Integer> _manorsID;
    public ExSendManorList(FastList<String> manors, FastList<Integer> manorsID)
    {
        _manors = manors;
        _manorsID = manorsID;
    }
    
    
    /* (non-Javadoc)
     * @see net.sf.l2j.gameserver.serverpackets.ServerBasePacket#writeImpl()
     */
    @Override
    protected void writeImpl()
    {
        writeC(0xFE);
        writeH(0x1B);
        writeD(_manors.size());
        for(int i=0;i<_manors.size();i++)
        {
            writeD(_manorsID.get(i));
            writeS(_manors.get(i));
        }
        
    }

    /* (non-Javadoc)
     * @see net.sf.l2j.gameserver.BasePacket#getType()
     */
    @Override
    public String getType()
    {
        return _S__FE_1B_EXSENDMANORLIST;
    }
    
}