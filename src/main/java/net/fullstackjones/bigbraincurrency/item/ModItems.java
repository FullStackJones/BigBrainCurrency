package net.fullstackjones.bigbraincurrency.item;

import net.fullstackjones.bigbraincurrency.BigBrainCurrency;
import net.fullstackjones.bigbraincurrency.item.custom.MoneyPouchItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(BigBrainCurrency.MODID);

    public static final DeferredItem<Item> COPPERCOIN = ITEMS.register(
            "coppercoin",
            () -> new Item(new Item.Properties().stacksTo(128)));
    public static final DeferredItem<Item> SILVERCOIN = ITEMS.register(
            "silvercoin",
            () -> new Item(new Item.Properties().stacksTo(128)));
    public static final DeferredItem<Item> GOLDCOIN = ITEMS.register(
            "goldcoin",
            () -> new Item(new Item.Properties().stacksTo(128)));
    public static final DeferredItem<Item> PINKCOIN = ITEMS.register(
            "pinkcoin",
            () -> new Item(new Item.Properties().stacksTo(128)));
    public static final DeferredItem<Item> MONEY_POUCH = ITEMS.register(
            "moneypouch",
            () -> new MoneyPouchItem(new Item.Properties()
                    .stacksTo(1)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
