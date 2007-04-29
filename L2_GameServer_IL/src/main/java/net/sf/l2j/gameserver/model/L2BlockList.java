/*
 * $Header: BlockList.java, 21/11/2005 14:53:53 luisantonioa Exp $
 *
 * $Author: luisantonioa $
 * $Date: 21/11/2005 14:53:53 $
 * $Revision: 1 $
 * $Log: BlockList.java,v $
 * Revision 1  21/11/2005 14:53:53  luisantonioa
 * Added copyright notice
 *
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package net.sf.l2j.gameserver.model;

import java.util.Set;

import javolution.util.FastSet;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;

/**
 * This class ...
 * 
 * @version $Revision: 1.2 $ $Date: 2004/06/27 08:12:59 $
 */

public class L2BlockList
{
    private final Set<String> blockSet;
    private boolean blockAll;
    
    public L2BlockList()
    {
        blockSet    = new FastSet<String>();
        blockAll    = false;
    }

    private void addToBlockList(L2PcInstance character)
    {
        if(character != null)
        {
            blockSet.add(new String(character.getName()));
        }
    }
   
    private void removeFromBlockList(String character)
    {
    	blockSet.remove(character);
    }
    
    private void removeFromBlockList(L2PcInstance character)
    {
        if(character != null)
        {
            blockSet.remove(character.getName());
        }
    }
    
    private boolean isInBlockList(String character)
    {
        return blockSet.contains(character);        
    }
   
    public boolean isInBlockList(L2PcInstance character)
    {
        return isInBlockList(character.getName());        
    }
    
    private boolean isBlockAll()
    {
        return blockAll;
    }
    
    public static boolean isBlocked(L2PcInstance listOwner, L2PcInstance character)
    {
        L2BlockList blockList = listOwner.getBlockList();
        return blockList.isBlockAll() || blockList.isInBlockList(character);
    }
    
    private void setBlockAll(boolean state)
    {
        blockAll = state;
    }
    
    private Set<String> getBlockList()
    {
        return blockSet;
    }
    
    private String[] getBlockNames()
    {
    	return blockSet.toArray(new String[blockSet.size()]);
    }
    
    public static void addToBlockList(L2PcInstance listOwner, L2PcInstance character)
    {
        listOwner.getBlockList().addToBlockList(character);
    }
    
    public static void removeFromBlockList(L2PcInstance listOwner, String character)
    {
        listOwner.getBlockList().removeFromBlockList(character);
    }
    
    public static void removeFromBlockList(L2PcInstance listOwner, L2PcInstance character)
    {
        listOwner.getBlockList().removeFromBlockList(character);
    }
    
    public static boolean isInBlockList(L2PcInstance listOwner, L2PcInstance character)
    {
        return listOwner.getBlockList().isInBlockList(character);
    }
    
    public static boolean isInBlockList(L2PcInstance listOwner, String character)
    {
        return listOwner.getBlockList().isInBlockList(character);
    }
    
    public static boolean isBlockAll(L2PcInstance listOwner)
    {
        return listOwner.getBlockList().isBlockAll();
    }
    
    public static void setBlockAll(L2PcInstance listOwner, boolean newValue)
    {
        listOwner.getBlockList().setBlockAll(newValue);
    }
    
    public static String[] getBlockNames(L2PcInstance listOwner)
    {
        return listOwner.getBlockList().getBlockNames();
    }
    
    public static Set<String> getBlockList(L2PcInstance listOwner)
    {
        return listOwner.getBlockList().getBlockList();
    }
}
