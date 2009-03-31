package transformations;

import com.l2jfree.gameserver.instancemanager.TransformationManager;
import com.l2jfree.gameserver.model.L2DefaultTransformation;
import com.l2jfree.gameserver.model.actor.instance.L2PcInstance;

/**
 * Description: <br>
 * This will handle the transformation, giving the skills, and removing them, when the player logs out and is transformed these skills
 * do not save.
 * When the player logs back in, there will be a call from the enterworld packet that will add all their skills.
 * The enterworld packet will transform a player.
 *
 * @author Ahmed
 *
 */
public class DivineKnight extends L2DefaultTransformation
{
	public DivineKnight()
	{
		// id, colRadius, colHeight
		super(252, 12.0, 30.0);
	}
	
	public void transformedSkills(L2PcInstance player)
	{
		/* Commented till we get proper values for these skills
		addSkill(player, 680, 1); // Divine Knight Hate
		addSkill(player, 681, 1); // Divine Knight Hate Aura
		addSkill(player, 682, 1); // Divine Knight Stun Attack
		addSkill(player, 683, 1); // Divine Knight Thunder Storm
		addSkill(player, 684, 1); // Divine Knight Ultimate Defense
		addSkill(player, 685, 1); // Sacrifice Knight
		addSkill(player, 795, 1); // Divine Knight Brandish
		addSkill(player, 796, 1); // Divine Knight Explosion
		*/
	}

	public void removeSkills(L2PcInstance player)
	{
		/* Commented till we get proper values for these skills
		removeSkill(player, 680); // Divine Knight Hate
		removeSkill(player, 681); // Divine Knight Hate Aura
		removeSkill(player, 682); // Divine Knight Stun Attack
		removeSkill(player, 683); // Divine Knight Thunder Storm
		removeSkill(player, 684); // Divine Knight Ultimate Defense
		removeSkill(player, 685); // Sacrifice Knight
		removeSkill(player, 795); // Divine Knight Brandish
		removeSkill(player, 796); // Divine Knight Explosion
		*/
	}

	public static void main(String[] args)
	{
		TransformationManager.getInstance().registerTransformation(new DivineKnight());
	}
}