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
package com.l2jfree.gameserver.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.l2jfree.Config;
import com.l2jfree.gameserver.datatables.GmListTable;
import com.l2jfree.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfree.gameserver.network.Disconnection;

/**
 * @author luisantonioa
 */
public final class IllegalPlayerAction implements Runnable
{
	private static Log		_logAudit			= LogFactory.getLog("audit");

	protected String		_message;
	protected int			_punishment;
	protected L2PcInstance	_actor;

	public static final int	PUNISH_BROADCAST	= 1;
	public static final int	PUNISH_KICK			= 2;
	public static final int	PUNISH_KICKBAN		= 3;
	public static final int	PUNISH_JAIL			= 4;

	public IllegalPlayerAction(L2PcInstance actor, String message, int punishment)
	{
		_message = message;
		_punishment = punishment;
		_actor = actor;

		switch (punishment)
		{
		case PUNISH_KICK:
			_actor.sendMessage("You will be kicked for illegal action, GM informed.");
			break;
		case PUNISH_KICKBAN:
			_actor.sendMessage("You are banned for illegal action, GM informed.");
			break;
		case PUNISH_JAIL:
			_actor.sendMessage("Illegal action performed!");
			_actor.sendMessage("You will be teleported to GM Consultation Service area and jailed.");
			break;
		}
	}

	public void run()
	{
		_logAudit.info("AUDIT:" + _message + "," + _actor + " " + _punishment);

		GmListTable.broadcastMessageToGMs(_message);

		switch (_punishment)
		{
		case PUNISH_BROADCAST:
			return;
			
		case PUNISH_KICKBAN:
			_actor.setAccountAccesslevel(-100);
		case PUNISH_KICK:
			new Disconnection(_actor).defaultSequence(false);
			break;
		case PUNISH_JAIL:
			_actor.setInJail(true, Config.DEFAULT_PUNISH_PARAM);
			break;
		}
	}
}
