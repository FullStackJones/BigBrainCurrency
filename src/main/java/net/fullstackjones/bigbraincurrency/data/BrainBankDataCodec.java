package net.fullstackjones.bigbraincurrency.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.time.LocalDateTime;

public class BrainBankDataCodec {
    public static final Codec<BrainBankData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("BankValue").forGetter(BrainBankData::getBankValue),
            Codec.STRING.xmap(LocalDateTime::parse, LocalDateTime::toString).fieldOf("UBITimeStamp").forGetter(BrainBankData::getUBITimeStamp)
    ).apply(instance, BrainBankData::new));
}
