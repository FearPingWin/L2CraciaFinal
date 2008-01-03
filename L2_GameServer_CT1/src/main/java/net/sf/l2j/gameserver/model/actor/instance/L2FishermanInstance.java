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
package net.sf.l2j.gameserver.model.actor.instance;

import java.util.StringTokenizer;

import net.sf.l2j.gameserver.datatables.SkillTable;
import net.sf.l2j.gameserver.datatables.SkillTreeTable;
import net.sf.l2j.gameserver.datatables.TradeListTable;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.L2SkillLearn;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.network.serverpackets.AcquireSkillList;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;
import net.sf.l2j.gameserver.templates.L2NpcTemplate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class L2FishermanInstance extends L2MerchantInstance
{
    private final static Log _log = LogFactory.getLog(L2FishermanInstance.class.getName());
    /**
     * @param objectId
     * @param template
     */
    public L2FishermanInstance(int objectId, L2NpcTemplate template)
    {
        super(objectId, template);
    }

    @Override
    public String getHtmlPath(int npcId, int val)
    {
        String pom = "";
        
        if (val == 0)
            pom = "" + npcId;
        else 
            pom = npcId + "-" + val;
        
        return "data/html/fisherman/" + pom + ".htm";
    }

    @Override
    public void onBypassFeedback(L2PcInstance player, String command)
    {
        if (command.startsWith("FishSkillList"))
        {
            player.setSkillLearningClassId(player.getClassId());
            showSkillList(player);
        }

        StringTokenizer st = new StringTokenizer(command, " ");
        String cmd = st.nextToken();
        
        if (cmd.equalsIgnoreCase("Buy"))
        {
            if (st.countTokens() < 1) return;
            int val = Integer.parseInt(st.nextToken());
            showBuyWindow(player, val);
        }
        else if (cmd.equalsIgnoreCase("Sell"))
        {
            showSellWindow(player);
        }
        else 
        {
            super.onBypassFeedback(player, command);
        }
    }   

    public void showSkillList(L2PcInstance player)
    {       
        L2SkillLearn[] skills = SkillTreeTable.getInstance().getAvailableSkills(player);
        AcquireSkillList asl = new AcquireSkillList(AcquireSkillList.skillType.Fishing);
        
        int counts = 0;

        for (L2SkillLearn s : skills)
        {
            L2Skill sk = SkillTable.getInstance().getInfo(s.getId(), s.getLevel());
            
            if (sk == null)
                continue;
            
            counts++;
            asl.addSkill(s.getId(), s.getLevel(), s.getLevel(), s.getSpCost(), 1);
        }
        
        if (counts == 0)
        {
            SystemMessage sm;
            int minlevel = SkillTreeTable.getInstance().getMinLevelForNewSkill(player);
            if (minlevel > 0)
            {
                // No more skills to learn, come back when you level.
                sm = new SystemMessage(SystemMessageId.DO_NOT_HAVE_FURTHER_SKILLS_TO_LEARN);
                sm.addNumber(minlevel);
            }
            else
            {
                sm = new SystemMessage(SystemMessageId.NO_MORE_SKILLS_TO_LEARN);
            }
            player.sendPacket(sm);
        }
        else 
        {
            player.sendPacket(asl);
        }
        
        player.sendPacket(new ActionFailed());
    }
}
