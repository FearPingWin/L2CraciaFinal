/**
 * 
 */
package elayne.views;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import javolution.util.FastList;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.part.ViewPart;

import elayne.application.Activator;
import elayne.ations.RequestClanInformation;
import elayne.ations.RequestPlayerInformation;
import elayne.datatables.CharTemplateTable;
import elayne.dialogs.SearchDialog;
import elayne.model.instance.L2CharacterBriefEntry;
import elayne.preferences.GeneralPreferencePage;
import elayne.util.connector.ServerDB;

/**
 * @author polbat02
 */
public class SearchView extends ViewPart
{

	private static final String SQL_HEROES = "SELECT c.account_name, c.char_name, c.level, h.class_id FROM heroes h, characters c WHERE played = 1 AND obj_Id=char_id LIMIT 0,1000";
	public static final String ID = "elayne.views.search";
	public Composite parent = null;
	private Table table;
	private FastList<L2CharacterBriefEntry> players = new FastList<L2CharacterBriefEntry>();
	private RequestPlayerInformation actionShowInfo;
	private RequestClanInformation actionClanInfo;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent)
	{
		this.parent = parent;
		actionShowInfo = new RequestPlayerInformation(getSite().getWorkbenchWindow(), null);
		actionClanInfo = new RequestClanInformation(getSite().getWorkbenchWindow());
		drawTable();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus()
	{}

	private void drawTable()
	{
		IPreferencesService service = Platform.getPreferencesService();
		String searchInput = service.getString(Activator.PLUGIN_ID, GeneralPreferencePage.SEARCH_INPUT, "", null);
		int searchType = service.getInt(Activator.PLUGIN_ID, GeneralPreferencePage.SEARCH_TYPE, 0, null);

		if (searchType == SearchDialog.SEARCH_FOR_HEROES)
		{
			getHeroes();
		}
		else if (searchType == SearchDialog.SEARCH_BY_NAME)
		{
			getPlayersByName(searchInput);
			fillTable();
		}
		else if (searchType == SearchDialog.SEARCH_BY_TITLE)
		{
			getPlayersByTitle(searchInput);
			fillTable();
		}
		else if (searchType == SearchDialog.SEARCH_BY_ACCOUNT)
		{
			getPlayersByAccount(searchInput);
			fillTable();
		}
		else if (searchType == SearchDialog.SEARCH_CLAN_BY_NAME)
		{
			getClanByName(searchInput);
		}
		else if (searchType == SearchDialog.SEARCH_BY_OBJECT_ID)
		{
			int objectId = 0;
			try
			{
				objectId = Integer.valueOf(searchInput);
			}
			catch (NumberFormatException e)
			{
				System.out.println("SearchView: Wrong values inserted in the search Field.");
				MessageDialog.openError(getSite().getShell(), "Invalid Search", "The Search field must be filled up with numbers.");
				return;
			}
			getPlayersByObjectId(objectId);
			fillTable();
		}

		if (!isClanSearch(searchType) && table != null)
		{
			table.setVisible(true);
			table.addMouseListener(new MouseListener()
			{
				public void mouseDoubleClick(MouseEvent e)
				{
					if (table.getSelection().length == 1)
					{
						TableItem item = table.getSelection()[0];
						String playerName = item.getText(0);
						actionShowInfo.setName(playerName);
						actionShowInfo.run();
					}
				}

				public void mouseDown(MouseEvent e)
				{}

				public void mouseUp(MouseEvent e)
				{}
			});
		}
		else
		{
			table.setVisible(true);
			table.addMouseListener(new MouseListener()
			{
				public void mouseDoubleClick(MouseEvent e)
				{
					if (table.getSelection().length == 1)
					{
						TableItem item = table.getSelection()[0];
						int clanId = Integer.valueOf(item.getText(0));
						String clanName = item.getText(1);
						actionClanInfo.setClanId(clanId);
						actionClanInfo.setClanName(clanName);
						actionClanInfo.run();
					}
				}

				public void mouseDown(MouseEvent e)
				{}

				public void mouseUp(MouseEvent e)
				{}
			});
		}
	}

	private void fillTable()
	{
		String[] titles = { "Player Name", "Account", "Level", "Sex" };

		table = new Table(parent, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		table.setLayoutData(data);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		for (int i = 0; i < titles.length; i++)
		{
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setText(titles[i]);
		}
		for (L2CharacterBriefEntry player : players)
		{
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(0, player.getName());
			item.setText(1, player.getAccount());
			item.setText(2, String.valueOf(player.getLevel()));
			String sex = "";
			if (player.getSex() == 0)
				sex = "Male";
			else
				sex = "Female";
			item.setText(3, sex);
		}
		for (int i = 0; i < titles.length; i++)
		{
			table.getColumn(i).pack();
		}
	}

	private void getPlayersByName(String nameToLookFor)
	{
		players.clear();
		java.sql.Connection con = null;
		try
		{
			con = ServerDB.getInstance().getConnection();
			String previous = "SELECT obj_Id, account_name, char_name, level, accesslevel, sex, clanid FROM `characters` WHERE `char_name` LIKE '%" + nameToLookFor + "%' LIMIT 0,200";
			PreparedStatement statement = con.prepareStatement(previous);
			ResultSet rset = statement.executeQuery();
			while (rset.next())
			{
				int objId = rset.getInt("obj_Id");
				String account = rset.getString("account_name");
				String name = rset.getString("char_name");
				int level = rset.getInt("level");
				int accesslevel = rset.getInt("accesslevel");
				int sex = rset.getInt("sex");
				int clanId = rset.getInt("clanid");
				players.add(new L2CharacterBriefEntry(objId, level, name, account, 1, accesslevel, sex, clanId));
			}
			rset.close();
			statement.close();
			con.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void getPlayersByTitle(String titleToLookFor)
	{
		players.clear();
		java.sql.Connection con = null;
		try
		{
			con = ServerDB.getInstance().getConnection();
			String previous = "SELECT obj_Id, account_name, char_name, level, accesslevel, sex, clanid FROM `characters` WHERE `title` LIKE '%" + titleToLookFor + "%' LIMIT 0,200";
			PreparedStatement statement = con.prepareStatement(previous);
			ResultSet rset = statement.executeQuery();
			while (rset.next())
			{
				int objId = rset.getInt("obj_Id");
				String account = rset.getString("account_name");
				String name = rset.getString("char_name");
				int level = rset.getInt("level");
				int accesslevel = rset.getInt("accesslevel");
				int sex = rset.getInt("sex");
				int clanId = rset.getInt("clanid");
				players.add(new L2CharacterBriefEntry(objId, level, name, account, 1, accesslevel, sex, clanId));
			}
			rset.close();
			statement.close();
			con.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void getPlayersByAccount(String accountToLookFor)
	{
		players.clear();
		java.sql.Connection con = null;
		try
		{
			con = ServerDB.getInstance().getConnection();
			String previous = "SELECT obj_Id, account_name, char_name, level, accesslevel, sex, clanid FROM `characters` WHERE `account_name` LIKE '%" + accountToLookFor + "%' LIMIT 0,200";
			PreparedStatement statement = con.prepareStatement(previous);
			ResultSet rset = statement.executeQuery();
			while (rset.next())
			{
				int objId = rset.getInt("obj_Id");
				String account = rset.getString("account_name");
				String name = rset.getString("char_name");
				int level = rset.getInt("level");
				int accesslevel = rset.getInt("accesslevel");
				int sex = rset.getInt("sex");
				int clanId = rset.getInt("clanid");
				players.add(new L2CharacterBriefEntry(objId, level, name, account, 1, accesslevel, sex, clanId));
			}
			rset.close();
			statement.close();
			con.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void getPlayersByObjectId(int objectId)
	{
		players.clear();
		java.sql.Connection con = null;
		try
		{
			con = ServerDB.getInstance().getConnection();
			String previous = "SELECT obj_Id, account_name, char_name, level, accesslevel, sex, clanid FROM `characters` WHERE `obj_Id` LIKE '%" + objectId + "%' LIMIT 0,200";
			PreparedStatement statement = con.prepareStatement(previous);
			ResultSet rset = statement.executeQuery();
			while (rset.next())
			{
				int objId = rset.getInt("obj_Id");
				String account = rset.getString("account_name");
				String name = rset.getString("char_name");
				int level = rset.getInt("level");
				int accesslevel = rset.getInt("accesslevel");
				int sex = rset.getInt("sex");
				int clanId = rset.getInt("clanid");
				players.add(new L2CharacterBriefEntry(objId, level, name, account, 1, accesslevel, sex, clanId));
			}
			rset.close();
			statement.close();
			con.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void getClanByName(String nameToLookFor)
	{
		players.clear();
		ArrayList<String[]> values = new ArrayList<String[]>();
		java.sql.Connection con = null;
		try
		{
			con = ServerDB.getInstance().getConnection();
			String previous = "SELECT clan_id, clan_name, clan_level, ally_name FROM `clan_data` WHERE `clan_name` LIKE '%" + nameToLookFor + "%' LIMIT 0,200";
			PreparedStatement statement = con.prepareStatement(previous);
			ResultSet rset = statement.executeQuery();
			while (rset.next())
			{
				int clanId = rset.getInt("clan_id");
				String clanName = rset.getString("clan_name");
				String allyName = rset.getString("ally_name");
				int clan_level = rset.getInt("clan_level");
				String[] clanData = { String.valueOf(clanId), clanName, allyName, String.valueOf(clan_level) };
				values.add(clanData);
			}
			rset.close();
			statement.close();
			con.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		String[] titles = { "Clan Id", "Clan Name", "Ally Name", "Clan Level" };

		table = new Table(parent, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		table.setLayoutData(data);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		for (int i = 0; i < titles.length; i++)
		{
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setText(titles[i]);
		}
		for (String[] clanData : values)
		{
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(0, clanData[0]);
			item.setText(1, clanData[1]);
			if (clanData[2] == null || clanData[2].equals(""))
				item.setText(2, "No Ally");
			else
				item.setText(2, clanData[2]);
			item.setText(3, clanData[3]);
		}
		for (int i = 0; i < titles.length; i++)
		{
			table.getColumn(i).pack();
		}
	}

	/**
	 * Retrieves all heroes from the server and displays the results in a newly
	 * created table. The table will contain a {@link DoubleClickEvent} handler
	 * that will allow us to get a player information in detail.
	 */
	private void getHeroes()
	{
		players.clear();
		ArrayList<String[]> values = new ArrayList<String[]>();
		java.sql.Connection con = null;
		try
		{
			con = ServerDB.getInstance().getConnection();
			String sql = SQL_HEROES;
			PreparedStatement statement = con.prepareStatement(sql);
			ResultSet rset = statement.executeQuery();
			while (rset.next())
			{
				String account = rset.getString("account_name");
				String name = rset.getString("char_name");
				int level = rset.getInt("level");
				String className = CharTemplateTable.getInstance().getClassNameById(rset.getInt("class_id"));
				String[] clanData = { name, account, String.valueOf(level), className };
				values.add(clanData);
			}
			rset.close();
			statement.close();
			con.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		String[] titles = { "Name", "Account", "Level", "Heroe of the Class" };

		table = new Table(parent, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		table.setLayoutData(data);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		for (int i = 0; i < titles.length; i++)
		{
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setText(titles[i]);
		}
		for (String[] dt : values)
		{
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(0, dt[0]);
			item.setText(1, dt[1]);
			item.setText(2, dt[2]);
			item.setText(3, dt[3]);
		}
		for (int i = 0; i < titles.length; i++)
		{
			table.getColumn(i).pack();
		}
	}

	private boolean isClanSearch(int searchType)
	{
		return (searchType == SearchDialog.SEARCH_CLAN_BY_NAME || searchType == SearchDialog.SEARCH_CLAN_BY_ID);
	}

	@Override
	public void dispose()
	{
		super.dispose();
	}
}
