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
package com.l2jfree.gameserver;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.List;
import java.util.Set;

import javolution.util.FastList;
import javolution.util.FastSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.l2jfree.Config;
import com.l2jfree.gameserver.util.Util;

public class DeadlockDetector extends Thread
{
	private static final Log _log = LogFactory.getLog(DeadlockDetector.class);
	
	private static DeadlockDetector _instance;
	
	public static DeadlockDetector getInstance()
	{
		if (_instance == null)
			_instance = new DeadlockDetector();
		
		return _instance;
	}
	
	private final ThreadMXBean _mbean = ManagementFactory.getThreadMXBean();
	private final Set<Long> _logged = new FastSet<Long>();
	
	private DeadlockDetector()
	{
		setPriority(Thread.MIN_PRIORITY);
		setDaemon(true);
		
		start();
	}
	
	@Override
	public void run()
	{
		for (;;)
			try
			{
				sleep(Config.DEADLOCKCHECK_INTERVAL);
				
				checkForDeadlocks();
			}
			catch (Exception e)
			{
				_log.warn("", e);
			}
	}
	
	private void checkForDeadlocks()
	{
		long[] ids = findDeadlockedThreadIDs();
		if (ids == null)
			return;
		
		List<Thread> deadlocked = new FastList<Thread>();
		
		for (long id : ids)
			if (_logged.add(id))
				deadlocked.add(findThreadById(id));
		
		if (!deadlocked.isEmpty())
		{
			Util.printSection("Deadlocked Thread(s)");
			for (Thread thread : deadlocked)
			{
				_log.fatal(thread);
				
				for (StackTraceElement trace : thread.getStackTrace())
					_log.fatal("\tat " + trace);
			}
		}
	}
	
	private long[] findDeadlockedThreadIDs()
	{
		if (_mbean.isSynchronizerUsageSupported())
			return _mbean.findDeadlockedThreads();
		else
			return _mbean.findMonitorDeadlockedThreads();
	}
	
	private Thread findThreadById(long id)
	{
		for (Thread thread : Thread.getAllStackTraces().keySet())
			if (thread.getId() == id)
				return thread;
		
		throw new IllegalStateException("Deadlocked Thread not found!");
	}
}