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
public class Kiyachi extends L2DefaultTransformation
{
	public Kiyachi()
	{
		// id, colRadius, colHeight
		super(310, 12.0, 29.0);
	}

	@Override
	public void transformedSkills(L2PcInstance player)
	{
		addSkill(player, 733, 1); // Kechi Double Cutter
		addSkill(player, 734, 1); // Kechi Air Blade
	}

	@Override
	public void removeSkills(L2PcInstance player)
	{
		removeSkill(player, 733); // Kechi Double Cutter
		removeSkill(player, 734); // Kechi Air Blade
	}

	public static void main(String[] args)
	{
		TransformationManager.getInstance().registerTransformation(new Kiyachi());
	}
}