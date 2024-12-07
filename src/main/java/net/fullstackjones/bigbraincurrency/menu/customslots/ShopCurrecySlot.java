package net.fullstackjones.bigbraincurrency.menu.customslots;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class ShopCurrecySlot  extends SlotItemHandler {
    private final ItemStack coinType;
    public ShopCurrecySlot(IItemHandler itemHandler, int index, int xPosition, int yPosition, ItemStack currencyType) {
        super(itemHandler, index, xPosition, yPosition);
        coinType = currencyType;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return stack.is(this.coinType.getItem()) ;
    }
}
