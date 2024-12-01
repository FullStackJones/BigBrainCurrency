package net.fullstackjones.bigbraincurrency.entities;

import net.fullstackjones.bigbraincurrency.Utills.CurrencyUtil;
import net.fullstackjones.bigbraincurrency.registration.ModBlockEntities;
import net.fullstackjones.bigbraincurrency.menu.ShopMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ShopBlockEntity extends BlockEntity implements MenuProvider {
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

    public ShopBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SHOPENTITY.get(), pos, state);
    }

    public UUID getOwnerUUID() {
        return owner;
    }

    public void setOwnerUUID(UUID owner) {
        this.owner = owner;
        setChanged();
    }

    public boolean IsThereProfit(){
        return !shopItems.getStackInSlot(27).isEmpty() || !shopItems.getStackInSlot(28).isEmpty() || !shopItems.getStackInSlot(29).isEmpty() || !shopItems.getStackInSlot(30).isEmpty();
    }

    public boolean IsShopPriceSet(){
        return !shopItems.getStackInSlot(32).isEmpty() || !shopItems.getStackInSlot(33).isEmpty() || !shopItems.getStackInSlot(34).isEmpty() || !shopItems.getStackInSlot(35).isEmpty();
    }

    public int GetShopBalance(){
        return CurrencyUtil.calculateTotalValue(shopItems.getStackInSlot(30).getCount(), shopItems.getStackInSlot(29).getCount(), shopItems.getStackInSlot(28).getCount(), shopItems.getStackInSlot(27).getCount());
    }

    public int GetShopPrice(){
        return CurrencyUtil.calculateTotalValue(shopItems.getStackInSlot(35).getCount(), shopItems.getStackInSlot(34).getCount(), shopItems.getStackInSlot(33).getCount(), shopItems.getStackInSlot(32).getCount());
    }

    public void UpdateShopProfits( ItemStack[] coins){
        shopItems.insertItem(30, coins[0], false);
        shopItems.insertItem(29, coins[1], false);
        shopItems.insertItem(28, coins[2], false);
        shopItems.insertItem(27, coins[3], false);
    }

    public void clearProfit(){
        shopItems.setStackInSlot(27, ItemStack.EMPTY);
        shopItems.setStackInSlot(28, ItemStack.EMPTY);
        shopItems.setStackInSlot(29, ItemStack.EMPTY);
        shopItems.setStackInSlot(30, ItemStack.EMPTY);
        setChanged();
    }

    public boolean stockIsEmpty(int amount){
        int countedItems = 0;
        for(int i = 0; i < 27; i++){
            countedItems += shopItems.getStackInSlot(i).getCount();
            if(countedItems >= amount){
                return false;
            }
        }
        return true;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.bigbraincurrency.shopentity");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new ShopMenu(containerId, playerInventory, this);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("shop", shopItems.serializeNBT(registries));
        if(owner != null){
            tag.putUUID("owner", owner);
        }
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
