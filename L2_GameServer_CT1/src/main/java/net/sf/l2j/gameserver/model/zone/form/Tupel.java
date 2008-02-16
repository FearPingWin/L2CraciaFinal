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
package net.sf.l2j.gameserver.model.zone.form;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.w3c.dom.Node;

public class Tupel
{
	protected static Log _log = LogFactory.getLog(Tupel.class.getName());

	public int x = 0;
	public int y = 0;

	public static Tupel parseTupel(Node n, int zoneId)
	{
		try
		{
			Tupel t = new Tupel();
			t.x = Integer.parseInt(n.getAttributes().getNamedItem("x").getNodeValue());
			t.y = Integer.parseInt(n.getAttributes().getNamedItem("y").getNodeValue());
			return t;
		}
		catch(NullPointerException npe)
		{
			_log.error("x or y value missing in zone "+zoneId);
		}
		catch(NumberFormatException nfe)
		{
			_log.error("x or y value not a number in zone "+zoneId);
		}
		return null;
	}
}
