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
package com.l2jfree.gameserver.model.actor.instance;

import java.util.StringTokenizer;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.l2jfree.gameserver.datatables.SkillTable;
import com.l2jfree.gameserver.datatables.SkillTreeTable;
import com.l2jfree.gameserver.datatables.TradeListTable;
import com.l2jfree.gameserver.model.L2Skill;
import com.l2jfree.gameserver.model.L2SkillLearn;
import com.l2jfree.gameserver.network.SystemMessageId;
import com.l2jfree.gameserver.network.serverpackets.AcquireSkillList;
import com.l2jfree.gameserver.network.serverpackets.ActionFailed;
import com.l2jfree.gameserver.network.serverpackets.SystemMessage;
import com.l2jfree.gameserver.templates.L2NpcTemplate;

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
            if (player.isTransformed())
                return;

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
        
        player.sendPacket(ActionFailed.STATIC_PACKET);
    }
}
