package net.fullstackjones.bigbraincurrency.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.time.LocalDateTime;

public class BankDetailsCodec {
    public static final Codec<BankDetails> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("copperCoins").forGetter(BankDetails::getCopperCoins),
            Codec.INT.fieldOf("silverCoins").forGetter(BankDetails::getSilverCoins),
            Codec.INT.fieldOf("goldCoins").forGetter(BankDetails::getGoldCoins),
            Codec.INT.fieldOf("pinkCoins").forGetter(BankDetails::getPinkCoins)
    ).apply(instance, BankDetails::new));
}