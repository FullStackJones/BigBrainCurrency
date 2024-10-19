package net.fullstackjones.bigbraincurrency.menu;

import net.fullstackjones.bigbraincurrency.data.BankDetails;
import net.fullstackjones.bigbraincurrency.data.PlayerBankData;
import net.fullstackjones.bigbraincurrency.menu.customslots.CurrencySlot;
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

        this.addSlot(new CurrencySlot(playerBankData, 0, 8, 7, PINKCOIN.toStack()));
        this.addSlot(new CurrencySlot(playerBankData, 1, 8 + (slotSize), 7, GOLDCOIN.toStack()));
        this.addSlot(new CurrencySlot(playerBankData, 2, 8 + (2 * slotSize), 7, SILVERCOIN.toStack()));
        this.addSlot(new CurrencySlot(playerBankData, 3, 8 + (3 * slotSize), 7, COPPERCOIN.toStack()));
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;
    private static final int TE_INVENTORY_SLOT_COUNT = 4;  // must be the number of slots you have!
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
    public void removed(Player player) {
        super.removed(player);
        if (!player.level().isClientSide) {
            player.setData(BANKDETAILS, playerBankData.getBankDetails());
        }
    }
}