package net.fullstackjones.bigbraincurrency.data;

import net.fullstackjones.bigbraincurrency.Utills.CurrencyUtil;

import java.time.Duration;
import java.time.LocalDateTime;

public class BankDetails {
    private final int copperCoins;
    private final int silverCoins;
    private final int goldCoins;
    private final int pinkCoins;
    private final int BankValue;
    private final String bankBalance;

    public BankDetails(int copperCoins, int silverCoins, int goldCoins, int pinkCoins) {
        this.copperCoins = copperCoins;
        this.silverCoins = silverCoins;
        this.goldCoins = goldCoins;
        this.pinkCoins = pinkCoins;
        this.BankValue = CurrencyUtil.calculateTotalValue(copperCoins, silverCoins, goldCoins, pinkCoins);
        this.bankBalance = String.format("Copper Coins: %d, Silver Coins: %d, Gold Coins: %d, Pink Coins: %d",
                copperCoins, silverCoins, goldCoins, pinkCoins);
    }

    public BankDetails update(int copperCoins, int silverCoins, int goldCoins, int pinkCoins) {
        return new BankDetails(copperCoins, silverCoins, goldCoins, pinkCoins);
    }

    public int getCopperCoins() {
        return copperCoins;
    }

    public int getSilverCoins() {
        return silverCoins;
    }

    public int getGoldCoins() {
        return goldCoins;
    }

    public int getPinkCoins() {
        return pinkCoins;
    }

    public int getBankValue() {
        return BankValue;
    }

    public int getBankBalanceValue() {
        return CurrencyUtil.calculateTotalValue(copperCoins, silverCoins, goldCoins, pinkCoins);
    }
}
