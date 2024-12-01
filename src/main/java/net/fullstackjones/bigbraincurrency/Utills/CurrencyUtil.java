package net.fullstackjones.bigbraincurrency.Utills;

import net.fullstackjones.bigbraincurrency.registration.ModItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class CurrencyUtil {
    private static final Integer SILVER_VALUE = 9;
    private static final Integer GOLD_VALUE = 81;
    private static final Integer PINK_VALUE = 729;
    private static final Integer MAX_VALUE = 99 + (SILVER_VALUE * 99) + (GOLD_VALUE * 99) + (PINK_VALUE * 99);

    public static int calculateTotalValue(int copperCoin, int silverCoin, int goldCoin, int pinkCoin) {
        int silverValue = silverCoin * SILVER_VALUE;
        int goldValue = goldCoin * GOLD_VALUE;
        int pinkValue = pinkCoin * PINK_VALUE;

        return copperCoin + silverValue + goldValue + pinkValue;
    }

    public static int getStackValue(ItemStack stack) {
        Item item = stack.getItem();
        int count = stack.getCount();

        if (stack.is(ModTags.Items.CURRENCY_ITEMS)) {
            if (item == ModItems.COPPERCOIN.get()) {
                return count;
            } else if (item == ModItems.SILVERCOIN.get()) {
                return count * SILVER_VALUE;
            } else if (item == ModItems.GOLDCOIN.get()) {
                return count * GOLD_VALUE;
            } else if (item == ModItems.PINKCOIN.get()) {
                return count * PINK_VALUE;
            }
        }
        return 0;
    }

    public static ItemStack[] convertValueToCoins(int value) {
        int pink = Math.min(value / PINK_VALUE, 99);
        value = value - pink * PINK_VALUE;

        int gold = Math.min(value / GOLD_VALUE, 99);
        value = value - gold * GOLD_VALUE;

        int silver = Math.min(value / SILVER_VALUE, 99);
        value = value - silver * SILVER_VALUE;

        int copper = value;

        ItemStack pinkCoinStack = new ItemStack(ModItems.PINKCOIN.get(), pink);
        ItemStack goldCoinStack = new ItemStack(ModItems.GOLDCOIN.get(), gold);
        ItemStack silverCoinStack = new ItemStack(ModItems.SILVERCOIN.get(), silver);
        ItemStack copperCoinStack = new ItemStack(ModItems.COPPERCOIN.get(), copper);

        return new ItemStack[]{copperCoinStack, silverCoinStack, goldCoinStack, pinkCoinStack};
    }

    public static Integer getMaxValue() {
        return MAX_VALUE;
    }
}
