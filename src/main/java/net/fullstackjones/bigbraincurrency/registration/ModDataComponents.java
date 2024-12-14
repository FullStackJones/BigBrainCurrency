package net.fullstackjones.bigbraincurrency.registration;

import net.fullstackjones.bigbraincurrency.BigBrainCurrency;
import net.fullstackjones.bigbraincurrency.data.BrainBankData;
import net.fullstackjones.bigbraincurrency.data.BrainBankDataCodec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModDataComponents {
    public static final DeferredRegister.DataComponents REGISTRAR = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, BigBrainCurrency.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<BrainBankData>> BRAINBANK_COMPONENT = REGISTRAR.registerComponentType(
            "basic",
            builder -> builder
                    // The codec to read/write the data to disk
                    .persistent(BrainBankDataCodec.CODEC)
                    // The codec to read/write the data across the network
                    .networkSynchronized(BrainBankDataCodec.BASIC_STREAM_CODEC)
    );

    public static void register(IEventBus eventBus) {
        REGISTRAR.register(eventBus);
    }
}
