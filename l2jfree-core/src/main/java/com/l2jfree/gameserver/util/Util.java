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

/**
 * 
 * @author luisantonioa
 * 
 */

import java.io.File;
import java.util.Collection;

import org.python.core.Py;
import org.python.core.PyModule;
import org.python.core.PySystemState;
import org.python.core.imp;
import org.python.util.InteractiveConsole;

import com.l2jfree.Config;
import com.l2jfree.gameserver.ThreadPoolManager;
import com.l2jfree.gameserver.model.L2Character;
import com.l2jfree.gameserver.model.L2Object;
import com.l2jfree.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfree.tools.util.CustomFileNameFilter;

/**
 * General Utility functions related to Gameserver
 * 
 * @version $Revision: 1.2 $ $Date: 2004/06/27 08:12:59 $
 */
public final class Util
{
	public static String[] getMemUsage()
	{
		double maxMem = ((long) (Runtime.getRuntime().maxMemory() / 1024)); // maxMemory is the upper limit the jvm can use
		double allocatedMem = ((long) (Runtime.getRuntime().totalMemory() / 1024)); // totalMemory the size of the current allocation pool
		double nonAllocatedMem = maxMem - allocatedMem; // non allocated memory till jvm limit
		double cachedMem = ((long) (Runtime.getRuntime().freeMemory() / 1024)); // freeMemory the unused memory in the allocation pool
		double usedMem = allocatedMem - cachedMem; // really used memory
		double useableMem = maxMem - usedMem; // allocated, but non-used and non-allocated memory
		return new String[]
		{
				" - AllowedMemory: " + ((int) (maxMem)) + " KB",
				"Allocated: " + ((int) (allocatedMem)) + " KB (" + (((double) (Math.round(allocatedMem / maxMem * 1000000))) / 10000) + "%)",
				"Non-Allocated: " + ((int) (nonAllocatedMem)) + " KB (" + (((double) (Math.round(nonAllocatedMem / maxMem * 1000000))) / 10000) + "%)",
				"- AllocatedMemory: " + ((int) (allocatedMem)) + " KB",
				"Used: " + ((int) (usedMem)) + " KB (" + (((double) (Math.round(usedMem / maxMem * 1000000))) / 10000) + "%)",
				"Unused (cached): " + ((int) (cachedMem)) + " KB (" + (((double) (Math.round(cachedMem / maxMem * 1000000))) / 10000) + "%)",
				"- UseableMemory: " + ((int) (useableMem)) + " KB (" + (((double) (Math.round(useableMem / maxMem * 1000000))) / 10000) + "%)" };
	}

	public static void JythonShell()
	{
		InteractiveConsole interp = null;
		try
		{
			String interpClass = PySystemState.registry.getProperty("python.console", "org.python.util.InteractiveConsole");
			interp = (InteractiveConsole) Class.forName(interpClass).newInstance();
		}
		catch (Exception e)
		{
			interp = new InteractiveConsole();
		}
		PyModule mod = imp.addModule("__main__");
		interp.setLocals(mod.__dict__);
		try
		{
			interp.interact(null);
		}
		catch (Throwable t)
		{
			Py.printException(t);
		}
		interp.cleanup();
	}

	public static void handleIllegalPlayerAction(L2PcInstance actor, String message, int punishment)
	{
		ThreadPoolManager.getInstance().scheduleGeneral(new IllegalPlayerAction(actor, message, punishment), 5000);
	}

	public static String getRelativePath(File base, File file)
	{
		return file.toURI().getPath().substring(base.toURI().getPath().length());
	}

	/** Return degree value of object 2 to the horizontal line with object 1 being the origin */
	public static double calculateAngleFrom(L2Object obj1, L2Object obj2)
	{
		return calculateAngleFrom(obj1.getX(), obj1.getY(), obj2.getX(), obj2.getY());
	}

	/** Return degree value of object 2 to the horizontal line with object 1 being the origin */
	public static double calculateAngleFrom(int obj1X, int obj1Y, int obj2X, int obj2Y)
	{
		double angleTarget = Math.toDegrees(Math.atan2(obj1Y - obj2Y, obj1X - obj2X));
		if (angleTarget < 0)
			angleTarget += 360;
		return angleTarget;
	}

	public static double convertHeadingToDegree(int clientHeading)
	{
		double degree = (clientHeading / 182.04) - 180;
		if (degree < 0)
			degree += 360;
		return degree;
	}

	public static int convertDegreeToClientHeading(double degree)
	{
		if (degree > 180)
			degree -= 360;
		return (int) ((degree + 180) * 182.04);
	}

	public static int calculateHeadingFrom(L2Object obj1, L2Object obj2)
	{
		return calculateHeadingFrom(obj1.getX(), obj1.getY(), obj2.getX(), obj2.getY());
	}

	public static int calculateHeadingFrom(int obj1X, int obj1Y, int obj2X, int obj2Y)
	{
		return (int) (Math.atan2(obj1Y - obj2Y, obj1X - obj2X) * 10430.38 + 32768);
	}

	public static double calculateDistance(int x1, int y1, int z1, int x2, int y2)
	{
		return calculateDistance(x1, y1, 0, x2, y2, 0, false);
	}

	public static double calculateDistance(int x1, int y1, int z1, int x2, int y2, int z2, boolean includeZAxis)
	{
		double dx = (double) x1 - x2;
		double dy = (double) y1 - y2;

		if (includeZAxis)
		{
			double dz = z1 - z2;
			return Math.sqrt((dx * dx) + (dy * dy) + (dz * dz));
		}
		else
			return Math.sqrt((dx * dx) + (dy * dy));
	}

	public static double calculateDistance(L2Object obj1, L2Object obj2, boolean includeZAxis)
	{
		if (obj1 == null || obj2 == null)
			return 1000000;
		return calculateDistance(obj1.getPosition().getX(), obj1.getPosition().getY(), obj1.getPosition().getZ(), obj2.getPosition().getX(), obj2.getPosition()
				.getY(), obj2.getPosition().getZ(), includeZAxis);
	}

	/**
	 * Capitalizes the first letter of a string, and returns the result.<BR>
	 * (Based on ucfirst() function of PHP)
	 * 
	 * @param String str
	 * @return String containing the modified string.
	 */
	public static String capitalizeFirst(String str)
	{
		str = str.trim();

		if (str.length() > 0 && Character.isLetter(str.charAt(0)))
			return str.substring(0, 1).toUpperCase() + str.substring(1);

		return str;
	}

	/**
	* Capitalizes the first letter of every "word" in a string.<BR>
	* (Based on ucwords() function of PHP)
	* 
	* @param String str
	* @return String containing the modified string.
	*/
	public static String capitalizeWords(String str)
	{
		char[] charArray = str.toCharArray();
		String result = "";

		// Capitalize the first letter in the given string!
		charArray[0] = Character.toUpperCase(charArray[0]);

		for (int i = 0; i < charArray.length; i++)
		{
			if (Character.isWhitespace(charArray[i]))
				charArray[i + 1] = Character.toUpperCase(charArray[i + 1]);

			result += Character.toString(charArray[i]);
		}

		return result;
	}

	/**
	 *  Checks if object is within range, adding collisionRadius
	 */
	public static boolean checkIfInRange(int range, L2Object obj1, L2Object obj2, boolean includeZAxis)
	{
		if(obj1.getInstanceId()!=obj2.getInstanceId())
			return false;
		
		if (range == -1)
			return true; // not limited

		int rad = 0;
		if (obj1 instanceof L2Character)
			rad += ((L2Character) obj1).getTemplate().getCollisionRadius();
		if (obj2 instanceof L2Character)
			rad += ((L2Character) obj2).getTemplate().getCollisionRadius();

		double dx = obj1.getX() - obj2.getX();
		double dy = obj1.getY() - obj2.getY();

		if (includeZAxis)
		{
			double dz = obj1.getZ() - obj2.getZ();
			double d = dx * dx + dy * dy + dz * dz;

			return d <= range * range + 2 * range * rad + rad * rad;
		}
		else
		{
			double d = dx * dx + dy * dy;

			return d <= range * range + 2 * range * rad + rad * rad;
		}
	}

	/*
	 *  Checks if object is within short (sqrt(int.max_value)) radius, 
	 *  not using collisionRadius. Faster calculation than checkIfInRange
	 *  if distance is short and collisionRadius isn't needed.
	 *  Not for long distance checks (potential teleports, far away castles etc)
	 */
	public static boolean checkIfInShortRadius(int radius, L2Object obj1, L2Object obj2, boolean includeZAxis)
	{
		if (obj1 == null || obj2 == null)
			return false;
		if (radius == -1)
			return true; // not limited

		int dx = obj1.getX() - obj2.getX();
		int dy = obj1.getY() - obj2.getY();

		if (includeZAxis)
		{
			int dz = obj1.getZ() - obj2.getZ();
			return dx * dx + dy * dy + dz * dz <= radius * radius;
		}
		else
		{
			return dx * dx + dy * dy <= radius * radius;
		}
	}

	/**
	 * Returns a delimited string for an given array of string elements.<BR>
	 * (Based on implode() in PHP)
	 * 
	 * @param String[] strArray
	 * @param String strDelim
	 * @return String implodedString
	 */
	public static String implodeString(String[] strArray, String strDelim)
	{
		String result = "";

		for (String strValue : strArray)
			result += strValue + strDelim;

		return result;
	}

	/**
	 * Returns a delimited string for an given collection of string elements.<BR>
	 * (Based on implode() in PHP)
	 * 
	 * @param Collection&lt;String&gt; strCollection
	 * @param String strDelim
	 * @return String implodedString
	 */
	public static String implodeString(Collection<String> strCollection, String strDelim)
	{
		return implodeString(strCollection.toArray(new String[strCollection.size()]), strDelim);
	}

	/**
	 * Returns the rounded value of val to specified number of digits 
	 * after the decimal point.<BR>
	 * (Based on round() in PHP) 
	 * 
	 * @param float val
	 * @param int numPlaces
	 * @return float roundedVal
	 */
	public static float roundTo(float val, int numPlaces)
	{
		if (numPlaces <= 1)
			return Math.round(val);

		float exponent = (float) Math.pow(10, numPlaces);

		return (Math.round(val * exponent) / exponent);
	}

	public static File[] getDatapackFiles(String dirname, String extention)
	{
		File dir = new File(Config.DATAPACK_ROOT, "data/" + dirname);
		if (!dir.exists())
			return null;

		CustomFileNameFilter filter = new CustomFileNameFilter(extention);

		return dir.listFiles(filter);
	}

	public static boolean isAlphaNumeric(String text)
	{
		if (text == null)
			return false;
		boolean result = true;
		char[] chars = text.toCharArray();
		for (int i = 0; i < chars.length; i++)
		{
			if (!Character.isLetterOrDigit(chars[i]))
			{
				result = false;
				break;
			}
		}
		return result;
	}

	/**
	 * Return amount of adena formatted with "," delimiter
	 * @param amount
	 * @return String formatted adena amount
	 */
	public static String formatAdena(int amount)
	{
		String s = "";
		int rem = amount % 1000;
		s = Integer.toString(rem);
		amount = (amount - rem) / 1000;
		while (amount > 0)
		{
			if (rem < 99)
				s = '0' + s;
			if (rem < 9)
				s = '0' + s;
			rem = amount % 1000;
			s = Integer.toString(rem) + "," + s;
			amount = (amount - rem) / 1000;
		}
		return s;
	}

	/**
	 * 
	 * @param s
	 */
	public static void printSection(String s)
	{
		int maxlength = 79;
		s = "-[ " + s + " ]";
		int slen = s.length();
		if (slen > maxlength)
		{
			System.out.println(s);
			return;
		}
		int i;
		for (i = 0; i < (maxlength - slen); i++)
			s = "=" + s;
		System.out.println(s);
	}

	public static int hash(int a)
	{
		a = ~a + (a << 15);
		a = a ^ (a >>> 12);
		a = a + (a << 2);
		a = a ^ (a >>> 4);
		a = (a + (a << 3)) + (a << 11);
		a = a ^ (a >>> 16);
		return a;
	}
}
