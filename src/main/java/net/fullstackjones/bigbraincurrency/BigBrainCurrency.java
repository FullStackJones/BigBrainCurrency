package net.fullstackjones.bigbraincurrency;

import net.fullstackjones.bigbraincurrency.block.ModBlockEntities;
import net.fullstackjones.bigbraincurrency.block.ModBlocks;
import net.fullstackjones.bigbraincurrency.block.entities.ShopBlockEntityRenderer;
import net.fullstackjones.bigbraincurrency.data.ModAttachmentTypes;
import net.fullstackjones.bigbraincurrency.item.ModItems;
import net.fullstackjones.bigbraincurrency.menu.ModContainers;
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
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

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
        ClientInit.init(modEventBus);
        ModAttachmentTypes.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModContainers.register(modEventBus);

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
            event.enqueueWork(() -> {
                BlockEntityRenderers.register(ModBlockEntities.SHOPENTITY.get(), ShopBlockEntityRenderer::new);
            });
        }
    }
}
