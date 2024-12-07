package net.fullstackjones.bigbraincurrency;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
@EventBusSubscriber(modid = BigBrainCurrency.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Config
{
    public static final String CATEGORY_UBI = "UBI";
    public static ModConfigSpec COMMON_CONFIG;
    public static ModConfigSpec.IntValue UBI_ALLOWANCE;

    static{
        ModConfigSpec.Builder COMMON_BUILDER = new ModConfigSpec.Builder();
        COMMON_BUILDER.comment("General settings").push(CATEGORY_UBI);
        UBI_ALLOWANCE = COMMON_BUILDER.comment("How much should the UBI be. [default: 3]")
                .defineInRange("ubiallowance", 3, 0, 64);
        COMMON_BUILDER.pop();
        COMMON_CONFIG = COMMON_BUILDER.build();
    }

    @SubscribeEvent
    public static void onLoad(final ModConfigEvent.Loading configEvent) {

    }
}
