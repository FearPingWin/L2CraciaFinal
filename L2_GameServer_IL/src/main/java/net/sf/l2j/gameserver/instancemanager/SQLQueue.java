/* This program is free software; you can redistribute it and/or modify
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
package net.sf.l2j.gameserver.instancemanager;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import net.sf.l2j.L2DatabaseFactory;
import net.sf.l2j.gameserver.ThreadPoolManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class manage queued sql's like INSERT/UPDATE/DELETE
 * 
 * @version $Revision: $ $Date: $
 * @author  DiezelMax
 */
public class SQLQueue
{
    protected static Log _log = LogFactory.getLog(SQLQueue.class.getName());
    
    private static SQLQueue _Instance;
    protected List<String> _queue1 = null;
    protected List<String> _queue2 = null;

    private SQLQueue()
    {
    	_queue1 = new ArrayList<String>();
    	_queue2 = new ArrayList<String>();
    	ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new doQueue(), 60000, 60000);
    }

    public static final SQLQueue getInstance()
    {
        if (_Instance == null)
        {
            _Instance = new SQLQueue();
        }
        return _Instance;
    }
    
    public void add(String sql)
    {
    	synchronized(_queue1)
    	{
    		_queue1.add(sql);
    	}
    }

    protected class doQueue extends Thread
    {
        public void run()
        {
        	flush();
        }
    }
    
    public void flush()
    {
    	synchronized(_queue2)
    	{
    		synchronized(_queue1)
        	{
    			if (!_queue1.isEmpty())
    			{
    				_queue2.addAll(_queue1);
        			_queue1.clear();
    			}    			
        	}
    		if(_queue2.isEmpty()) return;
    		java.sql.Connection con = null;
    		PreparedStatement statement = null;
    		try {
    			con = L2DatabaseFactory.getInstance().getConnection(con);        			
    			for (String sql: _queue2)
    			{
    				try {        						
    					statement = con.prepareStatement(sql);
    					statement.executeUpdate();
    					statement.close();
    					//System.out.print(sql + "\n");
    					sql = null;
    				} catch (Exception e) {
    					_log.fatal("error while porcessing sql queue " + e);
    				}
    			}
    			_queue2.clear();
    		} catch (Exception e) {
    			_log.fatal("error while porcessing sql queue " + e);
    		}
    		finally { try { con.close(); } catch (Exception e) {} }
    		if (_log.isDebugEnabled())
    			_log.warn("SQLQueue: "+ _queue2.size() + " sql's processed");
    	}
    }
 }
