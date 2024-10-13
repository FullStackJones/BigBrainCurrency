package net.fullstackjones.bigbraincurrency.menu;

import net.fullstackjones.bigbraincurrency.block.entities.ShopBlockEntity;
import net.fullstackjones.bigbraincurrency.menu.customslots.CurrencySlot;
import net.fullstackjones.bigbraincurrency.menu.customslots.PricingSlot;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
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

        setupPriceing();
        this.addSlot(new PricingSlot(shopContainer, 32, 98,  16, PINKCOIN.toStack()));
        this.addSlot(new PricingSlot(shopContainer, 33, 98 + slotSize,  16, GOLDCOIN.toStack()));
        this.addSlot(new PricingSlot(shopContainer, 34, 98 + 2 * slotSize,  16, SILVERCOIN.toStack()));
        this.addSlot(new PricingSlot(shopContainer, 35, 98 + 3 * slotSize,  16, COPPERCOIN.toStack()));
    }

    private void setupPriceing(){
        if(shopContainer.getItem(32).isEmpty())
            shopContainer.setItem(32, PINKCOIN.toStack());
        if(shopContainer.getItem(33).isEmpty())
            shopContainer.setItem(33, GOLDCOIN.toStack());
        if(shopContainer.getItem(34).isEmpty())
            shopContainer.setItem(34, SILVERCOIN.toStack());
        if(shopContainer.getItem(35).isEmpty())
            shopContainer.setItem(35, COPPERCOIN.toStack());
    }

    @Override
    public void slotsChanged(Container container) {
        if (blockEntity != null) {
            blockEntity.setChanged();
        }
        super.slotsChanged(container);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int pIndex) {
        return null;
    }

    @Override
    public boolean stillValid(Player player) {
        return player.isAlive();
    }

    public ShopBlockEntity getBlockEntity() {
        return blockEntity;
    }
}
