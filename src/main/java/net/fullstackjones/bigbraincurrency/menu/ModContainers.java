package net.fullstackjones.bigbraincurrency.menu;

import net.fullstackjones.bigbraincurrency.BigBrainCurrency;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModContainers {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, BigBrainCurrency.MODID);

    public static final Supplier<MenuType<MoneyPouchContainer>> MoneyPouchMenu = MENUS.register("my_menu", () -> new MenuType(MoneyPouchContainer::new, FeatureFlags.DEFAULT_FLAGS));

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
