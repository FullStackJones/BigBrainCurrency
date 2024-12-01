package net.fullstackjones.bigbraincurrency.registration;

import com.mojang.serialization.MapCodec;
import net.fullstackjones.bigbraincurrency.BigBrainCurrency;
import net.fullstackjones.bigbraincurrency.loottables.BigBrainCurrencyLootModifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ModLootModifiers {
    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> GLM = DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, BigBrainCurrency.MODID);
    public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<BigBrainCurrencyLootModifier>> STRUCTURE_MODDED_LOOT_IMPORTER = GLM.register("coin_loot_modifier", () -> BigBrainCurrencyLootModifier.CODEC);

    public static void register(IEventBus eventBus) {
        GLM.register(eventBus);
    }
}
