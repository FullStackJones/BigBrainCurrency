package net.fullstackjones.bigbraincurrency.data;

import net.fullstackjones.bigbraincurrency.BigBrainCurrency;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.time.LocalDateTime;
import java.util.function.Supplier;

public class ModAttachmentTypes {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, BigBrainCurrency.MODID);

    public static final Supplier<AttachmentType<BankDetails>> BANKDETAILS = ATTACHMENT_TYPES.register(
            "bankdetails", () -> AttachmentType.builder(() -> new BankDetails(0,0,0,0, LocalDateTime.now().minusDays(1))).serialize(BankDetailsCodec.CODEC).copyOnDeath().build()
    );

    public static void register(IEventBus eventBus) {
        ATTACHMENT_TYPES.register(eventBus);
    }
}
