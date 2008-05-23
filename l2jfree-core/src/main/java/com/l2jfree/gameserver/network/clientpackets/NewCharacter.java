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
package com.l2jfree.gameserver.network.clientpackets;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.l2jfree.gameserver.datatables.CharTemplateTable;
import com.l2jfree.gameserver.model.base.ClassId;
import com.l2jfree.gameserver.network.serverpackets.NewCharacterSuccess;
import com.l2jfree.gameserver.templates.L2PcTemplate;

/**
 * This class ...
 * 
 * @version $Revision: 1.3.4.5 $ $Date: 2005/03/27 15:29:30 $
 */
public class NewCharacter extends L2GameClientPacket
{
	private static final String _C__0E_NEWCHARACTER = "[C] 0E NewCharacter";
	private final static Log _log = LogFactory.getLog(NewCharacter.class.getName());

	/**
	 * packet type id 0x0e
	 * format:		c
	 * @param rawPacket
	 */
    @Override
    protected void readImpl()
    {
    }

    @Override
    protected void runImpl()
	{
		if (_log.isDebugEnabled()) _log.debug("CreateNewChar");
		
		NewCharacterSuccess ct = new NewCharacterSuccess();
		
		L2PcTemplate template = CharTemplateTable.getInstance().getTemplate(0);
		ct.addChar(template);

		template = CharTemplateTable.getInstance().getTemplate(ClassId.fighter); // human fighter
		ct.addChar(template);

		template = CharTemplateTable.getInstance().getTemplate(ClassId.mage); // human mage
		ct.addChar(template);

		template = CharTemplateTable.getInstance().getTemplate(ClassId.elvenFighter); // elf fighter
		ct.addChar(template);

		template = CharTemplateTable.getInstance().getTemplate(ClassId.elvenMage); // elf mage
		ct.addChar(template);

		template = CharTemplateTable.getInstance().getTemplate(ClassId.darkFighter); // dark elf fighter
		ct.addChar(template);

		template = CharTemplateTable.getInstance().getTemplate(ClassId.darkMage); // dark elf mage
		ct.addChar(template);

		template = CharTemplateTable.getInstance().getTemplate(ClassId.orcFighter); // orc fighter
		ct.addChar(template);
		
		template = CharTemplateTable.getInstance().getTemplate(ClassId.orcMage); // orc mage
		ct.addChar(template);

		template = CharTemplateTable.getInstance().getTemplate(ClassId.dwarvenFighter); // dwarf fighter
		ct.addChar(template);

		template = CharTemplateTable.getInstance().getTemplate(ClassId.maleSoldier); //kamael male soldier
		ct.addChar(template);

		template = CharTemplateTable.getInstance().getTemplate(ClassId.femaleSoldier); // kamael female soldier
		ct.addChar(template);

		sendPacket(ct);
	}

	/* (non-Javadoc)
	 * @see com.l2jfree.gameserver.clientpackets.ClientBasePacket#getType()
	 */
	@Override
	public String getType()
	{
		return _C__0E_NEWCHARACTER;
	}
}
