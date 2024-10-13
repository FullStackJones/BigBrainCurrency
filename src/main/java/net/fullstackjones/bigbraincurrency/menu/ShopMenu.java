package net.fullstackjones.bigbraincurrency.menu;

import net.fullstackjones.bigbraincurrency.block.entities.ShopBlockEntity;
import net.fullstackjones.bigbraincurrency.menu.customslots.CurrencySlot;
import net.fullstackjones.bigbraincurrency.menu.customslots.PricingSlot;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import static net.fullstackjones.bigbraincurrency.item.ModItems.*;
import static net.fullstackjones.bigbraincurrency.menu.ModContainers.SHOPMENU;

public class ShopMenu extends AbstractContainerMenu {
    protected final int inventoryColumns = 9;
    protected final int inventoryRows = 3;

    protected final int playerInventoryColumns = 9;
    protected final int playerInventoryRows = 4;

    protected final int slotSize = 18;

    protected final Container shopContainer;
    protected final Inventory playerInventory;
    protected final ShopBlockEntity blockEntity;

    public ShopMenu(int i, Inventory inventory) {
        this(i, inventory, null);
    }

    public ShopMenu(int i, Inventory inventory, ShopBlockEntity shop) {
        super(SHOPMENU.get(), i);
        this.shopContainer = (shop != null) ? shop : new SimpleContainer(36);;
        this.blockEntity = shop;
        this.playerInventory = inventory;
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
                this.addSlot(new Slot(shopContainer, l + k * inventoryColumns, 8 + l * slotSize,  k * slotSize + 76));
            }
        }

        this.addSlot(new CurrencySlot(shopContainer, 27, 98,  56, PINKCOIN.toStack()));
        this.addSlot(new CurrencySlot(shopContainer, 28, 98 + slotSize,  56, GOLDCOIN.toStack()));
        this.addSlot(new CurrencySlot(shopContainer, 29, 98 + 2 * slotSize,  56, SILVERCOIN.toStack()));
        this.addSlot(new CurrencySlot(shopContainer, 30, 98 + 3 * slotSize,  56, COPPERCOIN.toStack()));

        this.addSlot(new Slot(shopContainer, 31, 8,  16));

        this.addSlot(new PricingSlot(shopContainer, 32, 98,  16, PINKCOIN.toStack()));
        this.addSlot(new PricingSlot(shopContainer, 33, 98 + slotSize,  16, GOLDCOIN.toStack()));
        this.addSlot(new PricingSlot(shopContainer, 34, 98 + 2 * slotSize,  16, SILVERCOIN.toStack()));
        this.addSlot(new PricingSlot(shopContainer, 35, 98 + 3 * slotSize,  16, COPPERCOIN.toStack()));
    }

    @Override
    public void slotsChanged(Container container) {
        if (blockEntity != null) {
            blockEntity.setChanged();
            if (blockEntity.getLevel() != null && !blockEntity.getLevel().isClientSide) {
                blockEntity.getLevel().sendBlockUpdated(blockEntity.getBlockPos(), blockEntity.getBlockState(), blockEntity.getBlockState(), 3);
            }
        }
        super.slotsChanged(container);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index){
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
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
        return player.isAlive();
    }

    public ShopBlockEntity getBlockEntity() {
        return blockEntity;
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
        Slot slot = this.getSlot(slotIndex);
        ItemStack stack = slot.getItem();
        if (button == 0) { // Left click
            if (stack.isEmpty()) {
                PricingSlot pricingSlot = (PricingSlot) slot;
                Item item = pricingSlot.getCurrencyType().getItem();
                slot.set(item.getDefaultInstance());
            } else {
                if(stack.getCount() <= 99) {
                    stack.grow(1);
                }
            }
        } else if (button == 1) { // Right click
            if (!stack.isEmpty()) {
                stack.shrink(1);
                if (stack.getCount() == 0) {
                    slot.set(ItemStack.EMPTY);
                    slot.setChanged();
                }
            }
        }
        this.slotsChanged(slot.container);
    }
}
