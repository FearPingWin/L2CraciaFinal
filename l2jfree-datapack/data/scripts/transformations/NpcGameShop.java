package transformations;

import java.util.StringTokenizer;

import com.l2jfree.gameserver.gameobjects.L2Npc;
import com.l2jfree.gameserver.gameobjects.L2Player;
import com.l2jfree.gameserver.model.quest.Quest;
import com.l2jfree.gameserver.network.packets.server.NpcHtmlMessage;
import com.l2jfree.gameserver.datatables.MultisellTable;

/**
 * NpcGameShop — HTML category menu -> native MultiSell per category.
 * NPC_ID: 998. Two categories: Quest Items and Items.
 * Multisell lists: 998001 (Quest), 998002 (Items).
 */
public final class NpcGameShop extends Quest {
    private static final int NPC_ID      = 998;
    private static final int LIST_QUEST  = 998001; // data/multisell/998001.xml
    private static final int LIST_ITEMS  = 998002; // data/multisell/998002.xml

    public NpcGameShop() {
        super(-1, "NpcGameShop", "transformations");
        addStartNpc(NPC_ID);
        addFirstTalkId(NPC_ID);
        addTalkId(NPC_ID);
    }

    @Override
    public String onFirstTalk(L2Npc npc, L2Player player) {
        return showMenu(npc, player);
    }

    @Override
    public String onTalk(L2Npc npc, L2Player player) {
        return showMenu(npc, player);
    }

    @Override
    public String onAdvEvent(String event, L2Npc npc, L2Player player) {
        if (player == null || npc == null)
            return null;
        try {
            if ("menu".equalsIgnoreCase(event) || "_".equals(event))
                return showMenu(npc, player);

            StringTokenizer st = new StringTokenizer(event, " ");
            String cmd = st.hasMoreTokens() ? st.nextToken() : "";

            if ("open".equalsIgnoreCase(cmd)) {
                int cat = st.hasMoreTokens() ? Integer.parseInt(st.nextToken()) : 0; // 0=quest, 1=items
                openMultiSell(npc, player, cat);
                return null;
            }
        } catch (Exception e) {
            player.sendMessage("Invalid command.");
        }
        return null;
    }

    // --- UI: simple category menu ---
    private String showMenu(L2Npc npc, L2Player player) {
        StringBuilder sb = new StringBuilder(512);
        sb.append("<html><body><center>");
        sb.append("<font color=LEVEL>Game Shop</font><br1>");
        sb.append("<table width=300>");
        sb.append("<tr><td align=center><button value=\"Quest Items\" action=\"bypass -h Quest NpcGameShop open 0\" width=240 height=24 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>");
        sb.append("<tr><td align=center><button value=\"Items\" action=\"bypass -h Quest NpcGameShop open 1\" width=240 height=24 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>");
        sb.append("</table>");
        sb.append("</center></body></html>");
        NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
        html.setHtml(sb.toString());
        player.sendPacket(html);
        return null;
    }

    // --- Logic: open native window for the chosen category ---
    private void openMultiSell(L2Npc npc, L2Player player, int cat) {
        final int listId = (cat <= 0) ? LIST_QUEST : LIST_ITEMS;
        try {
            MultisellTable.getInstance().separateAndSend(listId, player, npc.getNpcId(), false, 0.0);
        } catch (Throwable t) {
            player.sendMessage("Failed to open store. Check multisell lists 998001/998002.");
        }
    }

    public static void main(String[] args) { new NpcGameShop(); }
}
