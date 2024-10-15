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

    public ItemStackHandler getShopItems() {
        return shopItems;
    }

    public UUID getOwnerUUID() {
        return owner;
    }

    public void setOwnerUUID(UUID owner) {
        this.owner = owner;
        setChanged();
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
