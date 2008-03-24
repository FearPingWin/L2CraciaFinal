package transformations;

import net.sf.l2j.gameserver.datatables.SkillTable;
import net.sf.l2j.gameserver.instancemanager.TransformationManager;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.L2Transformation;

/**
 * Description: <br>
 * This will handle the transformation, giving the skills, and removing them, when the player logs out and is transformed these skills
 * do not save. 
 * When the player logs back in, there will be a call from the enterworld packet that will add all their skills.
 * The enterworld packet will transform a player.
 * 
 * @author durgus
 *
 */
public class GolemGuardianNormal extends L2Transformation
{
	public GolemGuardianNormal()
	{
		// id, duration (secs), colRadius, colHeight
		// Retail Like 30 min - Skatershi
		super(211, 1800, 8.0, 22.0);
	}

	public void onTransform()
	{
		// Disable all character skills.
		for (L2Skill sk : this.getPlayer().getAllSkills())
		{
			if (sk != null && !sk.isPassive())
				this.getPlayer().removeSkill(sk, false);
		}
		if (this.getPlayer().transformId() > 0 && !this.getPlayer().isCursedWeaponEquipped())
		{
			// give transformation skills
			transformedSkills();
			return;
		}
		// give transformation skills
		transformedSkills();
		// Update Transformation ID
		this.getPlayer().transformInsertInfo();
	}

	public void transformedSkills()
	{
		// Double Slasher
		this.getPlayer().addSkill(SkillTable.getInstance().getInfo(572, 3), false);
		// Earthquake
		this.getPlayer().addSkill(SkillTable.getInstance().getInfo(573, 3), false);
		// Bomb Installation
		this.getPlayer().addSkill(SkillTable.getInstance().getInfo(574, 3), false);
		// Steel Cutter
		this.getPlayer().addSkill(SkillTable.getInstance().getInfo(575, 3), false);
		// Transfrom Dispel
		this.getPlayer().addSkill(SkillTable.getInstance().getInfo(619, 1), false);
		// Decrease Bow/Crossbow Attack Speed
		this.getPlayer().addSkill(SkillTable.getInstance().getInfo(5491, 1), false);
		// Send a Server->Client packet StatusUpdate to the L2PcInstance.
		this.getPlayer().sendSkillList();
	}

	public void onUntransform()
	{
		// Enable all character skills
		for (L2Skill sk : this.getPlayer().getAllSkills())
		{
			if (sk != null && !sk.isPassive())
				this.getPlayer().addSkill(sk, false);
		}
		// Only remove transformation skills. Keeps transformation id for restoration after CW is no longer equipped.
		if (this.getPlayer().isCursedWeaponEquipped())
		{
			removeSkills();
			return;
		}
		// Remove transformation skills
		removeSkills();
		// Update Transformation ID
		this.getPlayer().transformUpdateInfo();
	}

	public void removeSkills()
	{
		// Double Slasher
		this.getPlayer().removeSkill(SkillTable.getInstance().getInfo(572, 3), false);
		// Earthquake
		this.getPlayer().removeSkill(SkillTable.getInstance().getInfo(573, 3), false);
		// Bomb Installation
		this.getPlayer().removeSkill(SkillTable.getInstance().getInfo(574, 3), false);
		// Steel Cutter
		this.getPlayer().removeSkill(SkillTable.getInstance().getInfo(575, 3), false);
		// Transfrom Dispel
		this.getPlayer().removeSkill(SkillTable.getInstance().getInfo(619, 1), false);
		// Decrease Bow/Crossbow Attack Speed
		this.getPlayer().removeSkill(SkillTable.getInstance().getInfo(5491, 1), false);
		// Send a Server->Client packet StatusUpdate to the L2PcInstance.
		this.getPlayer().sendSkillList();
	}

	public static void main(String[] args)
	{
		TransformationManager.getInstance().registerTransformation(new GolemGuardianNormal());
	}
}
