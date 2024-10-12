package net.fullstackjones.bigbraincurrency.menu;

import net.fullstackjones.bigbraincurrency.data.BankDetails;
import net.fullstackjones.bigbraincurrency.data.PlayerBankData;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

import java.time.LocalDateTime;

import static net.fullstackjones.bigbraincurrency.data.ModAttachmentTypes.BANKDETAILS;
import static net.fullstackjones.bigbraincurrency.item.ModItems.*;
import static net.fullstackjones.bigbraincurrency.menu.ModContainers.MONEYPOUCHMENU;

public class MoneyPouchContainer extends AbstractContainerMenu {

    protected final Inventory playerInv;
    private final PlayerBankData playerBankData;
    protected final int inventoryColumns = 9;
    protected final int inventoryRows = 4;
    protected final int slotSize = 18;


    public MoneyPouchContainer(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory,  new PlayerBankData(4,new BankDetails(0,0,0,0, LocalDateTime.now())));
    }

    public MoneyPouchContainer(int id, Inventory playerInventory, PlayerBankData playerBankData) {
        super(MONEYPOUCHMENU.get(), id);
        this.playerInv = playerInventory;
        this.playerBankData = playerBankData;

        for (int k = 0; k < inventoryRows; k++) {
            for (int l = 0; l < inventoryColumns; l++) {
                if (k > 0) {
                    this.addSlot(new Slot(playerInventory, l + k * inventoryColumns, 8 + l * slotSize, 23 + k * slotSize));
                } else {
                    this.addSlot(new Slot(playerInventory, l + k * inventoryColumns, 8 + l * slotSize, 23 + (4 * slotSize) + 4));
                }
            }
        }

        this.addSlot(new MoneyPouchSlot(playerBankData, 0, 8, 7, PINKCOIN.toStack()));
        this.addSlot(new MoneyPouchSlot(playerBankData, 1, 8 + (slotSize), 7, GOLDCOIN.toStack()));
        this.addSlot(new MoneyPouchSlot(playerBankData, 2, 8 + (2 * slotSize), 7, SILVERCOIN.toStack()));
        this.addSlot(new MoneyPouchSlot(playerBankData, 3, 8 + (3 * slotSize), 7, COPPERCOIN.toStack()));
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int quickMovedSlotIndex) {
        ItemStack quickMovedStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(quickMovedSlotIndex);

        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            quickMovedStack = stackInSlot.copy();

            // Check if the slot is in the player's inventory (index >= 4)
            if (quickMovedSlotIndex >= 4) {
                // Move from player inventory to money pouch
                if (!this.moveItemStackTo(stackInSlot, 0, 4, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // Move from money pouch to player inventory
                if (!this.moveItemStackTo(stackInSlot, 4, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stackInSlot.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return quickMovedStack;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (!player.level().isClientSide) {
            player.setData(BANKDETAILS, playerBankData.getBankDetails());
        }
    }
}