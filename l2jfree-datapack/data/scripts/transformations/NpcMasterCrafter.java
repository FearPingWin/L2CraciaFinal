package transformations;

import java.util.*;

import com.l2jfree.gameserver.model.quest.Quest;
import com.l2jfree.gameserver.model.quest.QuestState;
import com.l2jfree.gameserver.gameobjects.L2Object;
import com.l2jfree.gameserver.gameobjects.L2Npc;
import com.l2jfree.gameserver.gameobjects.L2Player;
import com.l2jfree.gameserver.model.items.L2ItemInstance;

public class NpcMasterCrafter extends Quest
{
    private static final int NPC_ID = 999;

    private static final int CRYSTAL_D = 1458;
    private static final int CRYSTAL_C = 1459;
    private static final int CRYSTAL_B = 1460;
    private static final int CRYSTAL_A = 1461;
    private static final int CRYSTAL_S = 1462;

    private static final int COMMISSION_PERCENT = 10;
    private static final String VAR_CRYST_SEL = "cryst_sel";

    public NpcMasterCrafter()
    {
        super(-1, "NpcMasterCrafter", "transformations");
        addStartNpc(NPC_ID);
        addFirstTalkId(NPC_ID);
        addTalkId(NPC_ID);
    }

    @Override
    public String onFirstTalk(L2Npc npc, L2Player player)
    {
        return showMain(npc, player);
    }

    @Override
    public String onTalk(L2Npc npc, L2Player player)
    {
        return showMain(npc, player);
    }

    @Override
    public String onAdvEvent(String event, L2Npc npc, L2Player player)
    {
        try
        {
            if ("_".equals(event))
                return showMain(npc, player);

            if ("cryst_open".equals(event) || event.startsWith("cryst_search "))
            {
                String q = event.startsWith("cryst_search ") ? event.substring("cryst_search ".length()) : "";
                return showCrystList(npc, player, q);
            }
            if (event.startsWith("cryst_toggle "))
            {
                int objId = Integer.parseInt(event.substring("cryst_toggle ".length()));
                toggleSelection(player, objId);
                return showCrystList(npc, player, "");
            }
            if ("cryst_clear".equals(event))
            {
                setSelection(player, new HashSet<Integer>());
                return showCrystList(npc, player, "");
            }
            if ("cryst_confirm".equals(event))
            {
                return confirmCrystallize(npc, player);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return sendMsg(player, "Internal error.");
        }
        return null;
    }

    private String showMain(L2Npc npc, L2Player player)
    {
        StringBuilder sb = new StringBuilder(256);
        sb.append("<html><body><center>");
        sb.append("<font color=LEVEL>Crafter of Mammon</font><br1>");
        sb.append("<button value=\"Crystallize items\" action=\"bypass -h Quest NpcMasterCrafter cryst_open\" width=220 height=24 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
        sb.append("</center></body></html>");
        return sb.toString();
    }

    private String showCrystList(L2Npc npc, L2Player player, String query)
    {
        Set<Integer> sel = getSelection(player);
        List<L2ItemInstance> items = new ArrayList<L2ItemInstance>();

        for (L2ItemInstance it : player.getInventory().getItems()) {
            if (it == null || it.isEquipped()) continue;
            if (!it.getItem().isCrystallizable()) continue;
            try { if (it.isHeroItem()) continue; } catch (Throwable ignored) {}
            if (query != null && !query.isEmpty()) {
                String nm = String.valueOf(it.getItem().getName()).toLowerCase();
                if (!nm.contains(query.toLowerCase())) continue;
            }
            items.add(it);
        }
        Collections.sort(items, new Comparator<L2ItemInstance>() {
            public int compare(L2ItemInstance a, L2ItemInstance b) {
                String an = String.valueOf(a.getItem().getName());
                String bn = String.valueOf(b.getItem().getName());
                return an.compareToIgnoreCase(bn);
            }
        });

        StringBuilder sb = new StringBuilder(4096);
        sb.append("<html><body><center>");
        sb.append("<font color=LEVEL>Crystallize items</font><br1>");
        sb.append("Commission: 10% (min 1 per crystal type).<br><br>");

        sb.append("<table width=260><tr><td><edit var=\"q\" width=170></td>");
        sb.append("<td><button value=\"Search\" action=\"bypass -h Quest NpcMasterCrafter cryst_search $q\" width=70 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr></table>");

        if (items.isEmpty()) {
            sb.append("<br>No crystallizable items found.<br>");
        } else {
            sb.append("<table width=270>");
            for (L2ItemInstance it : items) {
                boolean checked = sel.contains(it.getObjectId());
                int cryType = it.getItem().getCrystalType();
                int cryCnt  = it.getItem().getCrystalCount(it.getEnchantLevel());
                String name = cut(String.valueOf(it.getItem().getName()), 26);
                if (it.getEnchantLevel() > 0) name += " +" + it.getEnchantLevel();

                sb.append("<tr>");
                sb.append("<td width=16>").append(checked ? "[x]" : "[ ]").append("</td>");
                sb.append("<td width=160>").append(name).append("</td>");
                sb.append("<td width=34 align=right>").append(cryCnt).append(" ").append(gradeName(cryType)).append("</td>");
                sb.append("<td width=60 align=center><a action=\"bypass -h Quest NpcMasterCrafter cryst_toggle ").append(it.getObjectId()).append("\">select</a></td>");
                sb.append("</tr>");
            }
            sb.append("</table>");
        }

        sb.append("<br><button value=\"Confirm\" action=\"bypass -h Quest NpcMasterCrafter cryst_confirm\" width=120 height=24 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
        sb.append(" <button value=\"Clear\" action=\"bypass -h Quest NpcMasterCrafter cryst_clear\" width=80 height=24 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
        sb.append(" <button value=\"Back\" action=\"bypass -h Quest NpcMasterCrafter _\" width=80 height=24 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
        sb.append("</center></body></html>");
        return sb.toString();
    }

    private String confirmCrystallize(L2Npc npc, L2Player player)
    {
        Set<Integer> sel = getSelection(player);
        if (sel.isEmpty()) return sendMsg(player, "No items selected.");

        long totalD=0, totalC=0, totalB=0, totalA=0, totalS=0;
        List<L2ItemInstance> toDestroy = new ArrayList<L2ItemInstance>();

        for (Integer objId : sel)
        {
            L2ItemInstance it = player.getInventory().getItemByObjectId(objId);
            if (it == null || it.isEquipped() || !it.getItem().isCrystallizable()) continue;
            int cnt = it.getItem().getCrystalCount(it.getEnchantLevel());
            if (cnt <= 0) continue;

            switch (it.getItem().getCrystalType())
            {
                case 1: totalD += cnt; break;
                case 2: totalC += cnt; break;
                case 3: totalB += cnt; break;
                case 4: totalA += cnt; break;
                case 5: totalS += cnt; break;
                default: break;
            }
            toDestroy.add(it);
        }

        if (toDestroy.isEmpty()) return sendMsg(player, "Nothing to crystallize.");

        long giveD = applyFee(totalD);
        long giveC = applyFee(totalC);
        long giveB = applyFee(totalB);
        long giveA = applyFee(totalA);
        long giveS = applyFee(totalS);

        for (L2ItemInstance it : toDestroy)
            player.destroyItem("CrystallizeNPC", it.getObjectId(), 1, (L2Object)npc, true);

        // give crystals to player
        if (giveD > 0) player.addItem("CrystallizeNPC", CRYSTAL_D, giveD, (L2Object)npc, true);
        if (giveC > 0) player.addItem("CrystallizeNPC", CRYSTAL_C, giveC, (L2Object)npc, true);
        if (giveB > 0) player.addItem("CrystallizeNPC", CRYSTAL_B, giveB, (L2Object)npc, true);
        if (giveA > 0) player.addItem("CrystallizeNPC", CRYSTAL_A, giveA, (L2Object)npc, true);
        if (giveS > 0) player.addItem("CrystallizeNPC", CRYSTAL_S, giveS, (L2Object)npc, true);

        setSelection(player, new HashSet<Integer>());

        StringBuilder msg = new StringBuilder("Crystallization complete. You received: ");
        boolean any=false;
        if (giveD>0){ msg.append(giveD).append(" D"); any=true; }
        if (giveC>0){ if(any) msg.append(", "); msg.append(giveC).append(" C"); any=true; }
        if (giveB>0){ if(any) msg.append(", "); msg.append(giveB).append(" B"); any=true; }
        if (giveA>0){ if(any) msg.append(", "); msg.append(giveA).append(" A"); any=true; }
        if (giveS>0){ if(any) msg.append(", "); msg.append(giveS).append(" S"); any=true; }
        if (!any) msg.append("0");
        return sendMsg(player, msg.toString());
    }

    private long applyFee(long total)
    {
        if (total <= 0) return 0;
        long fee = (total * COMMISSION_PERCENT) / 100;
        if (fee < 1) fee = 1;
        long give = total - fee;
        return give < 0 ? 0 : give;
    }

    private String gradeName(int crystalType)
    {
        switch (crystalType)
        {
            case 1: return "D";
            case 2: return "C";
            case 3: return "B";
            case 4: return "A";
            case 5: return "S";
            default: return "-";
        }
    }

    private Set<Integer> getSelection(L2Player player)
    {
        QuestState st = getOrCreateState(player);
        Object rawObj = st.get(VAR_CRYST_SEL);
        String raw = (rawObj == null) ? null : rawObj.toString();

        Set<Integer> res = new HashSet<Integer>();
        if (raw != null && !raw.isEmpty())
        {
            for (String s : raw.split(","))
            {
                if (s == null || s.isEmpty()) continue;
                try { res.add(Integer.parseInt(s.trim())); } catch (Exception ignored) {}
            }
        }
        return res;
    }

    private void setSelection(L2Player player, Set<Integer> set)
    {
        QuestState st = getOrCreateState(player);
        if (set == null || set.isEmpty()) st.unset(VAR_CRYST_SEL);
        else
        {
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            for (Integer i : set)
            {
                if (!first) sb.append(",");
                sb.append(i);
                first = false;
            }
            st.set(VAR_CRYST_SEL, sb.toString());
        }
    }

    private void toggleSelection(L2Player player, int objId)
    {
        Set<Integer> s = getSelection(player);
        if (s.contains(objId)) s.remove(objId); else s.add(objId);
        setSelection(player, s);
    }

    private QuestState getOrCreateState(L2Player player)
    {
        QuestState st = player.getQuestState(getName());
        if (st == null) st = newQuestState(player);
        return st;
    }

    private String sendMsg(L2Player player, String text)
    {
        return "<html><body><center>" + text +
            "<br><button value=\"Back\" action=\"bypass -h Quest NpcMasterCrafter _\" width=80 height=24 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></center></body></html>";
    }
    public static void main(String[] args) {
    new NpcMasterCrafter();
    }

    private String cut(String s, int n) {
    if (s == null) return "";
    return s.length() <= n ? s : s.substring(0, n - 1) + "...";
}
}
