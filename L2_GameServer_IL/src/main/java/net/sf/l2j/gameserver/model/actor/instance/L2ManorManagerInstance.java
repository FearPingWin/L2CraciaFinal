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

import javolution.text.TextBuilder;
import javolution.util.FastList;
import net.sf.l2j.Config;
import net.sf.l2j.gameserver.datatables.ItemTable;
import net.sf.l2j.gameserver.model.CropProcure;
import net.sf.l2j.gameserver.model.L2ItemInstance;
import net.sf.l2j.gameserver.model.L2Manor;
import net.sf.l2j.gameserver.model.L2TradeList;
import net.sf.l2j.gameserver.model.SeedProduction;
import net.sf.l2j.gameserver.serverpackets.BuyListSeed;
import net.sf.l2j.gameserver.serverpackets.ExShowSeedInfo;
import net.sf.l2j.gameserver.serverpackets.InventoryUpdate;
import net.sf.l2j.gameserver.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.serverpackets.SellListProcure;
import net.sf.l2j.gameserver.templates.L2NpcTemplate;

/**
 * Done By L2Emuproject Team
 * User: Scar69
 * Date: Jan 05, 2006
 * Time: 20:13 PM
 */
public class L2ManorManagerInstance extends L2NpcInstance
{
    private FastList<SeedProduction> _SeedIds = new FastList<SeedProduction>();

    private L2TradeList _tradeList;

    public L2TradeList getTradeList()
    {
        return _tradeList;
    }

    public L2ManorManagerInstance(int objectId, L2NpcTemplate template)
    {
        //Erased some lines
        super(objectId, template);
    }

    public void onBypassFeedback(L2PcInstance player, String command)
    {
        if (Config.ALLOW_MANOR)
        {
            if (command.startsWith("buy_seeds"))
            {
                int castleID = Integer.parseInt(command.substring(10));
                _tradeList = new L2TradeList(0);
                // L2Object target = player.getTarget();
                //  L2ManorManagerInstance manor = (target != null && target instanceof L2ManorManagerInstance) ? (L2ManorManagerInstance)target : null;

                _SeedIds = getCastle().getSeedProduction();

                for (SeedProduction s : _SeedIds)
                {
                    L2ItemInstance item = ItemTable.getInstance().createDummyItem(s.getSeedId());
                    item.setPriceToSell(s.getPrice());
                    item.setCount(s.getCanProduce());
                    if ((item.getCount() > 0) && (item.getPriceToSell() > 0)) _tradeList.addItem(item);
                }

                /*            for( int itemId : _SeedIds )
                 {
                 L2ItemInstance item = ItemTable.getInstance().createDummyItem(itemId);
                 if(itemId < 5650 )
                 item.setPriceToSell(1000);
                 else
                 item.setPriceToSell(3000);

                 item.setCount(100);
                 _tradeList.addItem(item);
                 }*/

                BuyListSeed bl = new BuyListSeed(_tradeList, castleID, player.getAdena());
                player.sendPacket(bl);
            }
            else if (command.startsWith("show_seed"))
            {
                player.sendPacket(new ExShowSeedInfo());
            }
            else if (command.startsWith("show_procure"))
            {
                showManorProcure(player);
            }

            else if (command.startsWith("buy_harvester"))
            {
                if (player.getAdena() > 1000)
                {
                    player.reduceAdena("Buy harvester", 1000, this, true);
                    L2ItemInstance item = player.getInventory().addItem("Buy harvester", 5125, 1,
                                                                        player, this);
                    InventoryUpdate playerIU = new InventoryUpdate();
                    playerIU.addNewItem(item);
                    player.sendPacket(playerIU);
                }
            }
            else if (command.startsWith("sell_crop"))
            {
                int castleID = Integer.parseInt(command.substring(10));
                player.sendPacket(new SellListProcure(player, castleID));
            }
        }
        else
        {
            TextBuilder msg = new TextBuilder("<html><body>");
            msg.append("Enable the manor system for use this function");
            msg.append("</body></html>");
            this.sendHtmlMessage(player, msg.toString());
        }
        super.onBypassFeedback(player, command);
    }

    public String getHtmlPath(int npcId, int val)
    {
        String pom = "";
        if (val == 0)
        {
            pom = "" + npcId;
        }
        else
        {
            pom = npcId + "-" + val;
        }
        return "data/html/manor/" + pom + ".htm";
    }

    private void sendHtmlMessage(L2PcInstance player, String htmlMessage)
    {
        NpcHtmlMessage html = new NpcHtmlMessage(1);
        html.setHtml(htmlMessage);
        html.replace("%objectId%", String.valueOf(getObjectId()));
        html.replace("%npcname%", getName());
        player.sendPacket(html);
    }

    private void showManorProcure(L2PcInstance player)
    {
        if (getCastle() == null) return;

        FastList<CropProcure> crops = getCastle().getManorRewards();
        NpcHtmlMessage html = new NpcHtmlMessage(1);
        String r = "";

        for (CropProcure crop : crops)
        {
            int cou = L2Manor.getInstance().getRewardAmount(crop.getId(),
                                                            getCastle().getCropReward(crop.getId()));
            r += "<tr><td>" + ItemTable.getInstance().getTemplate(crop.getId()).getName() + "</td>";
            r += "<td>" + crop.getAmount() + "</td>";
            r += "<td>" + crop.getReward() + "</td><td>" + cou + "</td></tr>";
        }

        html.setFile("data/html/chamberlain/chamberlain-manor-procure.htm");
        html.replace("%objectId%", String.valueOf(getObjectId()));
        html.replace("%npcname%", getName());
        html.replace("%table%", r);
        player.sendPacket(html);
    }

}