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
package com.l2jfree.gameserver.network.packets.client;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.l2jfree.gameserver.gameobjects.L2Object;
import com.l2jfree.gameserver.gameobjects.L2Player;
import com.l2jfree.gameserver.model.world.L2World;
import com.l2jfree.gameserver.network.SystemMessageId;
import com.l2jfree.gameserver.network.packets.L2ClientPacket;
import com.l2jfree.gameserver.network.packets.server.RecipeShopItemInfo;
import com.l2jfree.L2DatabaseFactory;
import com.l2jfree.gameserver.datatables.RecipeTable;
import com.l2jfree.gameserver.gameobjects.instance.L2NpcInstance;
import com.l2jfree.gameserver.model.items.recipe.L2RecipeList;
import com.l2jfree.gameserver.gameobjects.L2Npc;


/**
 * This class ...
 * cdd
 * @version $Revision: 1.1.2.1.2.2 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestRecipeShopMakeInfo extends L2ClientPacket
{
	private static final String _C__B5_RequestRecipeShopMakeInfo = "[C] b5 RequestRecipeShopMakeInfo";
	
	private int _objectId;
	private int _recipeId;
	
	@Override
	protected void readImpl()
	{
		_objectId = readD();
		_recipeId = readD();
	}
	
@Override
protected void runImpl()
{
    L2Player activeChar = getClient().getActiveChar();
    if (activeChar == null)
        return;

    L2Object obj = null;

    if (activeChar.getTargetId() == _objectId)
        obj = activeChar.getTarget();

    if (obj == null) {
        obj = L2World.getInstance().getPlayer(_objectId);
        if (obj == null)
            obj = L2World.getInstance().findObject(_objectId); // ← важное отличие
    }

    if (obj == null) {
        requestFailed(SystemMessageId.TARGET_IS_INCORRECT);
        return;
    }

    // СТАРЫЙ ПУТЬ: производитель — игрок
    if (obj instanceof L2Player) {
        sendPacket(new RecipeShopItemInfo((L2Player)obj, _recipeId));
        sendAF();
        return;
    }

    // НОВЫЙ ПУТЬ: производитель — наш НПЦ (id=999)
if (obj instanceof L2Npc) {
    L2Npc npc = (L2Npc)obj;
    if (npc.getNpcId() != 999) { sendAF(); return; }
        long price = 0L; // если записи нет — 0
        try (java.sql.Connection con = com.l2jfree.L2DatabaseFactory.getInstance().getConnection();
             java.sql.PreparedStatement ps = con.prepareStatement(
                 "SELECT fee_amount FROM npc_crafter_recipes WHERE npc_id=? AND recipe_id=? AND is_learned=1 AND enabled=1")) {
            ps.setInt(1, npc.getNpcId());
            ps.setInt(2, _recipeId);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) price = rs.getLong(1);
            }
        } catch (Exception ignored) {}

		sendPacket(new RecipeShopItemInfo(_objectId, _recipeId, 0, 0, (int)price));
        sendAF();
        return;
    }
    sendAF();
    return;
}
	@Override
	public String getType()
	{
		return _C__B5_RequestRecipeShopMakeInfo;
	}
}
