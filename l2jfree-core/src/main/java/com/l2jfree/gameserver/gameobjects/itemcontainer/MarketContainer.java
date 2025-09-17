package com.l2jfree.gameserver.gameobjects.itemcontainer;

import com.l2jfree.gameserver.gameobjects.L2Object;
import com.l2jfree.gameserver.gameobjects.L2Player;
import com.l2jfree.gameserver.model.items.L2ItemInstance;
import com.l2jfree.gameserver.model.items.L2ItemInstance.ItemLocation;
import com.l2jfree.gameserver.model.world.L2World;

public final class MarketContainer extends ItemContainer {

    private final L2Player owner;

    public MarketContainer(L2Player owner) {
        this.owner = owner;
    }

    @Override
    public L2Player getOwner() {
        return owner;
    }

    @Override
    public int getOwnerId() {
        return owner != null ? owner.getObjectId() : 0;
    }

    @Override
    public ItemLocation getBaseLocation() {
        return ItemLocation.MARKET;
    }

    @Override
    public String getName() {
        return "Market";
    }

    public L2ItemInstance attachExisting(int objectId) {
        L2Object obj = L2World.getInstance().findObject(objectId);
        if (!(obj instanceof L2ItemInstance))
            return null;

        L2ItemInstance item = (L2ItemInstance) obj;

        if (item.getOwnerId() != getOwnerId())
            return null;
        if (item.getLocation() != ItemLocation.MARKET)
            return null;

        super.addItem(item);
        return item;
    }

}
