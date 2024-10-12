package net.fullstackjones.bigbraincurrency;

import net.fullstackjones.bigbraincurrency.menu.MoneyPouchScreen;
import net.fullstackjones.bigbraincurrency.menu.ShopScreen;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

import static net.fullstackjones.bigbraincurrency.menu.ModContainers.*;

public class ClientInit {

    public static void init(IEventBus modBus) {
        modBus.addListener(ClientInit::onRegisterMenuScreens);
    }

    private static void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
        event.register(MONEYPOUCHMENU.get(), MoneyPouchScreen::new);
        event.register(SHOPMENU.get(), ShopScreen::new);
    }
}
