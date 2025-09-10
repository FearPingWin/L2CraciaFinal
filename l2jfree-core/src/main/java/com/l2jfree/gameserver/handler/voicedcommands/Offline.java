package com.l2jfree.gameserver.handler.voicedcommands;

import com.l2jfree.Config;
import com.l2jfree.gameserver.gameobjects.L2Player;
import com.l2jfree.gameserver.handler.IVoicedCommandHandler;
import com.l2jfree.gameserver.model.zone.L2Zone;
import com.l2jfree.gameserver.network.SystemMessageId;

public class Offline implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS = { "offline" };
	
	@Override
	public boolean useVoicedCommand(String command, L2Player activeChar, String target)
	{
		if (!Config.ALLOW_OFFLINE_TRADE)
		{
			activeChar.sendMessage("Offline trade is disabled!");
			return true;
		}
		
		switch (activeChar.getPrivateStoreType())
		{
			case L2Player.STORE_PRIVATE_MANUFACTURE:
			{
				if (!Config.ALLOW_OFFLINE_TRADE_CRAFT)
				{
					activeChar.sendMessage("Offline craft is disabled!");
					return true;
				}
			}
			//$FALL-THROUGH$
			case L2Player.STORE_PRIVATE_SELL:
			case L2Player.STORE_PRIVATE_BUY:
			case L2Player.STORE_PRIVATE_PACKAGE_SELL:
			{
				if (activeChar.isInsideZone(L2Zone.FLAG_PEACE) || activeChar.isGM())
				{
					if (Config.OFFLINE_TRADE_PRICE > 0)
					{
						if (activeChar.getInventory().destroyItemByItemId("offlinetrade",
								Config.OFFLINE_TRADE_PRICE_ITEM, Config.OFFLINE_TRADE_PRICE, null, null) != null)
						{
							activeChar.enterOfflineMode();
						}
						else
						{
							activeChar.sendPacket(SystemMessageId.NOT_ENOUGH_ITEMS);
						}
					}
					else
					{
						activeChar.enterOfflineMode();
					}
					return true;
				}
				else
				{
					activeChar.sendMessage("You must be in a peace zone to use offline mode!");
					return true;
				}
			}
		}
		
		activeChar.sendMessage("You must be in trade mode to use offline mode!");
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}
