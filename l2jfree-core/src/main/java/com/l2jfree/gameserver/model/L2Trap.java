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
package com.l2jfree.gameserver.model;

import com.l2jfree.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfree.gameserver.network.serverpackets.MyTargetSelected;
import com.l2jfree.gameserver.taskmanager.DecayTaskManager;
import com.l2jfree.gameserver.templates.L2CharTemplate;
import com.l2jfree.gameserver.templates.L2NpcTemplate;
import com.l2jfree.gameserver.templates.L2Weapon;

/**
 *
 * @author nBd
 */
public class L2Trap extends L2Character
{
	private L2PcInstance _owner;
	/**
	 * @param objectId
	 * @param template
	 */
	public L2Trap(int objectId, L2CharTemplate template, L2PcInstance owner)
	{
		super(objectId, template);
		getKnownList();
		getStat();
		getStatus();
		_owner = owner;
		getPosition().setXYZInvisible(owner.getX(), owner.getY(), owner.getZ());
	}

	/**
	 *
	 * @see com.l2jfree.gameserver.model.L2Character#onSpawn()
	 */
	@Override
    public void onSpawn()
    {
        super.onSpawn();
    }

	/**
	 *
	 * @see com.l2jfree.gameserver.model.L2Object#onAction(com.l2jfree.gameserver.model.actor.instance.L2PcInstance)
	 */
	@Override
    public void onAction(L2PcInstance player)
    {
        player.setTarget(this);
        MyTargetSelected my = new MyTargetSelected(getObjectId(), player.getLevel() - getLevel());
        player.sendPacket(my);
    }

	/**
	 *
	 *
	 */
	public void stopDecay()
    {
        DecayTaskManager.getInstance().cancelDecayTask(this);
    }

	/**
	 *
	 * @see com.l2jfree.gameserver.model.L2Character#onDecay()
	 */
	@Override
    public void onDecay()
    {
        deleteMe(_owner);
    }

	/**
	 *
	 * @return
	 */
	public final int getNpcId()
    {
        return getTemplate().getNpcId();
    }

	/**
	 *
	 * @see com.l2jfree.gameserver.model.L2Object#isAutoAttackable(com.l2jfree.gameserver.model.L2Character)
	 */
	@Override
    public boolean isAutoAttackable(L2Character attacker)
    {
        return _owner.isAutoAttackable(attacker);
    }

	/**
	 *
	 * @param owner
	 */
    public void deleteMe(L2PcInstance owner)
    {
        decayMe();
        getKnownList().removeAllKnownObjects();
        owner.setTrap(null);
    }

    /**
     *
     * @param owner
     */
    public synchronized void unSummon(L2PcInstance owner)
    {
        if (isVisible() && !isDead())
        {
            if (getWorldRegion() != null)
                getWorldRegion().removeFromZones(this);
            owner.setTrap(null);
            decayMe();
            getKnownList().removeAllKnownObjects();
        }
    }

    /**
     *
     * @see com.l2jfree.gameserver.model.L2Character#getActiveWeaponInstance()
     */
	@Override
	public L2ItemInstance getActiveWeaponInstance()
	{
		return null;
	}

	/**
	 *
	 * @see com.l2jfree.gameserver.model.L2Character#getActiveWeaponItem()
	 */
	@Override
	public L2Weapon getActiveWeaponItem()
	{
		return null;
	}

	/**
	 *
	 * @see com.l2jfree.gameserver.model.L2Character#getLevel()
	 */
	@Override
	public int getLevel()
	{
		return getTemplate().getLevel();
	}

	/**
	 *
	 * @return
	 */
	public final L2PcInstance getOwner()
    {
        return _owner;
    }

	/**
	 *
	 * @see com.l2jfree.gameserver.model.L2Character#getTemplate()
	 */
    @Override
    public L2NpcTemplate getTemplate()
    {
        return (L2NpcTemplate) super.getTemplate();
    }

    /**
     *
     * @see com.l2jfree.gameserver.model.L2Character#getSecondaryWeaponInstance()
     */
	@Override
	public L2ItemInstance getSecondaryWeaponInstance()
	{
		return null;
	}

	/**
	 *
	 * @see com.l2jfree.gameserver.model.L2Character#getSecondaryWeaponItem()
	 */
	@Override
	public L2Weapon getSecondaryWeaponItem()
	{
		return null;
	}

	/**
	 *
	 * @see com.l2jfree.gameserver.model.L2Character#updateAbnormalEffect()
	 */
	@Override
	public void updateAbnormalEffect()
	{

	}

	/**
	 *
	 * @return
	 */
	public boolean isDetected()
	{
		return false;
	}

	/**
	 *
	 *
	 */
	public void setDetected()
	{
		// Do nothing
	}
}
