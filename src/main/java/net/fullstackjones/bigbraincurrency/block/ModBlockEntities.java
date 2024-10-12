package net.fullstackjones.bigbraincurrency.block;

import net.fullstackjones.bigbraincurrency.BigBrainCurrency;
import net.fullstackjones.bigbraincurrency.block.entities.ShopBlockEntity;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, BigBrainCurrency.MODID);

    public static final Supplier<BlockEntityType<ShopBlockEntity>> SHOPENTITY = BLOCK_ENTITIES.register(
            "shopentity",
            () -> BlockEntityType.Builder
                    .of(ShopBlockEntity::new, ModBlocks.SHOP_BLOCK.get())
                    .build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
