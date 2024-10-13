package net.fullstackjones.bigbraincurrency;

import net.fullstackjones.bigbraincurrency.menu.MoneyPouchScreen;
import net.fullstackjones.bigbraincurrency.menu.ShopMenu;
import net.fullstackjones.bigbraincurrency.menu.ShopScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

import static net.fullstackjones.bigbraincurrency.menu.ModContainers.*;

public class ClientInit {

    public static void init(IEventBus modBus) {
        modBus.addListener(ClientInit::onRegisterMenuScreens);
    }

    private static void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
        event.register(MONEYPOUCHMENU.get(), MoneyPouchScreen::new);
        event.register(SHOPMENU.get(), (ShopMenu menu, Inventory inventory, Component title) -> new ShopScreen(menu, inventory, title));
    }
}
