package net.fullstackjones.bigbraincurrency.block.entities;

import net.fullstackjones.bigbraincurrency.block.ModBlockEntities;
import net.fullstackjones.bigbraincurrency.menu.ShopMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.fullstackjones.bigbraincurrency.menu.ModContainers.SHOPMENU;

public class ShopBlockEntity extends BaseContainerBlockEntity {
    public static final int SHOPSIZE = 32;

    private NonNullList<ItemStack> shopItems = NonNullList.withSize(SHOPSIZE, ItemStack.EMPTY);

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
    }


    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        ContainerHelper.loadAllItems(tag.getCompound("shop"), shopItems, registries);
    }

    @Override
    protected @NotNull NonNullList<ItemStack> getItems() {
        return shopItems;
    }

    @Override
    protected void setItems(@NotNull NonNullList<ItemStack> items) {
        shopItems = items;
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
}
