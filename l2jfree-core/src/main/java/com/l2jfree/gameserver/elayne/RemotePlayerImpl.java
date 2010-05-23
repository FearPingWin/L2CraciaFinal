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
package com.l2jfree.gameserver.elayne;

import java.rmi.RemoteException;

import com.l2jfree.gameserver.model.actor.instance.L2PcInstance;


/**
 * 
 * @author polbat02
 */
public class RemotePlayerImpl implements IRemotePlayer
{

	private L2PcInstance	_player;

	public RemotePlayerImpl(L2PcInstance character)
	{
		_player = character;
	}

	/**
	 * @see com.l2jfree.gameserver.elayne.IRemotePlayer#getAccessLevel()
	 */
	public int getAccessLevel() throws RemoteException
	{
		return _player.getAccessLevel();
	}

	/**
	 * @see com.l2jfree.gameserver.elayne.IRemotePlayer#getAccount()
	 */
	public String getAccount() throws RemoteException
	{
		return _player.getAccountName();
	}

	/**
	 * @see com.l2jfree.gameserver.elayne.IRemotePlayer#getLevel()
	 */
	public int getLevel() throws RemoteException
	{
		return _player.getLevel();
	}

	/**
	 * @see com.l2jfree.gameserver.elayne.IRemotePlayer#getName()
	 */
	public String getName() throws RemoteException
	{
		return _player.getName();
	}

	/**
	 * @see com.l2jfree.gameserver.elayne.IRemotePlayer#getObjectId()
	 */
	public int getObjectId() throws RemoteException
	{
		return _player.getObjectId();
	}

	/**
	 * @see com.l2jfree.gameserver.elayne.IRemotePlayer#getSex()
	 */
	public int getSex() throws RemoteException
	{
		if (_player.getAppearance().getSex())
			return 1;
		return 0;
	}

	/**
	 * @see com.l2jfree.gameserver.elayne.IRemotePlayer#online()
	 */
	public int online() throws RemoteException
	{
		return _player.isOnline();
	}

}
