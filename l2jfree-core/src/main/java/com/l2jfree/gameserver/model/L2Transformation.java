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


/**
 *
 * @author  KenM
 */
public abstract class L2Transformation
{
    private final int _id;
    private final int _graphicalId;
    private final double _collisionRadius;
    private final double _collisionHeight;

    public static final int TRANSFORM_ZARICHE = 301;
    public static final int TRANSFORM_AKAMANAH = 302;

    private L2PcInstance _player;

    /**
     * 
     * @param id Internal id that server will use to associate this transformation 
     * @param graphicalId Client visible transformation id
     * @param duration Transformation duration in seconds
     * @param collisionRadius Collision Radius of the player while transformed
     * @param collisionHeight  Collision Height of the player while transformed
     */
    public L2Transformation(int id, int graphicalId, double collisionRadius, double collisionHeight)
    {
        _id = id;
        _graphicalId = graphicalId;
        _collisionRadius = collisionRadius;
        _collisionHeight = collisionHeight;
    }
    
    /**
     * 
     * @param id Internal id(will be used also as client graphical id) that server will use to associate this transformation 
     * @param duration Transformation duration in seconds
     * @param collisionRadius Collision Radius of the player while transformed
     * @param collisionHeight  Collision Height of the player while transformed
     */
    public L2Transformation(int id, double collisionRadius, double collisionHeight)
    {
        this(id, id, collisionRadius, collisionHeight);
    }
    
    /**
     * @return Returns the id.
     */
    public int getId()
    {
        return _id;
    }

    /**
     * @return Returns the graphicalId.
     */
    public int getGraphicalId()
    {
        return _graphicalId;
    }

    /**
     * @return Returns the collisionRadius.
     */
    public double getCollisionRadius()
    {
        return _collisionRadius;
    }

    /**
     * @return Returns the collisionHeight.
     */
    public double getCollisionHeight()
    {
        return _collisionHeight;
    }

    // Scriptable Events
    public abstract void onTransform();
    
    public abstract void onUntransform();
}
