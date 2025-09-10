package com.l2jfree.gameserver.model.zone;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import javolution.util.FastMap;

import com.l2jfree.Config;
import com.l2jfree.gameserver.gameobjects.L2Creature;
import com.l2jfree.gameserver.gameobjects.L2Player;
import com.l2jfree.gameserver.instancemanager.ClanHallManager;
import com.l2jfree.gameserver.instancemanager.TownManager;
import com.l2jfree.gameserver.model.entity.ClanHall;
import com.l2jfree.gameserver.network.packets.server.AgitDecoInfo;

public class L2TownZone extends L2Zone
{
	@Override
	protected void register()
	{
		TownManager.getInstance().registerTown(this);
	}

	private final Map<Integer, Byte> _map = new FastMap<Integer, Byte>().setShared(true);

	private static final int PEACE_SPEED_BONUS = 200;
	private static final Set<Integer> SPEED_ON = new ConcurrentSkipListSet<Integer>();

	@Override
	protected void onEnter(L2Creature character)
	{
		byte flag = FLAG_PEACE;

		switch (Config.ZONE_TOWN)
		{
			case 1:
			{
				if (character instanceof L2Player && ((L2Player)character).getSiegeState() != 0)
					flag = FLAG_PVP;
				break;
			}
			case 2:
			{
				flag = FLAG_PVP;
				break;
			}
		}

		_map.put(character.getObjectId(), flag);
		character.setInsideZone(flag, true);
		character.setInsideZone(FLAG_TOWN, true);

		super.onEnter(character);

		if (character instanceof L2Player)
		{
			ClanHall[] townHalls = ClanHallManager.getInstance().getTownClanHalls(getTownId());
			if (townHalls != null)
				for (ClanHall ch : townHalls)
					if (ch.getOwnerId() > 0)
						character.getActingPlayer().sendPacket(new AgitDecoInfo(ch));
		}

		if (character instanceof L2Player)
		{
			L2Player player = (L2Player)character;
			if (SPEED_ON.add(player.getObjectId()))
			{
				player.applyPeaceSpeedBonus(PEACE_SPEED_BONUS);
				player.sendMessage("Peace zone speed applied.");
			}
		}
	}

	@Override
	protected void onExit(L2Creature character)
	{
		Byte flag = _map.remove(character.getObjectId());
		if (flag != null)
			character.setInsideZone(flag.byteValue(), false);

		character.setInsideZone(FLAG_TOWN, false);

		if (character instanceof L2Player)
		{
			L2Player player = (L2Player)character;
			if (SPEED_ON.remove(player.getObjectId()))
			{
				player.removePeaceSpeedBonus();
				player.sendMessage("Peace zone speed removed.");
			}
		}

		super.onExit(character);
	}
}
