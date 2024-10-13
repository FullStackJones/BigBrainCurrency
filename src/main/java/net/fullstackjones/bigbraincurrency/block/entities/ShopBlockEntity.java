package net.fullstackjones.bigbraincurrency.block.entities;

import net.fullstackjones.bigbraincurrency.block.ModBlockEntities;
import net.fullstackjones.bigbraincurrency.menu.ShopMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.logging.Logger;

public class ShopBlockEntity extends BaseContainerBlockEntity {
    private static final Logger LOGGER = Logger.getLogger(ShopBlockEntity.class.getName());
    public static final int SHOPSIZE = 36;

    private NonNullList<ItemStack> shopItems = NonNullList.withSize(SHOPSIZE, ItemStack.EMPTY);
    private UUID owner = null;

    public ShopBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SHOPENTITY.get(), pos, state);
    }

    @Override
    protected @NotNull AbstractContainerMenu createMenu(int containerId, @NotNull Inventory inventory) {
        return new ShopMenu(containerId, inventory, this);
    }

    @Override
    protected @NotNull Component getDefaultName() {
        return Component.translatable("container.bigbraincurrency.shopentity");
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("shop", ContainerHelper.saveAllItems(new CompoundTag(), shopItems, registries));
        if(owner != null){
            tag.putUUID("owner", owner);
        }
    }


    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        ContainerHelper.loadAllItems(tag.getCompound("shop"), shopItems, registries);
        if (tag.hasUUID("owner")) {
            owner = tag.getUUID("owner");
        } else {
            owner = null;
        }
    }

    @Override
    protected @NotNull NonNullList<ItemStack> getItems() {
        return shopItems;
    }

    @Override
    protected void setItems(@NotNull NonNullList<ItemStack> items) {
        shopItems = items;
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public @NotNull ItemStack removeItem(int slot, int amount) {
        LOGGER.info("removeItem called with slot: " + slot + ", amount: " + amount);
        ItemStack itemStack = ContainerHelper.removeItem(shopItems, slot, amount);
        LOGGER.info("Item count after removal: " + shopItems.get(slot).getCount());
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), this.getBlockState(), 3);
        }
        return itemStack;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
        resetRenderer();
    }

    @Override
    public int getContainerSize() {
        return SHOPSIZE;
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider lookupProvider) {
        CompoundTag tag = pkt.getTag();
        this.loadAdditional(tag, lookupProvider);
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        super.handleUpdateTag(tag, lookupProvider);
        this.loadAdditional(tag, lookupProvider);
    }

    public void resetRenderer() {
        requestModelDataUpdate();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public void requestModelDataUpdate() {
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public UUID getOwnerUUID() {
        return owner;
    }

    public void setOwnerUUID(UUID owner) {
        this.owner = owner;
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }
}
