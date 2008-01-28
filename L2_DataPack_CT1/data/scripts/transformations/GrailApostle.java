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
 * The only thing that is missing now is completion of the skill effects and the stat changes of the transformation.<br>
 * - Ahmed
 * 
 * @author durgus
 *
 */
public class GrailApostle extends L2Transformation
{
	public GrailApostle()
	{
		// id, duration (secs), colRadius, colHeight
		super(201, 3600, 8.0, 35.0);
	}

	public void onTransform()
	{
		// Disable all character skills.
		for (L2Skill sk : this.getPlayer().getAllSkills())
		{
			if (sk != null)
				this.getPlayer().removeSkill(sk, false);
		}
		if (this.getPlayer().transformId() > 0 && !this.getPlayer().isCursedWeaponEquipped())
		{
			// give transformation skills
			transformedSkills();
			// Message sent to player after transforming.
			this.getPlayer().sendMessage("Grail Apostle transformation complete.");
			return;
		}
		// give transformation skills
		transformedSkills();
		// Update Transformation ID
		this.getPlayer().transformInsertInfo();
		// Message sent to player after transforming.
		this.getPlayer().sendMessage("Grail Apostle transformation complete.");
	}

	public void transformedSkills()
	{
		// Spear
		this.getPlayer().addSkill(SkillTable.getInstance().getInfo(559, 1), false);
		// Power Slash
		this.getPlayer().addSkill(SkillTable.getInstance().getInfo(560, 1), false);
		// Bless of Angel
		this.getPlayer().addSkill(SkillTable.getInstance().getInfo(561, 1), false);
		// Wind of Angel
		this.getPlayer().addSkill(SkillTable.getInstance().getInfo(562, 1), false);
		// Transfrom Dispel
		this.getPlayer().addSkill(SkillTable.getInstance().getInfo(619, 1), false);
		// Send a Server->Client packet StatusUpdate to the L2PcInstance.
		this.getPlayer().sendSkillList();
	}

	public void onUntransform()
	{
		// Enable all character skills
		for (L2Skill sk : this.getPlayer().getAllSkills())
		{
			if (sk != null)
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
		// Message sent to player after transforming.
		this.getPlayer().sendMessage("Grail Apostle has been dispelled.");
	}

	public void removeSkills()
	{
		// Spear
		this.getPlayer().removeSkill(SkillTable.getInstance().getInfo(559, 1), false);
		// Power Slash
		this.getPlayer().removeSkill(SkillTable.getInstance().getInfo(560, 1), false);
		// Bless of Angel
		this.getPlayer().removeSkill(SkillTable.getInstance().getInfo(561, 1), false);
		// Wind of Angel
		this.getPlayer().removeSkill(SkillTable.getInstance().getInfo(562, 1), false);
		// Transfrom Dispel
		this.getPlayer().removeSkill(SkillTable.getInstance().getInfo(619, 1), false);
		// Send a Server->Client packet StatusUpdate to the L2PcInstance.
		this.getPlayer().sendSkillList();
	}

	public static void main(String[] args)
	{
		TransformationManager.getInstance().registerTransformation(new GrailApostle());
	}
}