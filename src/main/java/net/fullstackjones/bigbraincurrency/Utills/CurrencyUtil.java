package net.fullstackjones.bigbraincurrency.Utills;

import net.fullstackjones.bigbraincurrency.registration.ModItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class CurrencyUtil {
    public static final Integer SILVER_VALUE = 9;
    public static final Integer GOLD_VALUE = 81;
    public static final Integer PINK_VALUE = 729;
    public static final Integer MAX_VALUE = 64 + (SILVER_VALUE * 64) + (GOLD_VALUE * 64) + (PINK_VALUE * 64);

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
        int pink = Math.min(value / PINK_VALUE, 64);
        value = value - pink * PINK_VALUE;

        int gold = Math.min(value / GOLD_VALUE, 64);
        value = value - gold * GOLD_VALUE;

        int silver = Math.min(value / SILVER_VALUE, 64);
        value = value - silver * SILVER_VALUE;

        int copper = value;

        ItemStack pinkCoinStack = new ItemStack(ModItems.PINKCOIN.get(), pink);
        ItemStack goldCoinStack = new ItemStack(ModItems.GOLDCOIN.get(), gold);
        ItemStack silverCoinStack = new ItemStack(ModItems.SILVERCOIN.get(), silver);
        ItemStack copperCoinStack = new ItemStack(ModItems.COPPERCOIN.get(), copper);

        return new ItemStack[]{copperCoinStack, silverCoinStack, goldCoinStack, pinkCoinStack};
    }

    public static ItemStack getLargestCoin(int value) {
        if (value >= PINK_VALUE) {
            return new ItemStack(ModItems.PINKCOIN.get(), 1);
        } else if (value >= GOLD_VALUE) {
            return new ItemStack(ModItems.GOLDCOIN.get(), 1);
        } else if (value >= SILVER_VALUE) {
            return new ItemStack(ModItems.SILVERCOIN.get(), 1);
        } else if (value >= 1) {
            return new ItemStack(ModItems.COPPERCOIN.get(), 1);
        }
        return ItemStack.EMPTY;
    }

    public static int getCoinValue(ItemStack ItemStack) {
        if (ItemStack.getItem() == ModItems.PINKCOIN.asItem()) {
            return PINK_VALUE;
        } else if (ItemStack.getItem() == ModItems.GOLDCOIN.asItem()) {
           return GOLD_VALUE;
        } else if (ItemStack.getItem() == ModItems.SILVERCOIN.asItem()) {
            return SILVER_VALUE;
        } else {
            return 1;
        }
    }

    public static Integer getMaxValue() {
        return MAX_VALUE;
    }
}
