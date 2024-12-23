package net.fullstackjones.bigbraincurrency.entities;

import net.fullstackjones.bigbraincurrency.Utills.CurrencyUtil;
import net.fullstackjones.bigbraincurrency.data.BaseShopData;
import net.fullstackjones.bigbraincurrency.menu.SimpleShopMenu;
import net.fullstackjones.bigbraincurrency.registration.ModBlockEntities;
import net.fullstackjones.bigbraincurrency.menu.ShopMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class SimpleShopBlockEntity extends BlockEntity implements MenuProvider {
    public static final int SHOPSIZE = 36;

    public ItemStackHandler shopItems = new ItemStackHandler(SHOPSIZE){
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if(!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    private UUID owner = null;

    // this will be ugly this release to allow for migration of data
    public BaseShopData data = new BaseShopData();

    public SimpleShopBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SHOP_ENTITY.get(), pos, state);
    }

    public void setData(BaseShopData data) {
        this.data = data;
        updateData();
    }

    public void setOwner(UUID owner) {
        this.data.setOwnerId(owner);
        updateData();
    }

    public void setStockQuantity(int quantity) {
        this.data.setStockQuantity(quantity);
        updateData();
    }

    public void setSaleQuantity(int quantity) {
        this.data.setSaleQuantity(quantity);

        updateData();
    }

    public void setProfit(int profit) {
        this.data.setProfit(profit);
        updateData();
    }

    public void AddSale() {
        this.data.setProfit(this.data.getProfit() + this.data.getPrice());
        updateData();
    }

    public void setPrice(int price) {
        this.data.setPrice(price);
        updateData();
    }

    public void setItem(Item item) {
        this.data.setStockItemId(item);
        updateData();
    }

    public void setItemData(ItemStack item) {
        this.data.setStockStackData(item);
        updateData();
    }

    public ItemStack getItemStack() {
        ItemStack items = new ItemStack(this.data.getStockItem(), 0);
        DataComponentMap dataStack = this.data.getStockStackData().getComponents();
        for(DataComponentType key : dataStack.keySet()){
            var x = dataStack.get(key);
            items.set(key, x);
        }

        return items;
    }

    private void updateData(){
        setChanged();
        if(!level.isClientSide()) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    // Depreciated methods start
    public int GetShopBalance(){
        return CurrencyUtil.calculateTotalValue(shopItems.getStackInSlot(30).getCount(), shopItems.getStackInSlot(29).getCount(), shopItems.getStackInSlot(28).getCount(), shopItems.getStackInSlot(27).getCount());
    }

    public int GetShopPrice(){
        return CurrencyUtil.calculateTotalValue(shopItems.getStackInSlot(35).getCount(), shopItems.getStackInSlot(34).getCount(), shopItems.getStackInSlot(33).getCount(), shopItems.getStackInSlot(32).getCount());
    }
    // Depreciated methods end

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.bigbraincurrency.shopentity");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new SimpleShopMenu(containerId, playerInventory, this);
    }

    private void migrateData(ItemStackHandler shopItems) {
        data.setPrice(GetShopPrice());
        data.setProfit(GetShopBalance());

        int countedItems = 0;
        for(int i = 0; i < 27; i++){
            countedItems += shopItems.getStackInSlot(i).getCount();
        }

        data.setStockQuantity(countedItems);
        data.setStockStackData(shopItems.getStackInSlot(31));
        data.setStockItemId(shopItems.getStackInSlot(31).getItem());

        data.setSaleQuantity(shopItems.getStackInSlot(31).getCount());

        data.setOwnerId(owner);

        setChanged();
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("shop", shopItems.serializeNBT(registries));
        if(owner != null){
            tag.putUUID("owner", owner);
        }
        tag.put("data", data.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        shopItems.deserializeNBT(registries, tag.getCompound("shop"));
        if (tag.hasUUID("owner")) {
            owner = tag.getUUID("owner");
        } else {
            owner = null;
        }
        if(tag.contains("data", CompoundTag.TAG_COMPOUND)){
            data.deserializeNBT(registries, tag.getCompound("data"));
        }
        if(!tag.contains("data", CompoundTag.TAG_COMPOUND)){
            migrateData(shopItems);
        }
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
