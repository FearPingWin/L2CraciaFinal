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
package com.l2jfree.gameserver.handler.skillhandlers;

import com.l2jfree.gameserver.datatables.NpcTable;
import com.l2jfree.gameserver.handler.ISkillHandler;
import com.l2jfree.gameserver.idfactory.IdFactory;
import com.l2jfree.gameserver.instancemanager.FortSiegeManager;
import com.l2jfree.gameserver.instancemanager.SiegeManager;
import com.l2jfree.gameserver.model.L2Character;
import com.l2jfree.gameserver.model.L2Object;
import com.l2jfree.gameserver.model.L2Skill;
import com.l2jfree.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfree.gameserver.model.actor.instance.L2SiegeFlagInstance;
import com.l2jfree.gameserver.model.entity.FortSiege;
import com.l2jfree.gameserver.model.entity.Siege;
import com.l2jfree.gameserver.templates.L2NpcTemplate;
import com.l2jfree.gameserver.templates.L2SkillType;

/** 
 * @author _drunk_ 
 * 
 */
public class SiegeFlag implements ISkillHandler
{
	private static final L2SkillType[]	SKILL_IDS	=
													{ L2SkillType.SIEGEFLAG };

	public void useSkill(L2Character activeChar, @SuppressWarnings("unused")
	L2Skill skill, @SuppressWarnings("unused")
	L2Object... targets)
	{
		if (!(activeChar instanceof L2PcInstance))
			return;

		L2PcInstance player = (L2PcInstance) activeChar;

		Siege siege = SiegeManager.getInstance().getSiege(player);
		// In a siege zone
		if (siege != null)
		{
			if (SiegeManager.checkIfOkToPlaceFlag(activeChar, false)) // checkonly = false to send message to player if check fails
			{
				L2NpcTemplate template = NpcTable.getInstance().getTemplate(35062);
				if (skill != null && template != null) {
					// spawn a new flag
					L2SiegeFlagInstance flag = new L2SiegeFlagInstance(player, IdFactory.getInstance().getNextId(), template,
							skill.isAdvanced());
					flag.setTitle(player.getClan().getName());
					flag.getStatus().setCurrentHpMp(flag.getMaxHp(), flag.getMaxMp());
					flag.setHeading(player.getHeading());
					flag.spawnMe(player.getX(), player.getY(), player.getZ() + 50);
					siege.getFlag(player.getClan()).add(flag);
				}
			}
		}
		else
		{
			FortSiege fsiege = FortSiegeManager.getInstance().getSiege(player);
			if (fsiege != null && FortSiegeManager.checkIfOkToPlaceFlag(activeChar, false))
			{
				L2NpcTemplate template = NpcTable.getInstance().getTemplate(35062);
				if (skill != null && template != null) {
					// spawn a new flag
					L2SiegeFlagInstance flag = new L2SiegeFlagInstance(player, IdFactory.getInstance().getNextId(), template,
							skill.isAdvanced());
					flag.setTitle(player.getClan().getName());
					flag.getStatus().setCurrentHpMp(flag.getMaxHp(), flag.getMaxMp());
					flag.setHeading(player.getHeading());
					flag.spawnMe(player.getX(), player.getY(), player.getZ() + 50);
					fsiege.getFlag(player.getClan()).add(flag);
				}
			}
		}
	}

	public L2SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}
