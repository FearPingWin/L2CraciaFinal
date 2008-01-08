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
package net.sf.l2j.gameserver.network.serverpackets;

import net.sf.l2j.gameserver.model.L2ClanMember;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;

/**
 * This class ...
 * 
 * @version $Revision: 1.3.2.1.2.4 $ $Date: 2005/03/27 15:29:39 $
 */
public class PledgeShowMemberListAdd extends L2GameServerPacket
{
	private static final String _S__55_PLEDGESHOWMEMBERLISTADD = "[S] 55 PledgeShowMemberListAdd";
	private String _name;
	private int _lvl;
	private int _classId;
	private int _isOnline;
	private int _pledgeType;
	private int _sex;
	private int _race;

	public PledgeShowMemberListAdd(L2PcInstance player)
	{
		_name = player.getName();
		_lvl = player.getLevel();
		_classId = player.getClassId().getId();
		_isOnline = (player.isOnline() == 1 ? player.getObjectId() : 0);
		_pledgeType = player.getSubPledgeType();
		_sex = player.getAppearance().getSex() ? 1 : 0;
		_race = player.getRace().ordinal();
	}

	public PledgeShowMemberListAdd(L2ClanMember cm)
	{
		_name = cm.getName();
		_lvl = cm.getLevel();
		_classId = cm.getClassId();
		_isOnline = (cm.isOnline() ? cm.getObjectId() : 0);
		_pledgeType = cm.getSubPledgeType();
		_sex = cm.getSex();
		_race = cm.getRace();
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x5c);
		writeS(_name);
		writeD(_lvl);
		writeD(_classId);
		writeD(_sex); 
		writeD(_race);
		writeD(_isOnline); // 1=online 0=offline
		writeD(_pledgeType); 
	}

	/* (non-Javadoc)
	 * @see net.sf.l2j.gameserver.serverpackets.ServerBasePacket#getType()
	 */
	@Override
	public String getType()
	{
		return _S__55_PLEDGESHOWMEMBERLISTADD;
	}
}
