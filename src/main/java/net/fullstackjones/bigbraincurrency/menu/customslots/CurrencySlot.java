package net.fullstackjones.bigbraincurrency.menu.customslots;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class CurrencySlot extends Slot {
    private final ItemStack coinType;

    public CurrencySlot(Container container, int index, int x, int y, ItemStack coinType) {
        super(container, index, x, y);
        this.coinType = coinType;
    }

    @Override
    public int getMaxStackSize() {
        return 99;
    }
    @Override
    public int getMaxStackSize(ItemStack stack) {
        return 99;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return stack.is(this.coinType.getItem()) ;
    }
}
