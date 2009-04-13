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
package com.l2jfree.gameserver.taskmanager;

import com.l2jfree.gameserver.model.L2Character;
import com.l2jfree.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfree.gameserver.model.actor.instance.L2PlayableInstance;

/**
 * @author NB4L1
 */
public final class PacketBroadcaster extends AbstractFIFOPeriodicTaskManager<L2Character>
{
	public static enum BroadcastMode
	{
		BROADCAST_FULL_INFO {
			@Override
			protected void sendPacket(L2Character cha)
			{
				cha.broadcastFullInfoImpl();
			}
		},
		UPDATE_EFFECT_ICONS {
			@Override
			protected void sendPacket(L2Character cha)
			{
				((L2PlayableInstance)cha).updateEffectIconsImpl();
			}
		},
		BROADCAST_STATUS_UPDATE {
			@Override
			protected void sendPacket(L2Character cha)
			{
				cha.broadcastStatusUpdateImpl();
			}
		},
		BROADCAST_RELATION_CHANGED {
			@Override
			protected void sendPacket(L2Character cha)
			{
				((L2PcInstance)cha).broadcastRelationChangedImpl();
			}
		},
		SEND_ETC_STATUS_UPDATE {
			@Override
			protected void sendPacket(L2Character cha)
			{
				((L2PcInstance)cha).sendEtcStatusUpdateImpl();
			}
		},
		// TODO: more packets
		;
		
		private final byte _mask;
		
		private BroadcastMode()
		{
			_mask = (byte)(1 << ordinal());
		}
		
		public byte mask()
		{
			return _mask;
		}
		
		protected abstract void sendPacket(L2Character cha);
		
		protected final void trySendPacket(L2Character cha, byte mask)
		{
			if ((mask & mask()) == mask())
				sendPacket(cha);
		}
	}
	
	private static PacketBroadcaster _instance;
	
	public static PacketBroadcaster getInstance()
	{
		if (_instance == null)
			_instance = new PacketBroadcaster();
		
		return _instance;
	}
	
	private static final BroadcastMode[] VALUES = BroadcastMode.values();
	
	private PacketBroadcaster()
	{
		super(100);
	}
	
	@Override
	void callTask(L2Character cha)
	{
		for (byte mask; (mask = cha.clearPacketBroadcastMask()) != 0;)
			for (BroadcastMode mode : VALUES)
				mode.trySendPacket(cha, mask);
	}
}
