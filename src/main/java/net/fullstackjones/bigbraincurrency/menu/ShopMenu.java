package net.fullstackjones.bigbraincurrency.menu;

import net.fullstackjones.bigbraincurrency.block.ModBlocks;
import net.fullstackjones.bigbraincurrency.block.entities.ShopBlockEntity;
import net.fullstackjones.bigbraincurrency.menu.customslots.CurrencySlot;
import net.fullstackjones.bigbraincurrency.menu.customslots.PricingSlot;
import net.fullstackjones.bigbraincurrency.menu.customslots.ShopCurrecySlot;
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

        this.addSlot(new ShopCurrecySlot(this.blockEntity.shopItems, 27, 98,  56, PINKCOIN.toStack()));
        this.addSlot(new ShopCurrecySlot(this.blockEntity.shopItems, 28, 98 + slotSize,  56, GOLDCOIN.toStack()));
        this.addSlot(new ShopCurrecySlot(this.blockEntity.shopItems, 29, 98 + 2 * slotSize,  56, SILVERCOIN.toStack()));
        this.addSlot(new ShopCurrecySlot(this.blockEntity.shopItems, 30, 98 + 3 * slotSize,  56, COPPERCOIN.toStack()));

        this.addSlot(new SlotItemHandler(this.blockEntity.shopItems, 31, 8,  16));

        this.addSlot(new PricingSlot(this.blockEntity.shopItems, 32, 98,  16, PINKCOIN.toStack()));
        this.addSlot(new PricingSlot(this.blockEntity.shopItems, 33, 98 + slotSize,  16, GOLDCOIN.toStack()));
        this.addSlot(new PricingSlot(this.blockEntity.shopItems, 34, 98 + 2 * slotSize,  16, SILVERCOIN.toStack()));
        this.addSlot(new PricingSlot(this.blockEntity.shopItems, 35, 98 + 3 * slotSize,  16, COPPERCOIN.toStack()));
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
            if(stack.getCount() <= 8) {
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


    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;
    private static final int TE_INVENTORY_SLOT_COUNT = 32;  // must be the number of slots you have!
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
}
