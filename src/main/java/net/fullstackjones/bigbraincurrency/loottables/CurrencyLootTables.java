package net.fullstackjones.bigbraincurrency.loottables;

import net.fullstackjones.bigbraincurrency.registration.ModItems;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class CurrencyLootTables {
    public static List<Supplier<ItemStack>> BASIC_LOOT = new ArrayList<>();
    public static List<Supplier<ItemStack>> UNCOMMON_LOOT = new ArrayList<>();
    public static List<Supplier<ItemStack>> RARE_LOOT = new ArrayList<>();

    public static Random r = new Random();

    static {
        BASIC_LOOT.add(() -> new ItemStack(ModItems.COPPERCOIN.get(), 1 + r.nextInt(5)));

        UNCOMMON_LOOT.add(() -> new ItemStack(ModItems.COPPERCOIN.get(), 1 + r.nextInt(9)));
        UNCOMMON_LOOT.add(() -> new ItemStack(ModItems.SILVERCOIN.get(), 1 + r.nextInt(1)));

        RARE_LOOT.add(() -> new ItemStack(ModItems.COPPERCOIN.get(), 5));
        RARE_LOOT.add(() -> new ItemStack(ModItems.SILVERCOIN.get(), 1 + r.nextInt(3)));
    }

    public static ItemStack getRandomItem(List<Supplier<ItemStack>> pool) {
        return pool.isEmpty() ? ItemStack.EMPTY : pool.get(r.nextInt(pool.size())).get();
    }

    public static List<ItemStack> getRandomRoll(BigBrainCurrencyLootModifier modifier) {
        List<ItemStack> stacks = new ArrayList<>();

        for (int i = 0; i < modifier.commonRolls; i++) {
            if (r.nextDouble() <= modifier.commonChance)
                stacks.add(getRandomItem(BASIC_LOOT));
        }

        for (int i = 0; i < modifier.uncommonRolls; i++) {
            if (r.nextDouble() <= modifier.uncommonChance)
                stacks.add(getRandomItem(UNCOMMON_LOOT));
        }

        for (int i = 0; i < modifier.rareRolls; i++) {
            if (r.nextDouble() <= modifier.rareChance)
                stacks.add(getRandomItem(RARE_LOOT));
        }
        return stacks;
    }
}
