package net.fullstackjones.refactor;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
@EventBusSubscriber(modid = BigBrainCurrency.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Config
{
    public static final String CATEGORY_PIGGYBANK = "piggy bank";
    public static ModConfigSpec COMMON_CONFIG;
    public static ModConfigSpec.IntValue PINK_UBI_ALLOWANCE;
    public static ModConfigSpec.IntValue GOLD_UBI_ALLOWANCE;
    public static ModConfigSpec.IntValue SILVER_UBI_ALLOWANCE;
    public static ModConfigSpec.IntValue COPPER_UBI_ALLOWANCE;

    static{
        ModConfigSpec.Builder COMMON_BUILDER = new ModConfigSpec.Builder();
        COMMON_BUILDER.comment("General settings").push(CATEGORY_PIGGYBANK);
        PINK_UBI_ALLOWANCE = COMMON_BUILDER.comment("How Many Pink Coins is part of the UBI. [default: 0]")
                .defineInRange("pinkubiallowance", 0, 0, 64);
        GOLD_UBI_ALLOWANCE = COMMON_BUILDER.comment("How Many Gold Coins is part of the UBI. [default: 0]")
                .defineInRange("goldubiallowance", 0, 0, 64);
        SILVER_UBI_ALLOWANCE = COMMON_BUILDER.comment("How Many Silver Coins is part of the UBI. [default: 0]")
                .defineInRange("silverubiallowance", 0, 0, 64);
        COPPER_UBI_ALLOWANCE = COMMON_BUILDER.comment("How Many Copper Coins is part of the UBI. [default: 3]")
                .defineInRange("copperubiallowance", 3, 0, 64);
        COMMON_BUILDER.pop();
        COMMON_CONFIG = COMMON_BUILDER.build();
    }

    @SubscribeEvent
    public static void onLoad(final ModConfigEvent.Loading configEvent) {

    }
}
