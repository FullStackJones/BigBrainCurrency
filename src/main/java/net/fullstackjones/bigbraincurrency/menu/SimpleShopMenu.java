package net.fullstackjones.bigbraincurrency.menu;

import net.fullstackjones.bigbraincurrency.entities.BrainBankBlockEntity;
import net.fullstackjones.bigbraincurrency.entities.SimpleShopBlockEntity;
import net.fullstackjones.bigbraincurrency.registration.ModBlocks;
import net.fullstackjones.bigbraincurrency.registration.ModMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public class SimpleShopMenu extends AbstractContainerMenu {
    protected final int playerInventoryColumns = 9;
    protected final int playerInventoryRows = 4;

    protected final int slotSize = 18;

    protected final Inventory playerInventory;
    public final SimpleShopBlockEntity blockEntity;
    public final Container shopInventory;
    public final Level level;

    public SimpleShopMenu(int containerId, Inventory inventory, BlockEntity blockEntity) {
        super(ModMenus.SIMPLESHOPMENU.get(), containerId);
        this.playerInventory = inventory;
        this.blockEntity = (SimpleShopBlockEntity) blockEntity;
        this.level = inventory.player.level();
        this.shopInventory = new SimpleContainer(2) {
            @Override
            public void setChanged() {

                super.setChanged();
            }

            @Override
            public ItemStack removeItem(int pIndex, int pCount) {

                return super.removeItem(pIndex, pCount);
            }
        };
        addPlayerInventory();
    }

    public SimpleShopMenu(int containerId, Inventory inventory, RegistryFriendlyByteBuf extraData) {
        this(containerId, inventory, inventory.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    private void addPlayerInventory(){
        for (int k = 0; k < playerInventoryRows; k++) {
            for (int l = 0; l < playerInventoryColumns; l++) {
                if (k > 0) {
                    this.addSlot(new Slot(playerInventory, l + k * playerInventoryColumns, 8 + l * slotSize,  k * slotSize + 138));
                } else {
                    this.addSlot(new Slot(playerInventory, l, 8 + l * slotSize, 214));
                }
            }
        }
    }

    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;
    private static final int TE_INVENTORY_SLOT_COUNT = 1;  // must be the number of slots you have!
    @Override
    public ItemStack quickMoveStack(Player playerIn, int pIndex) {
        Slot sourceSlot = slots.get(pIndex);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;  //EMPTY_ITEM
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();
        // Check if the slot clicked is one of the vanilla container slots
        if (pIndex < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            // This is a vanilla container slot so merge the stack into the tile inventory
            if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX
                    + TE_INVENTORY_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;  // EMPTY_ITEM
            }
        } else if (pIndex < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            // This is a TE slot so merge the stack into the players inventory
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            System.out.println("Invalid slotIndex:" + pIndex);
            return ItemStack.EMPTY;
        }
        // If stack size == 0 (the entire stack was moved) set slot contents to null
        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), playerInventory.player, ModBlocks.SHOP_BLOCK.get());
    }
}
