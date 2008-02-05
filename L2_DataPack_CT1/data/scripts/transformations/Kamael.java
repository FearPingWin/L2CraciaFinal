package transformations;

import net.sf.l2j.gameserver.datatables.SkillTable;
import net.sf.l2j.gameserver.instancemanager.TransformationManager;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.L2Transformation;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;

/**
 * Description: <br>
 * This will handle the transformation, giving the skills, and removing them, when the player logs out and is transformed these skills
 * do not save. 
 * When the player logs back in, there will be a call from the enterworld packet that will add all their skills.
 * The enterworld packet will transform a player. 
 * The only thing that is missing now is completion of the skill effects and the stat changes of the transformation.<br>
 * - Ahmed
 * 
 * @author KenM
 *
 */
public class Kamael extends L2Transformation
{
	public Kamael()
	{
		// id, duration (secs), colRadius, colHeight
		// Retail Like 4 min - Skatershi
		super(251, 240, 9.0, 32.5);
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
            // Message sent to player after transforming.
            SystemMessage msg = new SystemMessage(SystemMessageId.YOU_FEEL_S1_EFFECT);
            this.getPlayer().sendPacket(msg);
            return;
        }
        // give transformation skills
        transformedSkills();
        // Update Transformation ID
        this.getPlayer().transformInsertInfo();
        // Message sent to player after transforming.
        SystemMessage msg = new SystemMessage(SystemMessageId.YOU_FEEL_S1_EFFECT);
        this.getPlayer().sendPacket(msg);
    }

    public void transformedSkills()
    {
        // Nail Attack
        this.getPlayer().addSkill(SkillTable.getInstance().getInfo(539, 1), false);
        // Wing Assault
        this.getPlayer().addSkill(SkillTable.getInstance().getInfo(540, 1), false);
        // Soul Sucking
        this.getPlayer().addSkill(SkillTable.getInstance().getInfo(1471, 1), false);
        // Death Beam
        this.getPlayer().addSkill(SkillTable.getInstance().getInfo(1472, 1), false);
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
        // Message sent to player when transform has worn off.
        SystemMessage msg = new SystemMessage(SystemMessageId.S1_HAS_WORN_OFF);
        this.getPlayer().sendPacket(msg);
    }

    public void removeSkills()
    {
        // Nail Attack
        this.getPlayer().removeSkill(SkillTable.getInstance().getInfo(539, 1), false);
        // Wing Assault
        this.getPlayer().removeSkill(SkillTable.getInstance().getInfo(540, 1), false);
        // Soul Sucking
        this.getPlayer().removeSkill(SkillTable.getInstance().getInfo(1471, 1), false);
        // Death Beam
        this.getPlayer().removeSkill(SkillTable.getInstance().getInfo(1472, 1), false);
        // Transfrom Dispel
        this.getPlayer().removeSkill(SkillTable.getInstance().getInfo(619, 1), false);
        // Decrease Bow/Crossbow Attack Speed
        this.getPlayer().removeSkill(SkillTable.getInstance().getInfo(5491, 1), false);
        // Send a Server->Client packet StatusUpdate to the L2PcInstance.
        this.getPlayer().sendSkillList();
    }

    public static void main(String[] args)
    {
        TransformationManager.getInstance().registerTransformation(new Kamael());
    }
}
