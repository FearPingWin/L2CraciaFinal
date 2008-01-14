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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.network.L2GameClient;
import net.sf.l2j.gameserver.network.L2GamePacketHandler;
import net.sf.l2j.gameserver.network.SystemMessageId;


public final class RequestAuthSequence extends L2GameClientPacket
{
	private static final Log _log = LogFactory.getLog(RequestAuthSequence.class.getName());
	
    public RequestAuthSequence()
    {
    }

    protected void readImpl()
    {
        _version = readD();
    }

    protected void runImpl()
    {
        _log.warn("Requested unknown auth sequence with " + _version + " protocol. Closing connection.");
        getClient().closeNow();
    }

    public String getType()
    {
        return "RequestAuthSequence";
    }

    private int _version;
}