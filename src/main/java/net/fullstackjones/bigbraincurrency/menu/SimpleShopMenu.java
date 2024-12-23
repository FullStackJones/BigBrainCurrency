package net.fullstackjones.bigbraincurrency.menu;

import net.fullstackjones.bigbraincurrency.Utills.CurrencyUtil;
import net.fullstackjones.bigbraincurrency.data.BaseShopData;
import net.fullstackjones.bigbraincurrency.entities.BrainBankBlockEntity;
import net.fullstackjones.bigbraincurrency.entities.SimpleShopBlockEntity;
import net.fullstackjones.bigbraincurrency.registration.ModBlocks;
import net.fullstackjones.bigbraincurrency.registration.ModMenus;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

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
            public void setItem(int index, ItemStack stack) {
                super.setItem(index, stack);
                if(index == 0){
                    SimpleShopMenu.this.blockEntity.setItem(stack.getItem());
                    SimpleShopMenu.this.blockEntity.setStockQuantity(stack.getCount());
                    SimpleShopMenu.this.blockEntity.setItemData(stack);
                }
                this.setChanged();
            }

            @Override
            public ItemStack removeItem(int index, int count) {
                BaseShopData data = SimpleShopMenu.this.blockEntity.data;
                if(index == 0){
                    int quantity = data.getStockQuantity();
                    quantity -= count;
                    SimpleShopMenu.this.blockEntity.setStockQuantity(quantity);
                }

                if(index == 1){
                    int profit = data.getProfit();
                    ItemStack coin = this.getItem(1);
                    int coinValue = CurrencyUtil.getStackValue(coin);
                    SimpleShopMenu.this.blockEntity.setProfit(profit - coinValue);
                }
                return super.removeItem(index, count);
            }

            @Override
            public int getMaxStackSize() {
                return 1024;
            }

            @Override
            public int getMaxStackSize(ItemStack pStack) {
                return 1024;
            }
        };

        shopInventory.setItem(1,
                CurrencyUtil.getLargestCoin(this.blockEntity.data.getProfit()));

        ItemStack stockItem = this.blockEntity.getItemStack();
        stockItem.setCount(this.blockEntity.data.getStockQuantity());

        shopInventory.setItem(0,stockItem);

        addPlayerInventory();
        addShopSlots();
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

    private void addShopSlots(){
        // stock slot
        this.addSlot(new Slot(shopInventory, 0, 21 + slotSize,  slotSize + 41){
            @Override
            public int getMaxStackSize() {
                return 1024;
            }

            @Override
            public int getMaxStackSize(ItemStack pStack) {
                return 1024;
            }
        });
        // profit slot
        this.addSlot(new Slot(shopInventory, 1, 21 + slotSize,  slotSize + 84){
            @Override
            public boolean mayPlace(ItemStack pStack) {
                return false;
            }

            @Override
            public void onTake(Player pPlayer, ItemStack pStack) {
                super.onTake(pPlayer, pStack);
                shopInventory.setItem(1, CurrencyUtil.getLargestCoin(blockEntity.data.getProfit()));
            }
        });
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
    protected boolean moveItemStackTo(ItemStack pStack, int pStartIndex, int pEndIndex, boolean pReverseDirection) {
        boolean flag = false;
        int i = pStartIndex;
        if (pReverseDirection) {
            i = pEndIndex - 1;
        }

        if (pStack.isStackable() || pEndIndex >= TE_INVENTORY_FIRST_SLOT_INDEX) {
            while (!pStack.isEmpty() && (pReverseDirection ? i >= pStartIndex : i < pEndIndex)) {
                Slot slot = this.slots.get(i);
                ItemStack itemstack = slot.getItem();
                if (!itemstack.isEmpty() && ItemStack.isSameItemSameComponents(pStack, itemstack)) {
                    int j = itemstack.getCount() + pStack.getCount();
                    int k = slot.getMaxStackSize(itemstack);
                    if (j <= k) {
                        pStack.setCount(0);
                        itemstack.setCount(j);
                        if(pEndIndex >= TE_INVENTORY_FIRST_SLOT_INDEX){
                            this.blockEntity.setStockQuantity(j);
                        }
                        slot.setChanged();
                        flag = true;
                    } else if (itemstack.getCount() < k) {
                        pStack.shrink(k - itemstack.getCount());
                        itemstack.setCount(k);
                        slot.setChanged();
                        flag = true;
                    }
                }

                if (pReverseDirection) {
                    i--;
                } else {
                    i++;
                }
            }
        }

        if (!pStack.isEmpty()) {
            if (pReverseDirection) {
                i = pEndIndex - 1;
            } else {
                i = pStartIndex;
            }

            while (pReverseDirection ? i >= pStartIndex : i < pEndIndex) {
                Slot slot1 = this.slots.get(i);
                ItemStack itemstack1 = slot1.getItem();
                if (itemstack1.isEmpty() && slot1.mayPlace(pStack)) {
                    int l = slot1.getMaxStackSize(pStack);
                    slot1.setByPlayer(pStack.split(Math.min(pStack.getCount(), l)));
                    slot1.setChanged();
                    flag = true;
                    break;
                }

                if (pReverseDirection) {
                    i--;
                } else {
                    i++;
                }
            }
        }

        return flag;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), playerInventory.player, ModBlocks.SHOP_BLOCK.get());
    }

    @Override
    public boolean clickMenuButton(Player pPlayer, int pId) {
        switch (pId){
            case 0:
                this.blockEntity.setPrice(this.blockEntity.data.getPrice() + 1);
                break;
            case 1:
                if(this.blockEntity.data.getPrice() > 0){
                    this.blockEntity.setPrice(this.blockEntity.data.getPrice() - 1);
                }
                break;
            case 2:
                this.blockEntity.setPrice(this.blockEntity.data.getPrice() + CurrencyUtil.SILVER_VALUE);
                break;
            case 3:
                if(this.blockEntity.data.getPrice() > 0){
                    if(this.blockEntity.data.getPrice() >= CurrencyUtil.SILVER_VALUE) {
                        this.blockEntity.setPrice(this.blockEntity.data.getPrice() - CurrencyUtil.SILVER_VALUE);
                    }
                }
                break;
            case 4:
                this.blockEntity.setPrice(this.blockEntity.data.getPrice() + CurrencyUtil.GOLD_VALUE);
                break;
            case 5:
                if(this.blockEntity.data.getPrice() > 0) {
                    if(this.blockEntity.data.getPrice() >= CurrencyUtil.GOLD_VALUE) {
                        this.blockEntity.setPrice(this.blockEntity.data.getPrice() - CurrencyUtil.GOLD_VALUE);
                    }
                }
                break;
            case 6:
                this.blockEntity.setPrice(this.blockEntity.data.getPrice() + CurrencyUtil.PINK_VALUE);
                break;
            case 7:
                if(this.blockEntity.data.getPrice() > 0) {
                    if(this.blockEntity.data.getPrice() >= CurrencyUtil.PINK_VALUE){
                        this.blockEntity.setPrice(this.blockEntity.data.getPrice() - CurrencyUtil.PINK_VALUE);
                    }
                }
                break;
            case 8:
                if(this.blockEntity.data.getSaleQuantity() > 0) {
                    this.blockEntity.setSaleQuantity(this.blockEntity.data.getSaleQuantity() - 1);
                }
                break;
            case 9:
                this.blockEntity.setSaleQuantity(this.blockEntity.data.getSaleQuantity() + 1);
                break;
        }
        return super.clickMenuButton(pPlayer, pId);
    }
}
