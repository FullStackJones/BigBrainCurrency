package net.fullstackjones.bigbraincurrency.data;


import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.UnknownNullability;

import java.time.LocalDateTime;
import java.util.Objects;

public class BrainBankData implements INBTSerializable<CompoundTag> {
    protected int BankValue;
    protected boolean HadUbi;
    protected LocalDateTime UbiSetTime;

    public BrainBankData(int BankValue) {
        this(BankValue, true, LocalDateTime.now());
    }

    public BrainBankData(int BankValue, boolean HadUbi, LocalDateTime UbiSetTime) {
        this.BankValue = BankValue;
        this.HadUbi = HadUbi;
        this.UbiSetTime = UbiSetTime;
    }

    public int getBankValue() {
        return BankValue;
    }

    public boolean getHadUbi() {
        return HadUbi;
    }

    public LocalDateTime getUbiSetTime() {
        return UbiSetTime;
    }

    public void setHadUbi(boolean HadUbi) {
        this.HadUbi = HadUbi;
    }

    public void setBankValue(int BankValue) {
        this.BankValue = BankValue;
    }

    public void setUbiSetTime(LocalDateTime UbiSetTime) {
        this.UbiSetTime = UbiSetTime;
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("BankValue", BankValue);
        nbt.putBoolean("HadUbi", HadUbi);
        nbt.putBoolean("UbiSetTime", HadUbi);
        return nbt;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        setBankValue(nbt.getInt("BankValue"));
        setHadUbi(nbt.getBoolean("HadUbi"));
        setHadUbi(nbt.getBoolean("HadUbi"));
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getBankValue(), this.getUbiSetTime(), this.getHadUbi());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else {
            return obj instanceof BrainBankData ex
                    && this.getHadUbi() == ex.getHadUbi()
                    && this.getBankValue() == ex.getBankValue()
                    && this.getUbiSetTime() == ex.getUbiSetTime();
        }
    }
}
