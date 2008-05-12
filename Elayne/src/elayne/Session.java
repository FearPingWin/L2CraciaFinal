package elayne;

import java.io.IOException;
import java.rmi.ConnectException;
import java.security.MessageDigest;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

import com.sun.jmx.snmp.daemon.CommunicationException;

import elayne.application.Activator;
import elayne.datatables.ArmorTable;
import elayne.datatables.CharTemplateTable;
import elayne.datatables.DetailedItemTable;
import elayne.datatables.GetBannedPlayers;
import elayne.datatables.GetOnlinePlayers;
import elayne.datatables.HennaTable;
import elayne.datatables.ItemTable;
import elayne.datatables.WeaponTable;
import elayne.model.ConnectionDetails;
import elayne.preferences.LoginPreferencePage;
import elayne.rmi.RemoteAdministrationClient;
import elayne.util.Base64;
import elayne.util.connector.LoginDB;
import elayne.util.connector.ServerDB;

/**
 * Encapsulates the state for a session, including the connection details (user
 * name, password, server) and the connection itself.
 */
public class Session implements IAdaptable
{

	private ConnectionDetails connectionDetails;

	private static Session INSTANCE;

	private boolean isAllowedUser = false;

	public static Session getInstance()
	{
		if (INSTANCE == null)
			INSTANCE = new Session();
		return INSTANCE;
	}

	private Session()
	{
	// enforce the singleton patter
	}

	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter)
	{
		return null;
	}

	public boolean getIsAllowedUser()
	{
		return isAllowedUser;
	}

	public ConnectionDetails getConnectionDetails()
	{
		return connectionDetails;
	}

	public void setConnectionDetails(ConnectionDetails connectionDetails)
	{
		this.connectionDetails = connectionDetails;
	}

	/**
	 * Establishes the connection to the server and logs in. The connection
	 * details must have already been set.
	 */
	public void connectAndLogin(final IProgressMonitor monitor)
	{
		try
		{
			monitor.beginTask("Connecting...", IProgressMonitor.UNKNOWN);
			monitor.subTask("Conecting to login server...");
			try
			{
				if (selectAccount(monitor))
				{
					if (isAllowedUser)
					{
						// TANIS SERVER CONNECTION
						monitor.subTask("Conecting to Server database...");
						ServerDB.getInstance();

						// GET ITEMS
						monitor.subTask("Getting Items...");
						ItemTable.getInstance().load();

						// GET ARMORS
						monitor.subTask("Getting Armors...");
						ArmorTable.getInstance().load();

						// GET WEAPONS
						monitor.subTask("Getting Weapons...");
						WeaponTable.getInstance().load();

						// GET GENERAL ITEM INFORMATION
						monitor.subTask("Getting General Items Information...");
						DetailedItemTable.getInstance().load();

						// GET CHARACTER TEMPLATES
						monitor.subTask("Getting Character Templates...");
						CharTemplateTable.getInstance().load();

						// GET HENNA TEMPLATES
						monitor.subTask("GEtting Henna Templates...");
						HennaTable.getInstance().restore();

						// GET ONLINE PLAYERS
						monitor.subTask("Getting Online Players...");
						GetOnlinePlayers.getInstance().getOnlinePlayers(null, true);

						// GET BANNED PLAYERS
						monitor.subTask("Getting Banned Players...");
						GetBannedPlayers.getInstance().getBannedPlayers(null, true);

						IPreferencesService service = Platform.getPreferencesService();
						boolean auto_logn_rmi = service.getBoolean(Activator.PLUGIN_ID, LoginPreferencePage.AUTO_LOGIN_RMI, false, null);
						if (auto_logn_rmi)
						{
							// GET AND HOLD A SERVER CONNECTION
							monitor.subTask("Getting RMI Server connection...");
							RemoteAdministrationClient.getInstance().connect();
						}
					}

				}
				else
				{
					monitor.subTask("Login not allowed to continue.");
				}
			}
			catch (CommunicationException e)
			{
				System.out.println("Exception while connecting to servers: " + e.getMessage());
			}
			catch (ConnectException e)
			{
				System.out.println("Exception while connecting to servers database: " + e.getMessage());
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		finally
		{
			monitor.done();
		}
	}

	private boolean selectAccount(IProgressMonitor monitor)
	{
		try
		{
			// Encode Password
			MessageDigest md = MessageDigest.getInstance("SHA");
			byte[] newpass;
			newpass = getConnectionDetails().getPassword().getBytes("UTF-8");
			newpass = md.digest(newpass);

			monitor.subTask("Connecting to Login database...");
			// Add to Base
			java.sql.Connection con = null;
			// Connect to the Login DataBase
			con = LoginDB.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT login, password, accessLevel FROM accounts WHERE login=? AND password=?");
			statement.setString(1, getConnectionDetails().getUserId());
			statement.setString(2, Base64.encodeBytes(newpass));
			ResultSet rset = statement.executeQuery();
			int acl = 0;
			while (rset.next())
			{
				acl = rset.getInt("accessLevel");
			}
			statement.close();
			/*
			 * Access level is checked here. That's the place in which we can
			 * add configuration options for minimum access levels.
			 */
			if (acl >= 100)
			{
				isAllowedUser = true;
				return true;
			}
		}
		catch (Exception e)
		{
			System.out.println("Exception while connecting to server. Is the server online?");
		}
		return false;
	}
}
