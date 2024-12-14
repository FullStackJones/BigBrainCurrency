package net.fullstackjones.bigbraincurrency.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.time.LocalDateTime;
import java.util.UUID;

public class BrainBankDataCodec {
    public static final Codec<BrainBankData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("BankValue").forGetter(BrainBankData::getBankValue),
            Codec.BOOL.fieldOf("HadUbi").forGetter(BrainBankData::getHadUbi),
            Codec.STRING.xmap(LocalDateTime::parse, LocalDateTime::toString).fieldOf("UbiSetTime").forGetter(BrainBankData::getUbiSetTime)
    ).apply(instance, BrainBankData::new));

    public static final StreamCodec<ByteBuf, BrainBankData> BASIC_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, BrainBankData::getBankValue,
            ByteBufCodecs.BOOL, BrainBankData::getHadUbi,
            ByteBufCodecs.stringUtf8(36).map(LocalDateTime::parse, LocalDateTime::toString), BrainBankData::getUbiSetTime,
            BrainBankData::new
    );
}
