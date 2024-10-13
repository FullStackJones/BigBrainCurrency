package net.fullstackjones.bigbraincurrency.menu.customslots;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class PricingSlot extends Slot {
    private final ItemStack currencyType;

    private static final int MAX_STACK = 128;

    public PricingSlot(Container container, int slot, int x, int y, ItemStack currencyType) {
        super(container, slot, x, y);
        this.currencyType = currencyType;
    }

    @Override
    public void setChanged() {
        super.setChanged();
    }

    @Override
    public void set(ItemStack pStack) {
        super.set(pStack);
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
