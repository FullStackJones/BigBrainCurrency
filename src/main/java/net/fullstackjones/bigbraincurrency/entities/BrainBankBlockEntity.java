package net.fullstackjones.bigbraincurrency.entities;

import net.fullstackjones.bigbraincurrency.data.BrainBankData;
import net.fullstackjones.bigbraincurrency.menu.BrainBankMenu;
import net.fullstackjones.bigbraincurrency.registration.ModBlockEntities;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;


public class BrainBankBlockEntity extends BlockEntity implements MenuProvider {
    private final BrainBankData data = new BrainBankData(0);

    public BrainBankBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.BRAINBANK_ENTITY.get(), pos, blockState);
    }

    public BrainBankData getData() {
        return data;
    }

    public void setBankValue(int value) {
        data.setBankValue(value);
        setChanged();
        if(!level.isClientSide()) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    public void setUbi(boolean ubi) {
        data.setHadUbi(ubi);
        data.setUbiSetTime(LocalDateTime.now().plusDays(1).withHour(12).withMinute(0).withSecond(0));
        setChanged();
        if(!level.isClientSide()) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    public void setData(BrainBankData data) {
        this.data.setBankValue(data.getBankValue());
        this.data.setHadUbi(data.getHadUbi());
        this.data.setUbiSetTime(data.getUbiSetTime());
        setChanged();
        if(!level.isClientSide()) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.bigbraincurrency.brainbank");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new BrainBankMenu(containerId, playerInventory, this);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("data", data.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        data.deserializeNBT(registries, tag.getCompound("data"));
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
