package net.fullstackjones.bigbraincurrency.registration;

import net.fullstackjones.bigbraincurrency.BigBrainCurrency;
import net.fullstackjones.bigbraincurrency.menu.BrainBankMenu;
import net.fullstackjones.bigbraincurrency.menu.MoneyPouchContainer;
import net.fullstackjones.bigbraincurrency.menu.ShopMenu;
import net.fullstackjones.bigbraincurrency.menu.SimpleShopMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, BigBrainCurrency.MODID);

    public static final Supplier<MenuType<MoneyPouchContainer>> MONEYPOUCHMENU =
            MENUS.register("moneypouch_menu", () -> new MenuType<>(MoneyPouchContainer::new, FeatureFlags.DEFAULT_FLAGS));

    public static final DeferredHolder<MenuType<?>, MenuType<SimpleShopMenu>> SIMPLESHOPMENU =
            registerMenuType("simpleshop_menu", SimpleShopMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<BrainBankMenu>> BRAINBANKMENU =
            registerMenuType("brainbank_menu", BrainBankMenu::new);

    private static <T extends AbstractContainerMenu> DeferredHolder<MenuType<?>, MenuType<T>> registerMenuType(String name, IContainerFactory<T> factory) {
        return MENUS.register(name, () -> IMenuTypeExtension.create(factory));
    }

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
