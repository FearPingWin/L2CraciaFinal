package com.l2jfree.gameserver.instancemanager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import com.l2jfree.L2DatabaseFactory;
import com.l2jfree.gameserver.gameobjects.L2Player;
import com.l2jfree.gameserver.gameobjects.itemcontainer.ItemContainer;
import com.l2jfree.gameserver.gameobjects.itemcontainer.MarketContainer;
import com.l2jfree.gameserver.model.items.L2ItemInstance;
import com.l2jfree.gameserver.network.packets.server.ItemList;

public final class MarketManager {
    private static final MarketManager INSTANCE = new MarketManager();
    public static MarketManager getInstance() { return INSTANCE; }
    private MarketManager() {}

    public static final class ItemRequest {
        private final int objectId;
        private final long count;
        private final long priceAdena;
        public ItemRequest(int objectId, long count, long priceAdena) {
            this.objectId = objectId;
            this.count = count;
            this.priceAdena = priceAdena;
        }
        public int getObjectId()   { return objectId; }
        public long getCount()     { return count; }
        public long getPriceAdena(){ return priceAdena; }
    }

    private static final String SQL_UPSERT_LISTING =
        "INSERT INTO market_listings (" +
        " owner_char_id, owner_account, item_object_id, item_id, enchant_level, count, price_adena, currency_item_id, status, created_at" +
        ") VALUES (?, ?, ?, ?, ?, ?, ?, 57, 'LISTED', NOW()) " + // 57 = Adena
        "ON DUPLICATE KEY UPDATE " +
        " owner_char_id=VALUES(owner_char_id)," +
        " owner_account=VALUES(owner_account)," +
        " item_id=VALUES(item_id)," +
        " enchant_level=VALUES(enchant_level)," +
        " count=VALUES(count)," +
        " price_adena=VALUES(price_adena)," +
        " currency_item_id=VALUES(currency_item_id)," +
        " status='LISTED'";

    public void commitListingsFromInventory(L2Player player, List<ItemRequest> requests) throws Exception {
        if (player == null || requests == null || requests.isEmpty())
            return;

        final ItemContainer marketBox = new MarketContainer(player);

        try (Connection con = L2DatabaseFactory.getInstance().getConnection()) {
            con.setAutoCommit(false);

            for (ItemRequest r : requests) {
                // Ищем предмет
                L2ItemInstance src = player.getInventory().getItemByObjectId(r.getObjectId());
                if (src == null)
                    throw new IllegalStateException("Предмет не найден в инвентаре.");
                if (r.getCount() <= 0 || r.getCount() > src.getCount())
                    throw new IllegalStateException("Неверное количество.");
                if (src.isEquipped())
                    throw new IllegalStateException("Сначала снимите предмет.");

                // Перенос (отщепит часть стака при необходимости)
                L2ItemInstance moved = player.getInventory().transferItem(
                        "WebMarket", src.getObjectId(), r.getCount(), marketBox, player, null);

                if (moved == null)
                    throw new IllegalStateException("Не удалось перенести предмет в MARKET.");

                // Запись лота
                try (PreparedStatement ps = con.prepareStatement(SQL_UPSERT_LISTING)) {
                    ps.setInt(1, player.getObjectId());
                    ps.setString(2, player.getAccountName());
                    ps.setInt(3, moved.getObjectId());
                    ps.setInt(4, moved.getItemId());
                    ps.setInt(5, moved.getEnchantLevel());
                    ps.setLong(6, moved.getCount());
                    ps.setLong(7, r.getPriceAdena());
                    ps.executeUpdate();
                }
            }

            con.commit();
        } finally {
            // Обновляем инвентарь клиенту; если у тебя другое API — замени на актуальное
            player.sendPacket(new ItemList(player, false));
        }
    }

    /** Удобный хелпер для одного предмета. */
    public void commitSingle(L2Player player, int objectId, long count, long priceAdena) throws Exception {
        List<ItemRequest> one = new ArrayList<>(1);
        one.add(new ItemRequest(objectId, count, priceAdena));
        commitListingsFromInventory(player, one);
    }
    
    public void withdrawListingToInventory(com.l2jfree.gameserver.gameobjects.L2Player pc, long listingId) throws Exception {
    if (pc == null)
        return;

    // 1) Находим лот владельца (берём только object_id, count из БД не доверяем)
    final int objectId;
    try (java.sql.Connection con = com.l2jfree.L2DatabaseFactory.getInstance().getConnection();
         java.sql.PreparedStatement ps = con.prepareStatement(
                 "SELECT item_object_id FROM market_listings " +
                 "WHERE listing_id=? AND owner_char_id=? AND status='LISTED'")) {
        ps.setLong(1, listingId);
        ps.setInt(2, pc.getObjectId());
        try (java.sql.ResultSet rs = ps.executeQuery()) {
            if (!rs.next())
                throw new IllegalStateException("Лот не найден или уже снят.");
            objectId = rs.getInt(1);
        }
    }

    // 2) Находим живой инстанс предмета в мире и проверяем, что он действительно в MARKET у этого игрока
    com.l2jfree.gameserver.gameobjects.L2Object obj =
            com.l2jfree.gameserver.model.world.L2World.getInstance().findObject(objectId);
    if (!(obj instanceof com.l2jfree.gameserver.model.items.L2ItemInstance))
        throw new IllegalStateException("Предмет не найден.");
    com.l2jfree.gameserver.model.items.L2ItemInstance item =
            (com.l2jfree.gameserver.model.items.L2ItemInstance)obj;

    if (item.getOwnerId() != pc.getObjectId()
            || item.getLocation() != com.l2jfree.gameserver.model.items.L2ItemInstance.ItemLocation.MARKET)
        throw new IllegalStateException("Неверное состояние предмета.");

    final long takeCount = item.getCount(); // забираем весь фактический остаток
    if (takeCount <= 0)
        throw new IllegalStateException("Пустой лот.");

    // 3) Переносим из MARKET в инвентарь через контейнер (без restore!)
    final com.l2jfree.gameserver.gameobjects.itemcontainer.MarketContainer box =
            new com.l2jfree.gameserver.gameobjects.itemcontainer.MarketContainer(pc);
    // «прикрепляем» живой инстанс к контейнеру и переносим
    com.l2jfree.gameserver.model.items.L2ItemInstance attached =
            box.attachExisting(item.getObjectId());
    if (attached == null)
        throw new IllegalStateException("Не удалось подготовить предмет к возврату.");

    com.l2jfree.gameserver.model.items.L2ItemInstance moved =
            box.transferItem("MarketWithdrawById", item.getObjectId(), takeCount, pc.getInventory(), pc, null);
    if (moved == null)
        throw new IllegalStateException("Не удалось вернуть предмет в инвентарь (вес/слоты?).");

    // 4) Закрываем лот (без updated_at)
    try (java.sql.Connection con = com.l2jfree.L2DatabaseFactory.getInstance().getConnection();
         java.sql.PreparedStatement ps = con.prepareStatement(
                 "UPDATE market_listings SET status='CANCELLED' WHERE listing_id=?")) {
        ps.setLong(1, listingId);
        ps.executeUpdate();
    }

    // 5) Обновляем клиент
    pc.sendPacket(new com.l2jfree.gameserver.network.packets.server.ItemList(pc, false));
    pc.sendMessage("Лот снят с продажи. Предмет возвращён в инвентарь.");
}


    
}
