package net.sf.l2j.gameserver.model;

import java.lang.reflect.Constructor;

import net.sf.l2j.gameserver.Territory;
import net.sf.l2j.gameserver.idfactory.IdFactory;
import net.sf.l2j.gameserver.lib.Rnd;
import net.sf.l2j.gameserver.model.actor.instance.L2NpcInstance;
import net.sf.l2j.gameserver.templates.L2NpcTemplate;

/**
 * @author littlecrow
 * A special spawn implementation to spawn controllable mob
 */
public class L2GroupSpawn extends L2Spawn 
{
	private Constructor _constructor;
	private L2NpcTemplate _template;
	
	public L2GroupSpawn(L2NpcTemplate mobTemplate) throws SecurityException, ClassNotFoundException 
    {
		super(mobTemplate);
		_constructor = Class.forName("net.sf.l2j.gameserver.model.actor.instance.L2ControllableMobInstance").getConstructors()[0];
		_template = mobTemplate;
        
		setAmount(1);
	}

	public L2NpcInstance doGroupSpawn() 
    {
		L2NpcInstance mob = null;		
		
		try
		{
            if (_template.type.equalsIgnoreCase("L2Pet") || 
                    _template.type.equalsIgnoreCase("L2Minion"))
                return null;
            
			Object[] parameters = {IdFactory.getInstance().getNextId(), _template};
			Object  tmp = _constructor.newInstance(parameters);
            
			if (!(tmp instanceof L2NpcInstance))
				return null;
            
			mob = (L2NpcInstance)tmp; 

			int newlocx, newlocy, newlocz;

			if  (getLocx() == 0 && getLocy() == 0)
            {
			    if (getLocation() == 0) 
                    return null;

                int p[] = Territory.getInstance().getRandomPoint(getLocation());
				newlocx = p[0];
				newlocy = p[1];
				newlocz = p[2];
			} 
			else 
            {
				newlocx = getLocx();
				newlocy = getLocy();
				newlocz = getLocz();
			}
			
			mob.getStatus().setCurrentHpMp(mob.getStat().getMaxHp(), mob.getStat().getMaxMp());
			
			if (getHeading() == -1)
				mob.setHeading(Rnd.nextInt(61794));	
			else 
				mob.setHeading(getHeading());
			
			mob.setSpawn(this);
			mob.spawnMe(newlocx, newlocy, newlocz);
			mob.OnSpawn();

			if (_log.isDebugEnabled()) 
				_log.debug("spawned Mob ID: "+_template.npcId+" ,at: "
						+mob.getX()+" x, "+mob.getY()+" y, "+mob.getZ()+" z");
            
			return mob;
		
		}
		catch (Exception e)
		{
			_log.warn("NPC class not found: " + e);
			return null;
		}		
	}
}