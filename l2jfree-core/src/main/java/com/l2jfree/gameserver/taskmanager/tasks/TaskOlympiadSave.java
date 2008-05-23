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
package com.l2jfree.gameserver.taskmanager.tasks;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.l2jfree.gameserver.Olympiad;
import com.l2jfree.gameserver.taskmanager.Task;
import com.l2jfree.gameserver.taskmanager.TaskManager;
import com.l2jfree.gameserver.taskmanager.TaskTypes;
import com.l2jfree.gameserver.taskmanager.TaskManager.ExecutedTask;

/**
 * Updates all data of Olympiad nobles in db
 * 
 * @author godson
 */
public class TaskOlympiadSave extends Task
{
	private static final Log	_log	= LogFactory.getLog(TaskOlympiadSave.class.getName());
	public static final String	NAME	= "olympiad_save";

	public String getName()
	{
		return NAME;
	}

	public void onTimeElapsed(ExecutedTask task)
	{
		try
		{
			Olympiad.getInstance().save();
			_log.info("Olympiad System: Data updated successfully.");
		}
		catch (Exception e)
		{
			_log.warn("Olympiad System: Failed to save Olympiad configuration: " + e);
		}
	}

	public void initializate()
	{
		super.initializate();
		TaskManager.addUniqueTask(NAME, TaskTypes.TYPE_FIXED_SHEDULED, "900000", "1800000", "");
	}
}
