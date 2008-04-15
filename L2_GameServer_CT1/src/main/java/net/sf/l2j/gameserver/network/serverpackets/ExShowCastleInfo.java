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
package net.sf.l2j.gameserver.network.serverpackets;

import java.util.Map;

import net.sf.l2j.gameserver.datatables.ClanTable;
import net.sf.l2j.gameserver.instancemanager.CastleManager;
import net.sf.l2j.gameserver.model.entity.Castle;

/**
 *
 * @author  KenM
 */
public class ExShowCastleInfo extends L2GameServerPacket
{
     private static final String S_FE_14_EX_SHOW_CASTLE_INFO = "[S] FE:14 ExShowFortressInfo";
    /**
     * @see net.sf.l2j.gameserver.serverpackets.L2GameServerPacket#getType()
     */
    @Override
    public String getType()
    {
        return S_FE_14_EX_SHOW_CASTLE_INFO;
    }

    /**
     * @see net.sf.l2j.gameserver.serverpackets.L2GameServerPacket#writeImpl()
     */
    @Override
    protected void writeImpl()
    {
        writeC(0xfe);
        writeH(0x14);
        Map<Integer, Castle> castles = CastleManager.getInstance().getCastles();
        writeD(castles.size());
        for (Castle castle : castles.values())
        {
            writeD(castle.getCastleId());
            writeS(castle.getOwnerId() <= 0 ? "" : ClanTable.getInstance().getClan(castle.getOwnerId()).getName()); // owner clan name
            writeD(castle.getTaxPercent());
            writeD((int) (castle.getSiege().getSiegeDate().getTimeInMillis()/1000));
        }
    }
}
