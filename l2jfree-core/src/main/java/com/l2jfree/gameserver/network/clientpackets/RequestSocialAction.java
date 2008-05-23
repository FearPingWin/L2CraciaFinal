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
package com.l2jfree.gameserver.network.clientpackets;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.l2jfree.Config;
import com.l2jfree.gameserver.ThreadPoolManager;
import com.l2jfree.gameserver.ai.CtrlIntention;
import com.l2jfree.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfree.gameserver.network.SystemMessageId;
import com.l2jfree.gameserver.network.serverpackets.SocialAction;
import com.l2jfree.gameserver.network.serverpackets.SystemMessage;
import com.l2jfree.gameserver.util.Util;

/**
 * This class ...
 * 
 * @version $Revision: 1.6.4.4 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestSocialAction extends L2GameClientPacket
{
	private static final String _C__1B_REQUESTSOCIALACTION = "[C] 1B RequestSocialAction";
	private final static Log _log = LogFactory.getLog(RequestSocialAction.class.getName());
	
	// format  cd
	private int _actionId;
	
	/**
	 * packet type id 0x1b
	 * format:		cd
	 * @param decrypt
	 */
    @Override
    protected void readImpl()
    {
        _actionId  = readD();
    }

    @Override
    protected void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		    return;

        // You cannot do anything else while fishing
        if (activeChar.isFishing())
        {
            SystemMessage sm = new SystemMessage(SystemMessageId.CANNOT_DO_WHILE_FISHING_3);
            activeChar.sendPacket(sm);
            sm = null;
            return;
        }

        // check if its the actionId is allowed
        if (_actionId < 2 || _actionId > 13)
        {
           Util.handleIllegalPlayerAction(activeChar, "Warning!! Character "+activeChar.getName()+" of account "+activeChar.getAccountName()+" requested an internal Social Action.", Config.DEFAULT_PUNISH);
           return;
        }
        
		if (	activeChar.getPrivateStoreType()==0 &&
				activeChar.getActiveRequester()==null &&
				!activeChar.isAlikeDead() &&
				(!activeChar.isAllSkillsDisabled() || activeChar.isInDuel()) &&
				activeChar.getAI().getIntention()==CtrlIntention.AI_INTENTION_IDLE)
		{
			if (_log.isDebugEnabled()) 
				_log.debug("Social Action:" + _actionId);
			
			SocialAction atk = new SocialAction(activeChar.getObjectId(), _actionId);
			// Schedule a social task to wait for the animation to finish
			ThreadPoolManager.getInstance().scheduleGeneral(new SocialTask(this), 2600);
			activeChar.setIsParalyzed(true);
			activeChar.broadcastPacket(atk);
		}
	}

	private class SocialTask implements Runnable
	{
		L2PcInstance _player;
		SocialTask(RequestSocialAction action)
		{
			_player = getClient().getActiveChar();
		}
		public void run()
		{
			_player.setIsParalyzed(false);
		}
	}

	/* (non-Javadoc)
	 * @see net.sf.l2j.gameserver.clientpackets.ClientBasePacket#getType()
	 */
	@Override
	public String getType()
	{
		return _C__1B_REQUESTSOCIALACTION;
	}
}
