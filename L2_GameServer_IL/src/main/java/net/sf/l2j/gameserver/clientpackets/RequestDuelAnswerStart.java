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
package net.sf.l2j.gameserver.clientpackets;

import net.sf.l2j.gameserver.instancemanager.DuelManager;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.serverpackets.SystemMessage;

/**
 *  sample
 *  2a 
 *  01 00 00 00
 * 
 *  format  chddd
 * 
 * 
 * @version $Revision: 1.7.4.2 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestDuelAnswerStart extends L2GameClientPacket
{
	private static final String _C__2A_REQUESTANSWERPARTY = "[C] 2A RequestDuelAnswerStart";
	//private final static Log _log = LogFactory.getLog(RequestAnswerJoinParty.class.getName());
	
	private int _response;
    private int _duelType;
    private int _unk1;
	
    protected void readImpl()
    {
        _duelType = readD();
        _unk1=readD();
		_response = readD();
	}

    protected void runImpl()
	{
		L2PcInstance player = getClient().getActiveChar();
        if(player != null)
        {
            L2PcInstance requestor = player.getActiveRequester();
            if (requestor == null)
                return;	
            
            if (_response == 1) 
            {
                DuelManager.getInstance().createDuel(requestor, player, (_duelType==1));
            }
            else
            {
                SystemMessage msg = new SystemMessage(SystemMessage.PLAYER_DECLINED);
                requestor.sendPacket(msg);
                msg = null;
            }            
            player.setActiveRequester(null);
            requestor.onTransactionResponse();
        }
	}

	/* (non-Javadoc)
	 * @see net.sf.l2j.gameserver.clientpackets.ClientBasePacket#getType()
	 */
	public String getType()
	{
		return _C__2A_REQUESTANSWERPARTY;
	}
}
