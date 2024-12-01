package net.fullstackjones.bigbraincurrency.data;


import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.UnknownNullability;

import java.time.LocalDateTime;

public class BrainBankData implements INBTSerializable<CompoundTag> {
    protected int BankValue;
    protected LocalDateTime UBITimeStamp;

    public BrainBankData(int BankValue, LocalDateTime UBITimeStamp) {
        this.BankValue = BankValue;
        this.UBITimeStamp = UBITimeStamp;
    }

    public int getBankValue() {
        return BankValue;
    }

    public void setBankValue(int BankValue) {
        this.BankValue = BankValue;
    }

    public void setUBITimeStamp(LocalDateTime ubiTimeStamp) {
        this.UBITimeStamp = ubiTimeStamp;
    }

    public LocalDateTime getUBITimeStamp() {
        return UBITimeStamp;
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag nbt = new CompoundTag();
        nbt.putString("UBITimeStamp", UBITimeStamp.toString());
        nbt.putInt("BankValue", BankValue);
        return nbt;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        setUBITimeStamp(LocalDateTime.parse(nbt.getString("UBITimeStamp")));
        setBankValue(nbt.getInt("BankValue"));
    }
}
