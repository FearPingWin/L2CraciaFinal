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
package net.sf.l2j.loginserver.thread;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.List;
import java.util.Vector;

import javolution.util.FastList;

import net.sf.l2j.Config;
import net.sf.l2j.loginserver.LoginServer;
import net.sf.l2j.loginserver.beans.SessionKey;
import net.sf.l2j.loginserver.gameserverpackets.BlowFishKey;
import net.sf.l2j.loginserver.gameserverpackets.ChangeAccessLevel;
import net.sf.l2j.loginserver.gameserverpackets.GameServerAuth;
import net.sf.l2j.loginserver.gameserverpackets.PlayerAuthRequest;
import net.sf.l2j.loginserver.gameserverpackets.PlayerInGame;
import net.sf.l2j.loginserver.gameserverpackets.PlayerLogout;
import net.sf.l2j.loginserver.gameserverpackets.ServerStatus;
import net.sf.l2j.loginserver.loginserverpackets.AuthResponse;
import net.sf.l2j.loginserver.loginserverpackets.InitLS;
import net.sf.l2j.loginserver.loginserverpackets.KickPlayer;
import net.sf.l2j.loginserver.loginserverpackets.LoginServerFail;
import net.sf.l2j.loginserver.loginserverpackets.PlayerAuthResponse;
import net.sf.l2j.loginserver.manager.GameServerManager;
import net.sf.l2j.loginserver.manager.LoginManager;
import net.sf.l2j.loginserver.serverpackets.ServerBasePacket;
import net.sf.l2j.util.NewCrypt;
import net.sf.l2j.util.Util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author -Wooden-
 *
 */

public class GameServerThread extends Thread
{
	private static final Log _log = LogFactory.getLog(GameServerThread.class.getName());
	private Socket _connection;
	private InputStream _in;
	private OutputStream _out;
	private RSAPublicKey _publicKey;
	private RSAPrivateKey _privateKey;
	private NewCrypt _blowfish;
	private byte[] _blowfishKey;
	private boolean _isAuthed = false;
	private String _connectionIp;
	private int _max_players;
	private List<String> _players;
	private int _server_id;
	private boolean _isTestServer;
	private boolean _PvpServer;
	private int _gamePort;
	private byte[] _hexID;
	private String connectionIpAddress;
	private String _netConfig;
	
	/**
	 * @return Returns the hexID.
	 */
	public byte[] getHexID()
	{
		return _hexID;
	}
	
	public boolean isPlayerInGameServer(String account)
	{
		return _players.contains(account);
	}
	
	public void run()
	{
		try
		{
			InitLS startPacket = new InitLS(_publicKey.getModulus().toByteArray());
			sendPacket(startPacket);
            if (_log.isDebugEnabled())_log.debug("sent INIT");
			// register server and pass this to a GameServerThread
			connectionIpAddress   = _connection.getInetAddress().getHostAddress();
			if (isBannedGameserverIP(connectionIpAddress))
			{
				LoginServerFail lsf = new LoginServerFail(LoginServerFail.REASON_IP_BANNED);
				sendPacket(lsf);
				// throw new IOException("banned IP");
				_log.info("GameServerRegistration: IP Address " + connectionIpAddress + " is on Banned IP list.");
			}
			int lengthHi =0;
			int lengthLo =0;
			int length = 0;
			boolean checksumOk = false;
			while(true)
			{
				lengthLo = _in.read();
				lengthHi = _in.read();
				length= lengthHi*256 + lengthLo;  
				
				if (lengthHi < 0 || _connection.isClosed())
				{
					_log.debug("LoginServerThread: Login terminated the connection.");
					break;
				}
				
				byte[] incoming = new byte[length];
				incoming[0] = (byte) lengthLo;
				incoming[1] = (byte) lengthHi;
				
				int receivedBytes = 0;
				int newBytes = 0;
				while (newBytes != -1 && receivedBytes<length-2)
				{
					newBytes =  _in.read(incoming, 2, length-2);
					receivedBytes = receivedBytes + newBytes;
				}
				
				if (receivedBytes != length-2)
				{
					_log.warn("Incomplete Packet is sent to the server, closing connection.(LS)");
					break;
				}
				
				byte[] decrypt = new byte[length - 2];
				System.arraycopy(incoming, 2, decrypt, 0, decrypt.length);
				// decrypt if we have a key
				decrypt = _blowfish.decrypt(decrypt);
				checksumOk = _blowfish.checksum(decrypt);
				if (!checksumOk)
				{
					_log.warn("Incorrect packet checksum, closing connection (LS)");
					return;
				}
				
                if (_log.isDebugEnabled())_log.debug("[C]\n"+Util.printData(decrypt));
				
				int packetType = decrypt[0]&0xff;
				switch (packetType)
				{
					case 00:
						BlowFishKey bfk = new BlowFishKey(decrypt,_privateKey);
						_blowfishKey = bfk.getKey();
						_blowfish = new NewCrypt(_blowfishKey);
                        if (_log.isDebugEnabled())_log.debug("New BlowFish key received, Blowfih engine Re-initialized:");
						break;
					case 01:
						GameServerAuth gsa = new GameServerAuth(decrypt);
						_log.info("Auth request received");
						handleRegisterationProcess(gsa);
						if(_isAuthed)
						{
							AuthResponse ar = new AuthResponse(_server_id);
							sendPacket(ar);
							_log.info("Authed: id:"+_server_id);
							if ( LoginServer.statusServer != null )
								LoginServer.statusServer.SendMessageToTelnets("GameServer "+GameServerManager.getInstance().getServerName(_server_id)+" ("+_server_id+") is connected");
						}
						else
						{
							_log.info("Closing connection");
							_connection.close();
						}
						break;
					case 02:
						if(!_isAuthed)
						{
							LoginServerFail lsf = new LoginServerFail(LoginServerFail.NOT_AUTHED);
							sendPacket(lsf);
							_connection.close();
							break;
						}
						PlayerInGame pig = new PlayerInGame(decrypt);
						Vector<String> newAccounts = pig.getAccounts();
						for(String account : newAccounts)
						{
							_players.add(account);
                            if (_log.isDebugEnabled())_log.debug("Player "+account+" is in GameServer "+GameServerManager.getInstance().getServerName(_server_id)+" ("+_server_id+")");
							if(LoginServer.statusServer != null)
								LoginServer.statusServer.SendMessageToTelnets("Player "+account+" is in GameServer "+_server_id);
						}
						break;
					case 03:
						if(!_isAuthed)
						{
							LoginServerFail lsf = new LoginServerFail(LoginServerFail.NOT_AUTHED);
							sendPacket(lsf);
							_connection.close();
							break;
						}
						PlayerLogout plo = new PlayerLogout(decrypt);
						_players.remove(plo.getAccount());
						LoginManager.getInstance().removeAccountFromGameServer(plo.getAccount());
                        if (_log.isDebugEnabled())_log.debug("Player "+plo.getAccount()+" logged out from gameserver"+_server_id);
						if(LoginServer.statusServer != null)
							LoginServer.statusServer.SendMessageToTelnets("Player "+plo.getAccount()+" disconnected from GameServer "+_server_id);
						break;
					case 04:
						if(!_isAuthed)
						{
							LoginServerFail lsf = new LoginServerFail(LoginServerFail.NOT_AUTHED);
							sendPacket(lsf);
							_connection.close();
							break;
						}
						ChangeAccessLevel cal = new ChangeAccessLevel(decrypt);
						LoginManager.getInstance().setAccountAccessLevel(cal.getAccount(),cal.getLevel());
						_log.info("Changed "+cal.getAccount()+" access level to"+cal.getLevel());
						break;
					case 05:
						if(!_isAuthed)
						{
							LoginServerFail lsf = new LoginServerFail(LoginServerFail.NOT_AUTHED);
							sendPacket(lsf);
							_connection.close();
							break;
						}
						PlayerAuthRequest par = new PlayerAuthRequest(decrypt);
						PlayerAuthResponse authResponse;
                        if (_log.isDebugEnabled())_log.debug("auth request recieved for Player "+par.getAccount());
						SessionKey key = LoginManager.getInstance().getKeyForAccount(par.getAccount());
						if(key != null && key.equals(par.getKey()))
						{
                            if (_log.isDebugEnabled())_log.debug("auth request: OK");
							authResponse = new PlayerAuthResponse(par.getAccount(), true);
						}
						else
						{
                            if (_log.isDebugEnabled())
							{
								_log.debug("auth request: NO");
								_log.debug("session key from self:"+LoginManager.getInstance().getKeyForAccount(par.getAccount()));
								_log.debug("session key sent:"+par.getKey());
							}
							authResponse = new PlayerAuthResponse(par.getAccount(), false);
						}
						sendPacket(authResponse);
						break;
					case 06:
						if(!_isAuthed)
						{
							LoginServerFail lsf = new LoginServerFail(LoginServerFail.NOT_AUTHED);
							sendPacket(lsf);
							_connection.close();
							break;
						}
                        if (_log.isDebugEnabled())_log.debug("ServerStatus reiceved");
						@SuppressWarnings("unused")
						ServerStatus ss = new ServerStatus(decrypt,_server_id); //will do the actions by itself
						break;
				}
			}
		}
		catch(IOException e)
		{
			_log.info("Server "+GameServerManager.getInstance().getServerName(_server_id)+" ("+_server_id+") : connection lost");
			if(LoginServer.statusServer != null)
				LoginServer.statusServer.SendMessageToTelnets("GameServer "+_server_id+" is disconnected");
		}
		finally
		{
			if(_isAuthed)
			{
				GameServerManager.getInstance().setServerReallyDown(_server_id);
				GameServerListener.getInstance().removeGameServer(this);
				_log.info("Server "+GameServerManager.getInstance().getServerName(_server_id)+" ("+_server_id+") : Setted as disconnected");
			}
			GameServerListener.getInstance().removeFloodProtection(_connectionIp);
            _players.clear();
		}
	}
	
	/**
	 * @param hexID
	 */
	private void handleRegisterationProcess(GameServerAuth gameServerauth)
	{
		try
		{
			GameServerManager gsm = GameServerManager.getInstance();
			if(gsm.isARegisteredServer(gameServerauth.getHexID()))
			{
                if (_log.isDebugEnabled())_log.debug("Valid HexID");
				_server_id = gsm.getServerIDforHex(gameServerauth.getHexID());
				if(gsm.isServerAuthed(_server_id))
				{
					LoginServerFail lsf = new LoginServerFail(LoginServerFail.REASON_ALREADY_LOGGED_IN);
					sendPacket(lsf);
					_connection.close();
					return;
				}
				_gamePort = gameServerauth.getPort();
				_max_players = gameServerauth.getMax_palyers();
				_hexID = gameServerauth.getHexID();
				_netConfig = gameServerauth.getNetConfig();
				gsm.addServer(this);
				gsm.updateIP(_server_id);
			}
			else if(Config.ACCEPT_NEW_GAMESERVER)
			{
                if (_log.isDebugEnabled())_log.debug("New HexID");
				if(!gameServerauth.acceptAlternateID())
				{
					if(gsm.isIDfree(gameServerauth.getDesiredID()))
					{
                        if (_log.isDebugEnabled())_log.debug("Desired ID is Valid");
						_server_id = gameServerauth.getDesiredID();
						_gamePort = gameServerauth.getPort();
						_max_players = gameServerauth.getMax_palyers();
						_hexID = gameServerauth.getHexID();
						_netConfig = gameServerauth.getNetConfig();
						gsm.createServer(this);
						gsm.addServer(this);
						gsm.updateIP(_server_id);
					}
					else
					{
						LoginServerFail lsf = new LoginServerFail(LoginServerFail.REASON_ID_RESERVED);
						sendPacket(lsf);
						_connection.close();
						return;
					}
				}
				else
				{
					int id;
					if(!gsm.isIDfree(gameServerauth.getDesiredID()))
					{
						id = gsm.findFreeID();
                        if (_log.isDebugEnabled())_log.debug("Affected New ID:"+id);
						if(id < 0)
						{
							LoginServerFail lsf = new LoginServerFail(LoginServerFail.REASON_NO_FREE_ID);
							sendPacket(lsf);
							_connection.close();
							return;
						}
					}
					else
					{
						id = gameServerauth.getDesiredID();
                        if (_log.isDebugEnabled())_log.debug("Desired ID is Valid");
					}
					_server_id = id;
					_gamePort = gameServerauth.getPort();
					_max_players = gameServerauth.getMax_palyers();
					_hexID = gameServerauth.getHexID();
					_netConfig = gameServerauth.getNetConfig();
					gsm.createServer(this);
					gsm.addServer(this);
					gsm.updateIP(_server_id);
				}
			}
			else
			{
				_log.info("Wrong HexID");
				LoginServerFail lsf = new LoginServerFail(LoginServerFail.REASON_WRONG_HEXID);
				sendPacket(lsf);
				_connection.close();
				return;
			}
			
		}
		catch (IOException e)
		{
			_log.info("Error while registering GameServer "+GameServerManager.getInstance().getServerName(_server_id)+" (ID:"+_server_id+")");
		}
	}
	
	/**
	 * @param ipAddress
	 * @return
	 */
	public static boolean isBannedGameserverIP(@SuppressWarnings("unused") String ipAddress)
	{
		// TODO Auto-generated method stub
		return false;
	}
	
	public GameServerThread(Socket con)
	{
		_connection = con;
		_connectionIp = con.getInetAddress().getHostAddress();
		try
		{
			_in = _connection.getInputStream();
			_out = new BufferedOutputStream(_connection.getOutputStream());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		KeyPair pair = GameServerManager.getInstance().getKeyPair();
		_privateKey = (RSAPrivateKey) pair.getPrivate();
		_publicKey = (RSAPublicKey) pair.getPublic();
		_blowfish = new NewCrypt("_;v.]05-31!|+-%xT!^[$\00");
		_players = new FastList<String>();
		start();
	}
	
	/**
	 * @param sl
	 * @throws IOException
	 */
	private void sendPacket(ServerBasePacket sl) throws IOException
	{
		byte[] data = sl.getContent();
		_blowfish.checksum(data);
        if (_log.isDebugEnabled())_log.debug("[S]\n"+Util.printData(data));
		data = _blowfish.crypt(data);
		
		int len = data.length+2;
		synchronized(_out)
		{
			_out.write(len & 0xff);
			_out.write(len >> 8 &0xff);
			_out.write(data);
			_out.flush();
		}
	}
	
	public void KickPlayer(String account)
	{
		KickPlayer kp = new KickPlayer(account);
		try
		{
			sendPacket(kp);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * @return Returns the max_players.
	 */
	public int getMaxPlayers()
	{
		return _max_players;
	}
	
	/**
	 * @return Returns the current_players.
	 */
	public int getCurrentPlayers()
	{
		return _players.size();
	}
	
	/**
	 * @return Returns the server_id.
	 */
	public int getServerID()
	{
		return _server_id;
	}
	
	/**
	 * @return Returns the networks config string.
	 */
	public String getNetConfig()
	{
		return _netConfig;
	}
	
	/**
	 * @return
	 */
	public int getPort()
	{
		return _gamePort;
	}
	
	/**
	 * @return
	 */
	public boolean getPvP()
	{
		return _PvpServer;
	}
	
	/**
	 * @return
	 */
	public boolean isTestServer()
	{
		return _isTestServer;
	}
	
	/**
	 * @return Returns the isAuthed.
	 */
	public boolean isAuthed()
	{
		return _isAuthed;
	}
	
	/**
	 * @param isAuthed The isAuthed to set.
	 */
	public void setAuthed(boolean isAuthed)
	{
		_isAuthed = isAuthed;
	}
	
	/**
	 * @param value
	 */
	public void setMaxPlayers(int value)
	{
		_max_players = value;
	}
	
	/**
	 * @return Returns the connectionIpAddress.
	 */
	public String getConnectionIpAddress()
	{
		return connectionIpAddress;
	}
}