package net.fullstackjones.bigbraincurrency.Utills;

import net.fullstackjones.bigbraincurrency.item.ModItems;
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

    public static ItemStack[] convertValueToCoins(int value) {
        var copper = value;
        if(copper > 99){
            value = copper - 99;
            copper = 99;
        }

        var silver = value / SILVER_VALUE;
        if(silver > 99){
            value = value - (99 * SILVER_VALUE);
            silver = 99;
        }

        var gold = value / GOLD_VALUE;
        if(gold > 99){
            value = value - (99 * GOLD_VALUE);
            gold = 99;
        }

        var pink = value / PINK_VALUE;
        if(99 * PINK_VALUE < value){
            pink = 99;
        }
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
