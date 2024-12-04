package net.fullstackjones.bigbraincurrency;

import net.fullstackjones.bigbraincurrency.menu.BrainBankScreen;
import net.fullstackjones.bigbraincurrency.registration.*;
import net.fullstackjones.bigbraincurrency.entities.ShopBlockEntityRenderer;
import net.fullstackjones.bigbraincurrency.registration.ModAttachmentTypes;
import net.fullstackjones.bigbraincurrency.registration.ModMenus;
import net.fullstackjones.bigbraincurrency.menu.MoneyPouchScreen;
import net.fullstackjones.bigbraincurrency.menu.ShopScreen;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

import static net.fullstackjones.bigbraincurrency.registration.ModMenus.*;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(BigBrainCurrency.MODID)
public class BigBrainCurrency
{
    public static final String MODID = "bigbraincurrency";
    private static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "examplemod" namespace

    public BigBrainCurrency(IEventBus modEventBus, ModContainer modContainer)
    {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (ExampleMod) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG);
        NeoForge.EVENT_BUS.register(this);

        ModCreativeModeTabs.register(modEventBus);
        ModAttachmentTypes.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModMenus.register(modEventBus);
        ModLootModifiers.register(modEventBus);

    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {

    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {

    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {

        }

        @SubscribeEvent
        public static void registerBER(EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(ModBlockEntities.SHOP_ENTITY.get(), ShopBlockEntityRenderer::new);
        }

        @SubscribeEvent
        public static void registerScreens(RegisterMenuScreensEvent event) {
            event.register(MONEYPOUCHMENU.get(), MoneyPouchScreen::new);
            event.register(SHOPMENU.get(), ShopScreen::new);
            event.register(BRAINBANKMENU.get(), BrainBankScreen::new);
        }
    }
}
