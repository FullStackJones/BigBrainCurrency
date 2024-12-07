package net.fullstackjones.bigbraincurrency.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.UUID;

public class BaseShopData  implements INBTSerializable<CompoundTag> {
    protected int profit;
    protected int price;
    protected int stockQuantity;
    protected int stockItemId;
    protected @Nullable UUID ownerId;

    public int getPrice() {
        return price;
    }

    public int getProfit() {
        return profit;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public Item getStockItem() {
        return Item.byId(stockItemId);
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }

    public void setProfit(int profit) {
        this.profit = profit;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public void setStockItemId(Item item) {
        this.stockItemId = Item.getId(item);
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("Profit", profit);
        tag.putInt("Price", price);
        tag.putInt("StockQuantity", stockQuantity);
        tag.putInt("StockItemId", stockItemId);
        tag.putUUID("OwnerID", ownerId);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        profit = nbt.getInt("Profit");
        price = nbt.getInt("Price");
        stockQuantity = nbt.getInt("StockQuantity");
        stockItemId = nbt.getInt("StockItemId");
        ownerId = nbt.getUUID("OwnerID");
    }
}
