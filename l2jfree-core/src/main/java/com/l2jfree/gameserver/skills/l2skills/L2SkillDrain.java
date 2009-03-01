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
package com.l2jfree.gameserver.skills.l2skills;

import com.l2jfree.Config;
import com.l2jfree.gameserver.model.L2Character;
import com.l2jfree.gameserver.model.L2Effect;
import com.l2jfree.gameserver.model.L2ItemInstance;
import com.l2jfree.gameserver.model.L2Object;
import com.l2jfree.gameserver.model.L2Skill;
import com.l2jfree.gameserver.model.L2Summon;
import com.l2jfree.gameserver.model.actor.instance.L2CubicInstance;
import com.l2jfree.gameserver.model.actor.instance.L2NpcInstance;
import com.l2jfree.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfree.gameserver.network.SystemMessageId;
import com.l2jfree.gameserver.network.serverpackets.StatusUpdate;
import com.l2jfree.gameserver.network.serverpackets.SystemMessage;
import com.l2jfree.gameserver.skills.Formulas;
import com.l2jfree.gameserver.templates.StatsSet;

public class L2SkillDrain extends L2Skill
{
	private float	_absorbPart;
	private int		_absorbAbs;

	public L2SkillDrain(StatsSet set)
	{
		super(set);

		_absorbPart = set.getFloat("absorbPart", 0.f);
		_absorbAbs = set.getInteger("absorbAbs", 0);
	}

	@Override
	public void useSkill(L2Character activeChar, L2Character... targets)
	{
		if (activeChar.isAlikeDead())
			return;

		boolean ss = false;
		boolean bss = false;

		for (L2Character target: targets)
		{
			if (target.isAlikeDead() && getTargetType() != SkillTargetType.TARGET_CORPSE_MOB)
				continue;

			if (activeChar != target && (target.isInvul() || target.isPetrified()))
				continue; // No effect on invulnerable chars unless they cast it themselves.

			L2ItemInstance weaponInst = activeChar.getActiveWeaponInstance();

			if (weaponInst != null)
			{
				if (weaponInst.getChargedSpiritshot() == L2ItemInstance.CHARGED_BLESSED_SPIRITSHOT)
				{
					bss = true;
					weaponInst.setChargedSpiritshot(L2ItemInstance.CHARGED_NONE);
				}
				else if (weaponInst.getChargedSpiritshot() == L2ItemInstance.CHARGED_SPIRITSHOT)
				{
					ss = true;
					weaponInst.setChargedSpiritshot(L2ItemInstance.CHARGED_NONE);
				}
			}
			// If there is no weapon equipped, check for an active summon.
			else if (activeChar instanceof L2Summon)
			{
				L2Summon activeSummon = (L2Summon) activeChar;

				if (activeSummon.getChargedSpiritShot() == L2ItemInstance.CHARGED_BLESSED_SPIRITSHOT)
				{
					bss = true;
					activeSummon.setChargedSpiritShot(L2ItemInstance.CHARGED_NONE);
				}
				else if (activeSummon.getChargedSpiritShot() == L2ItemInstance.CHARGED_SPIRITSHOT)
				{
					ss = true;
					activeSummon.setChargedSpiritShot(L2ItemInstance.CHARGED_NONE);
				}
			}
			else if (activeChar instanceof L2NpcInstance)
			{
				bss = ((L2NpcInstance) activeChar).isUsingShot(false);
				ss = ((L2NpcInstance) activeChar).isUsingShot(true);
			}

			boolean mcrit = Formulas.getInstance().calcMCrit(activeChar.getMCriticalHit(target, this));
			byte shld = Formulas.getInstance().calcShldUse(activeChar, target);
			int damage = (int) Formulas.getInstance().calcMagicDam(activeChar, target, this, shld, ss, bss, mcrit);

			int _drain = 0;
			int _cp = (int) target.getStatus().getCurrentCp();
			int _hp = (int) target.getStatus().getCurrentHp();

			if (_cp > 0)
			{
				if (damage < _cp)
					_drain = 0;
				else
					_drain = damage - _cp;
			}
			else if (damage > _hp)
				_drain = _hp;
			else
				_drain = damage;

			double hpAdd = _absorbAbs + _absorbPart * _drain;
			double hp = ((activeChar.getStatus().getCurrentHp() + hpAdd) > activeChar.getMaxHp() ? activeChar.getMaxHp() : (activeChar.getStatus()
					.getCurrentHp() + hpAdd));

			double hpDiff = hp - activeChar.getStatus().getCurrentHp();

			activeChar.getStatus().increaseHp(hpDiff);

			StatusUpdate suhp = new StatusUpdate(activeChar.getObjectId());
			suhp.addAttribute(StatusUpdate.CUR_HP, (int) hp);
			activeChar.sendPacket(suhp);

			// Check to see if we should damage the target
			if (damage > 0 && (!target.isDead() || getTargetType() != SkillTargetType.TARGET_CORPSE_MOB))
			{
				if (activeChar instanceof L2PcInstance)
				{
					L2PcInstance activeCaster = (L2PcInstance) activeChar;

					if (activeCaster.isGM() && activeCaster.getAccessLevel() < Config.GM_CAN_GIVE_DAMAGE)
						damage = 0;
				}

				// Manage attack or cast break of the target (calculating rate, sending message...)
				if (Formulas.getInstance().calcAtkBreak(target, damage))
				{
					target.breakAttack();
					target.breakCast();
				}

				activeChar.sendDamageMessage(target, damage, mcrit, false, false);

				if (hasEffects() && getTargetType() != SkillTargetType.TARGET_CORPSE_MOB)
				{
					if (target.reflectSkill(this))
					{
						activeChar.stopSkillEffects(getId());
						getEffects(target, activeChar);
						SystemMessage sm = new SystemMessage(SystemMessageId.YOU_FEEL_S1_EFFECT);
						sm.addSkillName(this);
						activeChar.sendPacket(sm);
					}
					else
					{
						// activate attacked effects, if any
						target.stopSkillEffects(getId());
						if (Formulas.getInstance().calcSkillSuccess(activeChar, target, this, shld, false, ss, bss))
							getEffects(activeChar, target);
						else
						{
							SystemMessage sm = new SystemMessage(SystemMessageId.S1_WAS_UNAFFECTED_BY_S2);
							sm.addCharName(target);
							sm.addSkillName(this);
							activeChar.sendPacket(sm);
						}
					}
				}

				target.reduceCurrentHp(damage, activeChar, this);
			}
			// Check to see if we should do the decay right after the cast
			if (target.isDead() && getTargetType() == SkillTargetType.TARGET_CORPSE_MOB && target instanceof L2NpcInstance)
			{
				((L2NpcInstance) target).endDecayTask();
			}
		}
		//effect self :]
		L2Effect effect = activeChar.getFirstEffect(getId());
		if (effect != null && effect.isSelfEffect())
		{
			//Replace old effect with new one.
			effect.exit();
		}
		// cast self effect if any
		getEffectsSelf(activeChar);
	}

	public void useCubicSkill(L2CubicInstance activeCubic, L2Object[] targets)
	{
		if (_log.isDebugEnabled())
			_log.info("L2SkillDrain: useCubicSkill()");

		for (L2Object element :  targets)
		{
			if (element == null || 
					!(element instanceof L2Character))
				continue;
			
			L2Character target = (L2Character) element;
			
			if (target.isAlikeDead() && getTargetType() != SkillTargetType.TARGET_CORPSE_MOB)
				continue;

			boolean mcrit = Formulas.getInstance().calcMCrit(activeCubic.getMCriticalHit(target, this));
			byte shld = Formulas.getInstance().calcShldUse(activeCubic.getOwner(), target);
			int damage = (int) Formulas.getInstance().calcMagicDam(activeCubic, target, this, mcrit, shld);
			if (_log.isDebugEnabled())
				_log.info("L2SkillDrain: useCubicSkill() -> damage = " + damage);

			double hpAdd = _absorbAbs + _absorbPart * damage;
			L2PcInstance owner = activeCubic.getOwner();
			double hp = ((owner.getStatus().getCurrentHp() + hpAdd) > owner.getMaxHp() ? owner.getMaxHp() : (owner.getStatus().getCurrentHp() + hpAdd));

			owner.getStatus().setCurrentHp(hp);

			StatusUpdate suhp = new StatusUpdate(owner.getObjectId());
			suhp.addAttribute(StatusUpdate.CUR_HP, (int) hp);
			owner.sendPacket(suhp);

			// Check to see if we should damage the target
			if (damage > 0 && (!target.isDead() || getTargetType() != SkillTargetType.TARGET_CORPSE_MOB))
			{
				target.reduceCurrentHp(damage, activeCubic.getOwner(), this);

				// Manage attack or cast break of the target (calculating rate, sending message...)
				if (!target.isRaid() && Formulas.getInstance().calcAtkBreak(target, damage))
				{
					target.breakAttack();
					target.breakCast();
				}
				owner.sendDamageMessage(target, damage, mcrit, false, false);
			}
		}
	}
}