package net.fullstackjones.bigbraincurrency;


import net.fullstackjones.bigbraincurrency.block.ModBlocks;
import net.fullstackjones.bigbraincurrency.item.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, BigBrainCurrency.MODID);

    public static final Supplier<CreativeModeTab> BIGBRAINCURRENCYTAB = CREATIVE_MODE_TAB.register(
            "bigbraincurrency_tab",
            () -> CreativeModeTab.builder().icon(() ->
                    new ItemStack(ModItems.GOLDCOIN.get()))
                    .title(Component.translatable("creativetab.bigbraincurrency.bigbraincurrency_items"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ModItems.COPPERCOIN);
                        output.accept(ModItems.SILVERCOIN);
                        output.accept(ModItems.GOLDCOIN);
                        output.accept(ModItems.PINKCOIN);
                        output.accept(ModItems.MONEY_POUCH);
                        output.accept(ModBlocks.PIGGYBANK_BLOCK);
                        output.accept(ModBlocks.SHOP_BLOCK);
                    }).build());

    public static void register(IEventBus eventBus){
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
