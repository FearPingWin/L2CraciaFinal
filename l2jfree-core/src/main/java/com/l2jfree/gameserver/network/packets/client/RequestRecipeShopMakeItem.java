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

import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;

import com.l2jfree.gameserver.gameobjects.L2Npc;
import com.l2jfree.L2DatabaseFactory;
import com.l2jfree.gameserver.datatables.RecipeTable;
import com.l2jfree.gameserver.gameobjects.L2Object;
import com.l2jfree.gameserver.gameobjects.L2Player;
import com.l2jfree.gameserver.model.world.L2World;
import com.l2jfree.gameserver.network.SystemMessageId;
import com.l2jfree.gameserver.network.packets.L2ClientPacket;
import com.l2jfree.gameserver.network.packets.server.RecipeShopItemInfo;
import com.l2jfree.gameserver.util.Util;

public class RequestRecipeShopMakeItem extends L2ClientPacket
{
	private static final String _C__AF_REQUESTRECIPESHOPMAKEITEM = "[C] af RequestRecipeShopMakeItem";

	private int _id;
	private int _recipeId;

	@Override
	protected void readImpl()
	{
		_id = readD();
		_recipeId = readD();
		/* _unknown = */ readCompQ();
	}

	@Override
	protected void runImpl()
	{
		final L2Player activeChar = getClient().getActiveChar();
		if (activeChar == null)
			return;

		L2Object object = null;

		// сначала пробуем текущую цель
		if (activeChar.getTargetId() == _id)
			object = activeChar.getTarget();

		// затем поиск в мире (сначала игрок)
		if (object == null)
		{
			object = L2World.getInstance().getPlayer(_id);
			if (object == null)
				object = L2World.getInstance().findObject(_id);
		}

		if (object == null)
		{
			requestFailed(SystemMessageId.TARGET_IS_INCORRECT);
			return;
		}

		// ===== игрок-производитель (стандартный приватный крафт) =====
		if (object instanceof L2Player)
		{
			final L2Player manufacturer = (L2Player)object;

			if (!activeChar.isSameInstance(manufacturer))
			{
				sendAF();
				return;
			}
			if (activeChar.getPrivateStoreType() != 0)
			{
				requestFailed(SystemMessageId.PRIVATE_STORE_UNDER_WAY);
				return;
			}
			if (manufacturer.getPrivateStoreType() != 5)
			{
				sendAF();
				return;
			}
			if (activeChar.isInCraftMode() || manufacturer.isInCraftMode())
			{
				sendAF();
				return;
			}
			if (manufacturer.isInDuel() || activeChar.isInDuel())
			{
				requestFailed(SystemMessageId.CANT_CRAFT_DURING_COMBAT);
				return;
			}

			if (Util.checkIfInRange(150, activeChar, manufacturer, true))
				RecipeTable.getInstance().requestManufactureItem(manufacturer, _recipeId, activeChar);
			else
				sendPacket(SystemMessageId.TARGET_TOO_FAR);

			sendAF();
			return;
		}

		// ===== НПЦ-производитель (наш Crafter of Mammon) =====
if (object instanceof L2Npc)
{
	final L2Npc npc = (L2Npc)object;
	if (npc.getNpcId() != 999) {
		requestFailed(SystemMessageId.TARGET_IS_INCORRECT);
		return;
	}
	if (!activeChar.isSameInstance(npc)) {
		sendAF();
		return;
	}
	if (!Util.checkIfInRange(150, activeChar, npc, true)) {
		sendPacket(SystemMessageId.TARGET_TOO_FAR);
		sendAF();
		return;
	}

	RecipeTable.getInstance().requestManufactureItemNpc(npc, _recipeId, activeChar);
	sendNpcRecipeInfo(activeChar, npc, _recipeId);
	sendAF();
	return;
}

		requestFailed(SystemMessageId.TARGET_IS_INCORRECT);
	}

	@Override
	public String getType()
	{
		return _C__AF_REQUESTRECIPESHOPMAKEITEM;
	}
	private void sendNpcRecipeInfo(L2Player player, L2Npc npc, int recipeId)
{
    int price = 0;
    try (Connection con = L2DatabaseFactory.getInstance().getConnection();
         PreparedStatement ps = con.prepareStatement(
             "SELECT fee_amount FROM npc_crafter_recipes " +
             "WHERE npc_id=? AND recipe_id=? AND is_learned=1 AND enabled=1"))
    {
        ps.setInt(1, npc.getNpcId());
        ps.setInt(2, recipeId);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) price = (int)rs.getLong(1);
        }
    } catch (Exception ignored) {}

    // перерисовать именно текущую карточку рецепта (без открытия нового окна)
    player.sendPacket(new RecipeShopItemInfo(npc.getObjectId(), recipeId, 0, 0, price));
}
}
