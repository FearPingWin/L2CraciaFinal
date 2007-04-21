/*
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
package net.sf.l2j.gameserver;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import javolution.util.FastList;
import net.sf.l2j.Config;
import net.sf.l2j.gameserver.datatables.ItemTable;
import net.sf.l2j.gameserver.lib.Rnd;
import net.sf.l2j.gameserver.model.GMAudit;
import net.sf.l2j.gameserver.model.Inventory;
import net.sf.l2j.gameserver.model.L2ItemInstance;
import net.sf.l2j.gameserver.model.L2ManufactureItem;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.recipes.model.L2Recipe;
import net.sf.l2j.gameserver.recipes.model.L2RecipeComponent;
import net.sf.l2j.gameserver.recipes.service.L2RecipeService;
import net.sf.l2j.gameserver.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.serverpackets.ItemList;
import net.sf.l2j.gameserver.serverpackets.MagicSkillUser;
import net.sf.l2j.gameserver.serverpackets.RecipeBookItemList;
import net.sf.l2j.gameserver.serverpackets.RecipeItemMakeInfo;
import net.sf.l2j.gameserver.serverpackets.RecipeShopItemInfo;
import net.sf.l2j.gameserver.serverpackets.SetupGauge;
import net.sf.l2j.gameserver.serverpackets.StatusUpdate;
import net.sf.l2j.gameserver.serverpackets.SystemMessage;
import net.sf.l2j.gameserver.skills.Stats;
import net.sf.l2j.tools.L2Registry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RecipeController
{
	protected static Log _log = LogFactory.getLog(RecipeController.class.getName());
	
    /**
     * Service for recipe list
     */    
    private L2RecipeService l2RecipeService=(L2RecipeService) L2Registry.getBean("L2RecipeService");    
    
	private static RecipeController _instance;
	protected static Map<L2PcInstance, RecipeItemMaker> activeMakers = Collections.synchronizedMap(new WeakHashMap<L2PcInstance, RecipeItemMaker>());
	
	public static RecipeController getInstance()
	{
		return _instance == null ? _instance = new RecipeController() : _instance;
	}
    
	public synchronized void requestBookOpen(L2PcInstance player, boolean isDwarvenCraft)
	{
		RecipeItemMaker maker = null;
		if (Config.ALT_GAME_CREATION) maker = activeMakers.get(player);
		
		if (maker == null)
		{
			RecipeBookItemList response = new RecipeBookItemList(isDwarvenCraft, player.getMaxMp());
			response.addRecipes(isDwarvenCraft	? player.getDwarvenRecipeBook()
			                                  	: player.getCommonRecipeBook());
			player.sendPacket(response);
			return;
		}
		
		SystemMessage sm = new SystemMessage(SystemMessage.CANT_ALTER_RECIPEBOOK_WHILE_CRAFTING);
		player.sendPacket(sm);
		return;
	}
	
	public synchronized void requestMakeItemAbort(L2PcInstance player)
	{
		activeMakers.remove(player);  // TODO:  anything else here?
	}
	
	public synchronized void requestManufactureItem(L2PcInstance manufacturer, int recipeListId,
	                                                L2PcInstance player)
	{
        L2Recipe pRecipe = getValidRecipeList(player, recipeListId);

		if (pRecipe == null) return;
		
		RecipeItemMaker maker;
		
		if (Config.ALT_GAME_CREATION && (maker = activeMakers.get(manufacturer)) != null) // check if busy
		{
			player.sendMessage("Manufacturer is busy, please try again later.");
			return;
		}
		
		maker = new RecipeItemMaker(manufacturer, pRecipe, player);
		if (maker.isValid)
		{
			if (Config.ALT_GAME_CREATION)
			{
				activeMakers.put(manufacturer, maker);
				ThreadPoolManager.getInstance().scheduleGeneral(maker, 100);
			}
			else maker.run();
		}
	}
	
	public synchronized void requestMakeItem(L2PcInstance player, int recipeListId)
	{
        L2Recipe pRecipe = getValidRecipeList(player, recipeListId);

		if (pRecipe == null)	return;
		
		RecipeItemMaker maker;
		
		// check if already busy (possible in alt mode only)
		if (Config.ALT_GAME_CREATION && ((maker = activeMakers.get(player)) != null)) 
		{
			SystemMessage sm = new SystemMessage(614);
			sm.addString("You are busy creating ");
			sm.addItemName(pRecipe.getItemId());
			player.sendPacket(sm);
			return;
		}
		
		maker = new RecipeItemMaker(player, pRecipe, player);
		if (maker.isValid)
		{
			if (Config.ALT_GAME_CREATION)
			{
				activeMakers.put(player, maker);
				ThreadPoolManager.getInstance().scheduleGeneral(maker, 100);
			}
			else maker.run();
		}
	}
	
	public class RecipeItemMaker implements Runnable
	{
		public boolean isValid;
		List<TempItem> items = null;
		final L2Recipe recipeList;
		final L2PcInstance player; // "crafter"
		final L2PcInstance target; // "customer"		
		final L2Skill skill;
		final int skillId;
		final int skillLevel;
		double creationPasses;
		double manaRequired;
		int price;
		int totalItems;
		int materialsRefPrice;
		int delay;
		
		public RecipeItemMaker(L2PcInstance pPlayer, L2Recipe pRecipeList, L2PcInstance pTarget)
		{
			this.player = pPlayer;
			this.target = pTarget;
			this.recipeList = pRecipeList;
			
			isValid = false;
			skillId = recipeList.isDwarvenRecipe()	? L2Skill.SKILL_CREATE_DWARVEN
			                                      	: L2Skill.SKILL_CREATE_COMMON;
			skillLevel = player.getSkillLevel(skillId);
			skill = player.getKnownSkill(skillId);
			
			player.isInCraftMode(true);
			
			if (player.isAlikeDead())
			{
				player.sendMessage("Dead people don't craft.");
				player.sendPacket(new ActionFailed());
				abort();
				return;
			}
			
			if (target.isAlikeDead())
			{
				target.sendMessage("Dead customers can't use manufacture.");
				target.sendPacket(new ActionFailed());
				abort();
				return;
			}
			
			if(target.isProcessingTransaction())
			{
				target.sendMessage("You are busy.");
				target.sendPacket(new ActionFailed());
				abort();
				return;
			}

			if(player.isProcessingTransaction())
			{
				if(player!=target)
				{
					target.sendMessage("Manufacturer "+player.getName() + " is busy.");	
				}
				player.sendPacket(new ActionFailed());
				abort();
				return;
			}
			
			// validate recipe list
			if ((recipeList == null) || (recipeList.getRecipeComponents().length == 0))
			{
				player.sendMessage("No such recipe");
				player.sendPacket(new ActionFailed());
				abort();
				return;
			}
			
			manaRequired = recipeList.getMpCost();
			
			// validate skill level
			if (recipeList.getLevel() > skillLevel)
			{
				player.sendMessage("Need skill level " + recipeList.getLevel());
				player.sendPacket(new ActionFailed());
				abort();
				return;
			}
			
			// check that customer can afford to pay for creation services
			if (player != target)
			{
				for (L2ManufactureItem temp : player.getCreateList().getList())
					if (temp.getRecipeId() == recipeList.getId()) // find recipe for item we want manufactured
					{
						price = temp.getCost();
						if (target.getAdena() < price) // check price
						{
							target.sendPacket(new SystemMessage(SystemMessage.YOU_NOT_ENOUGH_ADENA));
							abort();
							return;
						}
						break;
					}
			}
			
			// make temporary items
			if ((items = listItems(false)) == null) 
				{
					abort();
					return;
				}
			
			// calculate reference price
			for (TempItem i : items)
			{
				materialsRefPrice += i.getReferencePrice() * i.getQuantity();
				totalItems += i.getQuantity();
			}
			// initial mana check requires MP as written on recipe
			if (player.getCurrentMp() < manaRequired)
			{
				target.sendPacket(new SystemMessage(SystemMessage.NOT_ENOUGH_MP));
				abort();
				return;
			}
			
			// determine number of creation passes needed
			// can "equip"  skillLevel items each pass
			creationPasses = (totalItems / skillLevel) + ((totalItems % skillLevel)!=0 ? 1 : 0);
			
			if (Config.ALT_GAME_CREATION && creationPasses != 0) // update mana required to "per pass"
				manaRequired /= creationPasses; // checks to validateMp() will only need portion of mp for one pass
	
			updateMakeInfo(true); 
			updateCurMp();
			updateCurLoad();
			
			player.isInCraftMode(false);
			isValid = true;
		}
		
		public void run()
		{	
			if (!Config.IS_CRAFTING_ENABLED)
			{
				target.sendMessage("Item creation is currently disabled.");
				abort();
				return;
			}

			if (player == null || target == null)
			{
				_log.warn("player or target == null (disconnected?), aborting"+target+player);
				abort();
				return;
			}

			if (player.isOnline()==0 || target.isOnline()==0)
			{
				_log.warn("player or target is not online, aborting "+target+player);
				abort();
				return;
			}

			if (Config.ALT_GAME_CREATION && activeMakers.get(player) == null)
			{			
				if (target!=player) 
				{
					target.sendMessage("Manufacture aborted");
					player.sendMessage("Manufacture aborted");
				} 
				else
				{
					player.sendMessage("Item creation aborted");		
				}
						
				abort();
				return;
			}		
					
			if (Config.ALT_GAME_CREATION && !items.isEmpty())
			{
				
				if (!validateMp()) return;				// check mana				
				player.reduceCurrentMp(manaRequired); 	// use some mp
				updateCurMp();							// update craft window mp bar
				
				grabSomeItems(); // grab (equip) some more items with a nice msg to player
				
				// if still not empty, schedule another pass
				if(!items.isEmpty())
				{
					// divided by RATE_CONSUMABLES_COST to remove craft time increase on higher consumables rates 
					delay = (int) (Config.ALT_GAME_CREATION_SPEED * player.getMReuseRate(skill)
							* GameTimeController.TICKS_PER_SECOND / Config.RATE_CRAFT_COST)
							* GameTimeController.MILLIS_IN_TICK;
					
					// FIXME: please fix this packet to show crafting animation (somebody)
					MagicSkillUser msk = new MagicSkillUser(player, skillId, skillLevel, delay, 0);
					player.broadcastPacket(msk);
					
					player.sendPacket(new SetupGauge(0, delay));
					ThreadPoolManager.getInstance().scheduleGeneral(this, 100 + delay);
				} 
				else 
				{
					// for alt mode, sleep delay msec before finishing
					player.sendPacket(new SetupGauge(0, delay));
					
					try { 
						Thread.sleep(delay); 
					} catch (InterruptedException e) {
					} finally {
						finishCrafting();
					}
				}
			}    // for old craft mode just finish
			else finishCrafting();
		}
	
		private void finishCrafting()
		{
			if(!Config.ALT_GAME_CREATION) player.reduceCurrentMp(manaRequired);
			
			// first take adena for manufacture
			if ((target != player) && price > 0) // customer must pay for services
			{
				// attempt to pay for item
				L2ItemInstance adenatransfer = target.transferItem("PayManufacture",
										target.getInventory().getAdenaInstance().getObjectId(),
										price, player.getInventory(), player);
				
				if(adenatransfer==null)
				{
					target.sendPacket(new SystemMessage(SystemMessage.YOU_NOT_ENOUGH_ADENA));
					abort();
					return;	
				}
			}

			if ((items = listItems(true)) == null) // this line actually takes materials from inventory
			{ // handle possible cheaters here 
			  // (they click craft then try to get rid of items in order to get free craft)
			}
			else if (Rnd.get(100) < recipeList.getSuccessRate())
			{
				RewardPlayer(); // and immediately puts created item in its place		
				updateMakeInfo(true);
			}
			else
			{
				player.sendMessage("Item(s) failed to create");
				if (target != player)
				    target.sendMessage("Item(s) failed to create");
                               
				updateMakeInfo(false);
			}
			// update load and mana bar of craft window
			updateCurMp();
			updateCurLoad();				
			activeMakers.remove(player);
			player.isInCraftMode(false);
			target.sendPacket(new ItemList(target, false));			
		}
		private void updateMakeInfo(boolean success)
		{
			if (target == player) target.sendPacket(new RecipeItemMakeInfo(recipeList.getId(), target,
			                                                               success));
			else target.sendPacket(new RecipeShopItemInfo(player.getObjectId(), recipeList.getId()));
		}
		
		private void updateCurLoad()
		{
			StatusUpdate su = new StatusUpdate(target.getObjectId());
			su.addAttribute(StatusUpdate.CUR_LOAD, target.getCurrentLoad());
			target.sendPacket(su);
		}
		
		private void updateCurMp()
		{
			StatusUpdate su = new StatusUpdate(target.getObjectId());
			su.addAttribute(StatusUpdate.CUR_MP, (int) target.getCurrentMp());
			target.sendPacket(su);
		}
		
		private void grabSomeItems()
		{
			int numItems = skillLevel;
			
			while (numItems > 0 && !items.isEmpty())
			{
				TempItem item = items.get(0);
				
				int count = item.getQuantity();
				if (count >= numItems) count = numItems;
				
				item.setQuantity(item.getQuantity() - count);
				if (item.getQuantity() <= 0) items.remove(0);
				else items.set(0, item);
				
				numItems -= count;
				
				if (target == player)
				{
					SystemMessage sm = new SystemMessage(368); // you equipped ...
					sm.addNumber(count);
					sm.addItemName(item.getItemId());
					player.sendPacket(sm);
				} 
				else target.sendMessage("Manufacturer " + player.getName() + " used " + count + " "
				                        + item.getItemName());
			}
		}
		
		private boolean validateMp()
		{
			if (player.getCurrentMp() < manaRequired)
			{
				// rest (wait for MP)
				if (Config.ALT_GAME_CREATION)
				{
					player.sendPacket(new SetupGauge(0, delay));
					ThreadPoolManager.getInstance().scheduleGeneral(this, 100 + delay);
				}
				else
					// no rest - report no mana
				{
					target.sendPacket(new SystemMessage(SystemMessage.NOT_ENOUGH_MP));
					abort(); 
				}
				return false;
			}
			return true;
		}
		
		private List<TempItem> listItems(boolean remove)
		{
			L2RecipeComponent[] recipes = recipeList.getRecipeComponents();
			Inventory inv = target.getInventory();
			List<TempItem> materials = new FastList<TempItem>();
			
			for (L2RecipeComponent recipe : recipes)
			{
				int quantity = recipeList.isConsumable() ? (int) (recipe.getQuantity() * Config.RATE_CRAFT_COST)
				                                         : (int) recipe.getQuantity();
				
				if (quantity > 0)
				{
					L2ItemInstance item = inv.getItemByItemId(recipe.getItemId());
					
					// check materials
					if (item==null || item.getCount() < quantity)
					{
						target.sendMessage("You dont have the right elements for making this item"
						                   + ((recipeList.isConsumable() && Config.RATE_CRAFT_COST != 1)	? ".\nDue to server rates you need "
						                                                                                     	  + Config.RATE_CRAFT_COST
						                                                                                     	  + "x more material than listed in recipe"
						                                                                                     	  : ""));
						abort();
						return null;
					}
					
					// make new temporary object, just for counting puroses

					TempItem temp = new TempItem(item, quantity);
					materials.add(temp);
				}
			}

			if (remove)
			{
				for(TempItem tmp : materials)
					{
					inv.destroyItemByItemId("Manufacture", tmp.getItemId(), tmp.getQuantity(), target,
					                        player);
					}
			}			
			return materials;
		}
		
		private void abort()
		{
			updateMakeInfo(false);
			player.isInCraftMode(false);
			activeMakers.remove(player);
		}
		
		/**
		 * FIXME: This class should be in some other file, but I don't know where 
		 * 
		 * Class explanation:
		 * For item counting or checking purposes. When you don't want to modify inventory 
		 * class contains itemId, quantity, ownerId, referencePrice, but not objectId 
		 */
		private class TempItem
		{ // no object id stored, this will be only "list" of items with it's owner
			private int _itemId;
			private int _quantity;
			private int _ownerId;
			private int _referencePrice;
			private String _itemName;
			
			/**
			 * @param item
			 * @param quantity of that item
			 */
			public TempItem(L2ItemInstance item, int quantity)
			{
				super();
				_itemId = item.getItemId();
				_quantity = quantity;
				_ownerId = item.getOwnerId();
				_itemName = item.getItem().getName();
				_referencePrice = item.getReferencePrice();
			}
			
			/**
			 * @return Returns the quantity.
			 */
			public int getQuantity()
			{
				return _quantity;
			}
			
			/**
			 * @param quantity The quantity to set.
			 */
			public void setQuantity(int quantity)
			{
				_quantity = quantity;
			}
			
			public int getReferencePrice()
			{
				return _referencePrice;
			}
			
			/**
			 * @return Returns the itemId.
			 */
			public int getItemId()
			{
				return _itemId;
			}
			
			/**
			 * @return Returns the ownerId.
			 */
			public int getOwnerId()
			{
				return _ownerId;
			}
			
			/**
			 * @return Returns the itemName.
			 */
			public String getItemName()
			{
				return _itemName;
			}
		}
		
		private void RewardPlayer()
		{
			int itemId = recipeList.getItemId();
			int itemCount = recipeList.getCount();
			
            if (player.isGM())
            {
                // DaDummy: this way we log _every_ gmtransfer with all related info
                String params = target.getName() + " - " + String.valueOf(itemCount) + " - 0 - " + String.valueOf(itemId) + " - " + ItemTable.getInstance().getTemplate(itemId).getName();

                GMAudit.auditGMAction(player, "transferitem", "PcInventory", params);
            }
            
			L2ItemInstance createdItem = target.getInventory().addItem("Manufacture", itemId, itemCount,
			                                                           target, player);
			
			// inform customer of earned item
            SystemMessage sm = null;
            if (itemCount > 1)
            {
    			sm = new SystemMessage(SystemMessage.EARNED_S2_S1_s);
    			sm.addItemName(itemId);
                sm.addNumber(itemCount);
    			target.sendPacket(sm);
            } else
            {
                sm = new SystemMessage(SystemMessage.EARNED_ITEM);
                sm.addItemName(itemId);
                target.sendPacket(sm);
            }
			
			if (target != player)
			{
				// inform manufacturer of earned profit
				sm = new SystemMessage(SystemMessage.EARNED_ADENA);
				sm.addNumber(price);
				player.sendPacket(sm);
			}
			
			if (Config.ALT_GAME_CREATION)
			{
				int recipeLevel = recipeList.getLevel();
				int exp = createdItem.getReferencePrice() * itemCount;
				// one variation
				
				// exp -= materialsRefPrice;   // mat. ref. price is not accurate so other method is better
				
				if (exp < 0) exp = 0;
				
				// another variation
				exp /= recipeLevel;
				for (int i = skillLevel; i > recipeLevel; i--)
					exp /= 4;
				
				int sp = exp / 10;
				 
				// Added multiplication of Creation speed with XP/SP gain
				// slower crafting -> more XP,  faster crafting -> less XP 
				// you can use ALT_GAME_CREATION_XP_RATE/SP to
				// modify XP/SP gained (default = 1)   
				
				player.addExpAndSp((int) player.calcStat(Stats.EXPSP_RATE, exp * Config.ALT_GAME_CREATION_XP_RATE  
				                                         * Config.ALT_GAME_CREATION_SPEED, null, null)
				                  ,(int) player.calcStat(Stats.EXPSP_RATE, sp * Config.ALT_GAME_CREATION_SP_RATE   
				                                         * Config.ALT_GAME_CREATION_SPEED, null, null));
			}
			updateMakeInfo(true); // success
		}
	}
	
    /**
     * 
     * @param player
     * @param id
     * @return
     */
    private L2Recipe getValidRecipeList(L2PcInstance player, int id)
    {
        L2Recipe recipe =  l2RecipeService.getRecipeList(id - 1);
        
        if ((recipe == null) || (recipe.getRecipeComponents().length == 0))
        {
            player.sendMessage("No recipe for: " + id);
            player.isInCraftMode(false);
            return null;
        }
        return recipe;
    }
}
