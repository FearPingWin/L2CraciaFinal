package transformations;

import java.sql.ResultSet;
import java.util.*;

import com.l2jfree.gameserver.model.quest.Quest;
import com.l2jfree.gameserver.model.quest.QuestState;
import com.l2jfree.gameserver.gameobjects.L2Object;
import com.l2jfree.L2DatabaseFactory;
import com.l2jfree.gameserver.gameobjects.L2Npc;
import com.l2jfree.gameserver.gameobjects.L2Player;
import com.l2jfree.gameserver.model.items.L2ItemInstance;
import com.l2jfree.gameserver.model.items.recipe.L2RecipeList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import com.l2jfree.gameserver.model.items.manufacture.L2ManufactureItem;
import com.l2jfree.gameserver.model.items.manufacture.L2ManufactureList; 
import com.l2jfree.gameserver.network.packets.server.RecipeShopSellList; 

public class NpcMasterCrafter extends Quest
{
    private static final Map<Integer,String> ITEM_NAME_CACHE = new HashMap<Integer,String>();
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
            if ("teach_open".equals(event))
            {
                return showTeachList(npc, player);
            }
            if (event.startsWith("teach_give "))
            {
                int objId = Integer.parseInt(event.substring("teach_give ".length()));
                return confirmTeach(npc, player, objId);
            }
            if ("craft_menu".equals(event))
            {
                return showCraftCategories(npc, player);
            }
            if (event.startsWith("craft_cat "))
            {
                String cat = event.substring("craft_cat ".length());
                return openPrivateCraft(npc, player, cat);
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
        sb.append("<table width=260>");
        sb.append("<tr><td align=center><button value=\"Crystallize items\" action=\"bypass -h Quest NpcMasterCrafter cryst_open\" width=240 height=24 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>");
        sb.append("<tr><td align=center><button value=\"Teach a recipe\" action=\"bypass -h Quest NpcMasterCrafter teach_open\" width=240 height=24 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>");
        sb.append("<tr><td align=center><button value=\"Craft items\" action=\"bypass -h Quest NpcMasterCrafter craft_menu\" width=240 height=24 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>");
        sb.append("</table>");
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
private String showTeachList(L2Npc npc, L2Player player)
{
    List<L2ItemInstance> recipeScrolls = new ArrayList<L2ItemInstance>();

    for (L2ItemInstance it : player.getInventory().getItems())
    {
        if (it == null) continue;
        if (it.isEquipped()) continue;

        L2RecipeList r = com.l2jfree.gameserver.datatables.RecipeTable.getInstance().getRecipeByItemId(it.getItemId());
        if (r == null) continue;

        if (isRecipeLearned(NPC_ID, r.getId())) continue; // уже изучен у NPC
        recipeScrolls.add(it);
    }

    // сортировка по нормальному названию рецепта
Collections.sort(recipeScrolls, new Comparator<L2ItemInstance>() {
    public int compare(L2ItemInstance a, L2ItemInstance b) {
        return getItemDisplayName(a.getItemId()).compareToIgnoreCase(getItemDisplayName(b.getItemId()));
    }
});

    StringBuilder sb = new StringBuilder(4096);
    sb.append("<html><body><center>");
    sb.append("<font color=LEVEL>Teach a recipe</font><br1>");
    sb.append("Give me a recipe scroll to learn it.<br><br>");

    if (recipeScrolls.isEmpty())
    {
        sb.append("No teachable recipes found in your inventory.<br>");
    }
    else
    {
        sb.append("<table width=280>");
        for (L2ItemInstance it : recipeScrolls)
        {
            String nice = getItemDisplayName(it.getItemId());
            sb.append("<tr>");
            sb.append("<td width=200>").append(cut(nice, 34)).append("</td>");
            sb.append("<td width=80 align=center><a action=\"bypass -h Quest NpcMasterCrafter teach_give ")
            .append(it.getObjectId()).append("\">give</a></td>");
            sb.append("</tr>");
        }
        sb.append("</table>");
    }

    sb.append("<br><button value=\"Back\" action=\"bypass -h Quest NpcMasterCrafter _\" width=120 height=24 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
    sb.append("</center></body></html>");
    return sb.toString();
}
private String confirmTeach(L2Npc npc, L2Player player, int recipeScrollObjId)
{
    L2ItemInstance scroll = player.getInventory().getItemByObjectId(recipeScrollObjId);
    if (scroll == null) return sendMsg(player, "Recipe scroll not found.");

    String nice = getItemDisplayName(scroll.getItemId()); // имя из etcitem/armor/weapon
    L2RecipeList r = com.l2jfree.gameserver.datatables.RecipeTable.getInstance()
            .getRecipeByItemId(scroll.getItemId());
    if (r == null) return sendMsg(player, "This item is not a recipe scroll: " + nice + ".");
    if (isRecipeLearned(NPC_ID, r.getId())) return sendMsg(player, "I already know this recipe: " + nice + ".");

    if (!player.destroyItem("TeachRecipe", scroll.getObjectId(), 1, (L2Object)npc, true))
        return sendMsg(player, "Failed to take the recipe scroll: " + nice + ".");

    if (!saveLearnedRecipe(NPC_ID, r.getId()))
        return sendMsg(player, "Database error while learning: " + nice + ".");

    return sendMsg(player, "Learned: " + nice);
}

private boolean isRecipeLearned(int npcId, int recipeId)
{
    try (Connection con = L2DatabaseFactory.getInstance().getConnection();
         PreparedStatement ps = con.prepareStatement(
             "SELECT is_learned FROM npc_crafter_recipes WHERE npc_id=? AND recipe_id=?"))
    {
        ps.setInt(1, npcId);
        ps.setInt(2, recipeId);
        try (ResultSet rs = ps.executeQuery())
        {
            return rs.next() && rs.getInt(1) == 1;
        }
    }
    catch (Exception e)
    {
        e.printStackTrace();
        return false;
    }
}

private boolean saveLearnedRecipe(int npcId, int recipeId)
{
    try (Connection con = L2DatabaseFactory.getInstance().getConnection();
         PreparedStatement ps = con.prepareStatement(
             "INSERT INTO npc_crafter_recipes (npc_id, recipe_id, is_learned, learned_at) " +
             "VALUES (?, ?, 1, NOW()) " +
             "ON DUPLICATE KEY UPDATE is_learned=1, learned_at=IFNULL(learned_at, NOW())"))
    {
        ps.setInt(1, npcId);
        ps.setInt(2, recipeId);
        return ps.executeUpdate() > 0;
    }
    catch (Exception e)
    {
        e.printStackTrace();
        return false;
    }
}
private String showCraftCategories(L2Npc npc, L2Player player)
{
    String[][] cats = new String[][]{
        {"Soulshots", "shots"},
        {"Resources", "mats"},
        {"Armor D-grade", "armor_d"},
        {"Weapons D-grade", "weapon_d"},
        {"Armor C-grade", "armor_c"},
        {"Weapons C-grade", "weapon_c"},
        {"Armor B-grade", "armor_b"},
        {"Weapons B-grade", "weapon_b"},
        {"Armor A-grade", "armor_a"},
        {"Weapons A-grade", "weapon_a"},
        {"Armor S-grade", "armor_s"},
        {"Weapons S-grade", "weapon_s"},
        {"S80 / S84", "s80_s84"}
    };

    StringBuilder sb = new StringBuilder(2048);
    sb.append("<html><body><center>");
    sb.append("<font color=LEVEL>Craft items</font><br1>");
    sb.append("<table width=300>");

    for (int i=0; i<cats.length; i+=2)
    {
        sb.append("<tr>");
        sb.append("<td width=150 align=center><button value=\"").append(cats[i][0])
          .append("\" action=\"bypass -h Quest NpcMasterCrafter craft_cat ").append(cats[i][1])
          .append("\" width=140 height=24 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
        if (i+1 < cats.length)
        {
            sb.append("<td width=150 align=center><button value=\"").append(cats[i+1][0])
              .append("\" action=\"bypass -h Quest NpcMasterCrafter craft_cat ").append(cats[i+1][1])
              .append("\" width=140 height=24 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
        }
        else sb.append("<td></td>");
        sb.append("</tr>");
    }
    sb.append("</table>");
    sb.append("<br><button value=\"Back\" action=\"bypass -h Quest NpcMasterCrafter _\" width=120 height=24 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
    sb.append("</center></body></html>");
    return sb.toString();
}

private String getItemDisplayName(int itemId)
{
    String cached = ITEM_NAME_CACHE.get(itemId);
    if (cached != null) return cached;

    String name = null;
    final String sql =
        "SELECT name FROM etcitem WHERE item_id=? " +
        "UNION ALL SELECT name FROM armor WHERE item_id=? " +
        "UNION ALL SELECT name FROM weapon WHERE item_id=? " +
        "UNION ALL SELECT name FROM custom_armor WHERE item_id=? " +
        "UNION ALL SELECT name FROM custom_weapon WHERE item_id=? " +
        "LIMIT 1";

    try (Connection con = L2DatabaseFactory.getInstance().getConnection();
         PreparedStatement ps = con.prepareStatement(sql))
    {
        ps.setInt(1, itemId);
        ps.setInt(2, itemId);
        ps.setInt(3, itemId);
        ps.setInt(4, itemId);
        ps.setInt(5, itemId);
        try (ResultSet rs = ps.executeQuery())
        {
            if (rs.next()) name = rs.getString(1);
        }
    }
    catch (Exception e) { e.printStackTrace(); }

    if (name == null) {
        L2RecipeList r = com.l2jfree.gameserver.datatables.RecipeTable.getInstance().getRecipeByItemId(itemId);
        if (r != null && r.getRecipeName() != null) name = r.getRecipeName();
    }
    if (name == null) name = "item:" + itemId;

    ITEM_NAME_CACHE.put(itemId, name);
    return name;
}

private String openPrivateCraft(L2Npc npc, L2Player player, String cat)
{
    L2ManufactureList list = new L2ManufactureList();
    list.setStoreName("Crafter of Mammon");

    try (Connection con = L2DatabaseFactory.getInstance().getConnection();
         PreparedStatement ps = con.prepareStatement(
             "SELECT recipe_id, fee_amount FROM npc_crafter_recipes " +
             "WHERE npc_id=? AND category=? AND is_learned=1 AND enabled=1 AND fee_currency=57 " +
             "ORDER BY recipe_id"))
    {
        ps.setInt(1, NPC_ID);
        ps.setString(2, cat);
        try (ResultSet rs = ps.executeQuery())
        {
            while (rs.next())
            {
                int recipeId = rs.getInt(1);
                long priceL  = rs.getLong(2);
                int price    = (priceL > Integer.MAX_VALUE) ? Integer.MAX_VALUE : (int)priceL;
                list.add(new L2ManufactureItem(recipeId, price));
            }
        }
    }
    catch (Exception e) { e.printStackTrace(); return sendMsg(player, "DB error."); }

    if (list.getList().isEmpty())
        return sendMsg(player, "No learned recipes in this category.");
    player.setTarget(npc);
    player.sendPacket(new RecipeShopSellList(npc.getObjectId(), player.getAdena(), list));
    return null;
    }
}
