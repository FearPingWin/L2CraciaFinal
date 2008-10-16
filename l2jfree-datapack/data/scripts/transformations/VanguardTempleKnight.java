package transformations;

import com.l2jfree.gameserver.instancemanager.TransformationManager;
import com.l2jfree.gameserver.model.L2Skill;
import com.l2jfree.gameserver.model.L2Transformation;
import com.l2jfree.gameserver.model.actor.instance.L2PcInstance;

public class VanguardTempleKnight extends L2Transformation
{
	public VanguardTempleKnight()
	{
		// id, colRadius, colHeight
		super(314, 7.0, 24.0);
	}

	public void onTransform(L2PcInstance player)
	{
		// Disable all character skills.
		for (L2Skill sk : player.getAllSkills())
		{
			if (sk != null && !sk.isPassive())
			{
				switch (sk.getId())
				{
					// Aggression
					case 28:
						// Aura of Hate
					case 18:
						// Summon Storm Cubic
					case 10:
						// Summon Life Cubic
					case 67:
						// Summon Attractive Cubic
					case 449:
						// 	Tribunal
					case 400:
						// Holy Armor
					case 197:
					{
						// Those Skills wont be removed.
						break;
					}
					default:
					{
						player.removeSkill(sk, false);
						break;
					}
				}
			}

		}
		if (player.transformId() > 0 && !player.isCursedWeaponEquipped())
		{
			// give transformation skills
			transformedSkills(player);
			return;
		}
	}

	public void transformedSkills(L2PcInstance player)
	{
		if (player.getLevel() >= 43)
		{
			int level = player.getLevel() - 42;
			// Power Divide
			addSkill(player, 816, level);
			// Full Swing
			addSkill(player, 814, level);
			// Switch Stance
			addSkill(player, 838, 1);
			// Send a Server->Client packet StatusUpdate to the L2PcInstance.
			player.sendSkillList();
		}
	}

	public void onUntransform(L2PcInstance player)
	{
		// remove transformation skills
		removeSkills(player);
	}

	public void removeSkills(L2PcInstance player)
	{
		// Power Divide
		removeSkill(player, 816);
		// Full Swing
		removeSkill(player, 814);
		// Switch Stance
		removeSkill(player, 838);
		// Send a Server->Client packet StatusUpdate to the L2PcInstance.
		player.sendSkillList();
	}

	public static void main(String[] args)
	{
		TransformationManager.getInstance().registerTransformation(new VanguardTempleKnight());
	}
}