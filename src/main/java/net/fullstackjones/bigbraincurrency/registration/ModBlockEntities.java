package net.fullstackjones.bigbraincurrency.registration;

import net.fullstackjones.bigbraincurrency.BigBrainCurrency;
import net.fullstackjones.bigbraincurrency.entities.BrainBankBlockEntity;
import net.fullstackjones.bigbraincurrency.entities.ShopBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, BigBrainCurrency.MODID);

    public static final Supplier<BlockEntityType<ShopBlockEntity>> SHOP_ENTITY = BLOCK_ENTITIES.register(
            "shopentity",
            () -> BlockEntityType.Builder
                    .of(ShopBlockEntity::new, ModBlocks.SHOP_BLOCK.get())
                    .build(null));

    public static final Supplier<BlockEntityType<BrainBankBlockEntity>> BRAINBANK_ENTITY = BLOCK_ENTITIES.register(
            "brainbankentity",
            () -> BlockEntityType.Builder
                    .of(BrainBankBlockEntity::new, ModBlocks.BRAINBANK_BLOCK.get())
                    .build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
