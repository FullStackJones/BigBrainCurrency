package net.fullstackjones.bigbraincurrency.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import javax.xml.crypto.Data;
import java.util.UUID;

import static org.openjdk.nashorn.internal.objects.Global.println;

public class BaseShopData  implements INBTSerializable<CompoundTag> {
    protected int profit;
    protected int price;
    protected int saleQuantity;
    protected int stockQuantity;
    protected int stockItemId;
    protected ItemStack stockStackData = ItemStack.EMPTY; // <- this feels yucky id rather just store the tags
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

    public int getSaleQuantity() {
        return saleQuantity;
    }

    public Item getStockItem() {
        return stockStackData.getItem();
    }

    public @Nullable UUID getOwnerId() {
        return ownerId;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setOwnerId(@Nullable UUID ownerId) {
        this.ownerId = ownerId;
    }

    public void setProfit(int profit) {
        this.profit = profit;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public void setSaleQuantity(int saleQuantity) {
        this.saleQuantity = saleQuantity;
    }

    public void setStockItemId(Item item) {
        this.stockItemId = Item.getId(item);
    }

    public void setStockStackData(ItemStack item) {
        this.stockStackData = item;
    }

    public ItemStack getStockStackData() {
        return this.stockStackData;
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("Profit", profit);
        tag.putInt("Price", price);
        tag.putInt("StockQuantity", stockQuantity);
        tag.putInt("StockItemId", stockItemId);
        tag.putInt("SaleQuantity", saleQuantity);

        if(ownerId != null)
            tag.putUUID("OwnerID", ownerId);

        if(stockStackData.isEmpty())
            return tag;
        ItemStack stack = stockStackData.copy();
        stack.setCount(1);
        ListTag nbtTagList = new ListTag();
        CompoundTag itemTag = new CompoundTag();
        itemTag.putInt("Slot", 0);
        nbtTagList.add(stack.save(provider, itemTag));
        tag.put("stockStackData", nbtTagList);

        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        profit = nbt.getInt("Profit");
        price = nbt.getInt("Price");
        stockQuantity = nbt.getInt("StockQuantity");
        stockItemId = nbt.getInt("StockItemId");
        saleQuantity = nbt.getInt("SaleQuantity");

        if(nbt.contains("OwnerID"))
            ownerId = nbt.getUUID("OwnerID");
        if(nbt.contains("stockStackData")){
            ListTag tagList = nbt.getList("stockStackData", Tag.TAG_COMPOUND);
            NonNullList<ItemStack> stacks = NonNullList.withSize(1, ItemStack.EMPTY);
            CompoundTag itemTags = tagList.getCompound(0);
            ItemStack.parse(provider, itemTags).ifPresent(stack -> stacks.set(0, stack));
            stacks.getFirst().setCount(stockQuantity);
            stockStackData = stacks.getFirst();
        }
    }
}
