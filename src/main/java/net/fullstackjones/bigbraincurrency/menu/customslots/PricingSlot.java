package net.fullstackjones.bigbraincurrency.menu.customslots;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class PricingSlot extends SlotItemHandler {
    private final ItemStack currencyType;

    private static final int MAX_STACK = 64;

    public PricingSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition, ItemStack currencyType) {
        super(itemHandler, index, xPosition, yPosition);
        this.currencyType = currencyType;
    }


    @Override
    public boolean mayPlace(ItemStack stack) {
        return false;
    }

    @Override
    public boolean mayPickup(Player player) {
        return false;
    }

    @Override
    public void onTake(Player player, ItemStack stack) {

    }

    @Override
    public int getMaxStackSize() {
        return MAX_STACK;
    }

    public ItemStack getCurrencyType() {
        return currencyType;
    }
}
