package net.fullstackjones.bigbraincurrency.menu;

import net.fullstackjones.bigbraincurrency.block.ModBlocks;
import net.fullstackjones.bigbraincurrency.block.entities.ShopBlockEntity;
import net.fullstackjones.bigbraincurrency.menu.customslots.CurrencySlot;
import net.fullstackjones.bigbraincurrency.menu.customslots.PricingSlot;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

import static net.fullstackjones.bigbraincurrency.item.ModItems.*;

public class ShopMenu extends AbstractContainerMenu {
    protected final int inventoryColumns = 9;
    protected final int inventoryRows = 3;

    protected final int playerInventoryColumns = 9;
    protected final int playerInventoryRows = 4;

    protected final int slotSize = 18;

    protected final Inventory playerInventory;
    public final ShopBlockEntity blockEntity;
    private final Level level;

    public ShopMenu(int containerId, Inventory inventory, RegistryFriendlyByteBuf extraData) {
        this(containerId, inventory, inventory.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public ShopMenu(int containerId, Inventory inventory, BlockEntity shop) {
        super(ModContainers.SHOPMENU.get(), containerId);
        this.blockEntity = ((ShopBlockEntity) shop);
        this.playerInventory = inventory;
        this.level = inventory.player.level();
        addShopSaleSlots();
        addPlayerInventory();
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

    private void addShopSaleSlots() {
        for (int k = 0; k < inventoryRows; k++) {
            for (int l = 0; l < inventoryColumns; l++) {
                this.addSlot(new SlotItemHandler(this.blockEntity.shopItems, l + k * inventoryColumns, 8 + l * slotSize,  k * slotSize + 76));
            }
        }

        this.addSlot(new SlotItemHandler(this.blockEntity.shopItems, 27, 98,  56));
        this.addSlot(new SlotItemHandler(this.blockEntity.shopItems, 28, 98 + slotSize,  56));
        this.addSlot(new SlotItemHandler(this.blockEntity.shopItems, 29, 98 + 2 * slotSize,  56));
        this.addSlot(new SlotItemHandler(this.blockEntity.shopItems, 30, 98 + 3 * slotSize,  56));

        this.addSlot(new SlotItemHandler(this.blockEntity.shopItems, 31, 8,  16));

        this.addSlot(new PricingSlot(this.blockEntity.shopItems, 32, 98,  16, PINKCOIN.toStack()));
        this.addSlot(new PricingSlot(this.blockEntity.shopItems, 33, 98 + slotSize,  16, GOLDCOIN.toStack()));
        this.addSlot(new PricingSlot(this.blockEntity.shopItems, 34, 98 + 2 * slotSize,  16, SILVERCOIN.toStack()));
        this.addSlot(new PricingSlot(this.blockEntity.shopItems, 35, 98 + 3 * slotSize,  16, COPPERCOIN.toStack()));
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index){
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemstack = stackInSlot.copy();

            // Define slot ranges
            int shopStockStart = 0;
            int shopStockEnd = 27; // Shop stock slots
            int playerInventoryStart = 0;
            int playerInventoryEnd = playerInventoryStart + playerInventory.getContainerSize() - 1;

            if (index >= shopStockStart && index <= shopStockEnd) {
                // Move from shop stock to player inventory
                if (!this.moveItemStackTo(stackInSlot, playerInventoryStart, playerInventoryEnd + 1, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= playerInventoryStart && index <= playerInventoryEnd) {
                // Move from player inventory to shop stock
                if (!this.moveItemStackTo(stackInSlot, shopStockStart, shopStockEnd + 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                return ItemStack.EMPTY;
            }

            if (stackInSlot.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, ModBlocks.SHOP_BLOCK.get());
    }

    @Override
    public void clicked(int slotId, int button, ClickType clickType, Player player) {
        if (slotId >= 32 && slotId <= 35) {
            handlePricingSlotClick(slotId, button);
        } else {
            super.clicked(slotId, button, clickType, player);
        }
    }

    public void handlePricingSlotClick(int slotIndex, int button) {
        if(blockEntity == null) {
            return;
        }
        ItemStack stack = blockEntity.shopItems.getStackInSlot(slotIndex).copy();
        if (button == 0) { // Left click
            if (stack.isEmpty()) {
                PricingSlot pricingSlot = (PricingSlot) this.getSlot(slotIndex);
                blockEntity.shopItems.insertItem(slotIndex, pricingSlot.getCurrencyType(), false);
                return;
            }
            stack.grow(1);
            if(stack.getCount() <= 7) {
                blockEntity.shopItems.setStackInSlot(slotIndex, stack);
            } else if (stack.getCount() < 100 && slotIndex == 32) {
                blockEntity.shopItems.setStackInSlot(slotIndex, stack);
            }
        } else if (button == 1) { // Right click
            if (!stack.isEmpty()) {
                blockEntity.shopItems.extractItem(slotIndex, 1, false);
            }
        }
    }
}
