package elayne.model.instance;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import elayne.IImageKeys;
import elayne.application.Activator;
import elayne.util.connector.LoginDB;

/**
 * This group represents a group in which the account information for an
 * L2PcInstance is stored and displayed.
 * @author polbat02
 */
public class L2AccountInfoGroup extends L2GroupEntry
{
	private static final String RESTORE_ACCOUNT = "SELECT password, accessLevel, lastIP FROM `accounts` WHERE `login` =?";
	private int accessLevel;
	private String encryptedPass;
	private String lastIp;
	private String login;
	private L2PcInstance parent;
	private L2CharacterEntry encryptedPasswordEntry;

	/**
	 * Constructor defining ONLY an {@link L2PcInstance} which will be the
	 * parent of this group.
	 * @param parent
	 */
	public L2AccountInfoGroup(L2PcInstance parent)
	{
		super(parent, "Account Information");
		this.parent = parent;
	}

	/**
	 * @return the access level for the account of the parent
	 * {@link L2PcInstance}.
	 */
	public int getAccessLevel()
	{
		return accessLevel;
	}

	/**
	 * @return the encrypted password for the account of the parent
	 * {@link L2PcInstance}.
	 */
	public String getEncryptedPass()
	{
		return encryptedPass;
	}

	@Override
	public ImageDescriptor getImageDescriptor()
	{
		return AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, IImageKeys.ACCOUNT_INFORMATION);
	}

	/**
	 * @return the last known IP for the account of the parent
	 * {@link L2PcInstance}.
	 */
	public String getLastIp()
	{
		return lastIp;
	}

	/**
	 * @return the last known server for the account of the parent
	 * {@link L2PcInstance}. Notice that the server number returned, is
	 * relative to the one in the <code>GAMESERVERS</code> table in the login
	 * server database.
	 
	public int getLastServer()
	{
		return lastServer;
	}*/

	/**
	 * @return the account of the parent {@link L2PcInstance}.
	 */
	public String getLogin()
	{
		return login;
	}

	@Override
	public L2PcInstance getParent()
	{
		return parent;
	}

	/**
	 * Restores all the account information related with the parent
	 * {@link L2PcInstance}. When restored, the entries that this group will
	 * contain, are added to this group: Account, Encrypted password, access
	 * level, last IP and last server are the entries.
	 */
	public void restore()
	{
		clearEntries();

		java.sql.Connection con = null;
		try
		{
			con = LoginDB.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement(RESTORE_ACCOUNT);
			statement.setString(1, getParent().getAccount());
			ResultSet rset = statement.executeQuery();
			while (rset.next())
			{
				login = getParent().getAccount();
				encryptedPass = rset.getString("password");
				accessLevel = rset.getInt("accessLevel");
				lastIp = rset.getString("lastIP");
				addEntry(new L2CharacterEntry(this, "Account:", login));
				encryptedPasswordEntry = new L2CharacterEntry(this, "Encrypted Password:", encryptedPass);
				addEntry(encryptedPasswordEntry);
				addEntry(new L2CharacterEntry(this, "Access Level:", accessLevel));
				addEntry(new L2CharacterEntry(this, "Last Ip:", lastIp));
			}
			rset.close();
			statement.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (con != null)
					con.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		getParent().addEntry(this);
	}

	/**
	 * @return The {@link L2CharacterEntry} that represents the encrypted
	 * password.
	 */
	public L2CharacterEntry getEncryptedPasswordEntry()
	{
		return encryptedPasswordEntry;
	}

	/**
	 * Sets a new Encrypted password onto this groups parent player.
	 * @param newPass
	 */
	public void setEncryptedPasswordEntry(String newPass)
	{
		encryptedPasswordEntry.setField(newPass);
	}
}
