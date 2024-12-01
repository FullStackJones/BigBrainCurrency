package net.fullstackjones.bigbraincurrency.data;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

import static net.fullstackjones.bigbraincurrency.registration.ModItems.*;

public class PlayerBankData extends SimpleContainer {
    private BankDetails bankDetails;

    public PlayerBankData(int size, BankDetails bankDetails) {
        super(size);
        this.bankDetails = bankDetails;
        setItem(0, new ItemStack(PINKCOIN.get(), bankDetails.getPinkCoins()));
        setItem(1, new ItemStack(GOLDCOIN.get(), bankDetails.getGoldCoins()));
        setItem(2, new ItemStack(SILVERCOIN.get(), bankDetails.getSilverCoins()));
        setItem(3, new ItemStack(COPPERCOIN.get(), bankDetails.getCopperCoins()));
    }

    public BankDetails getBankDetails() {
        return bankDetails;
    }

    public BankDetails getMoneyPouchData() {
        return bankDetails;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        bankDetails = bankDetails.update(
                getItem(3).getCount(),
                getItem(2).getCount(),
                getItem(1).getCount(),
                getItem(0).getCount());
    }

    @Override
    public int getMaxStackSize(ItemStack pStack) {
        return 128;
    }

    @Override
    public int getMaxStackSize() {
        return 128;
    }
}
