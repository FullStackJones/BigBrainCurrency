package net.fullstackjones.bigbraincurrency.data;

import net.fullstackjones.bigbraincurrency.Utills.CurrencyUtil;

import java.time.Duration;
import java.time.LocalDateTime;

public class BankDetails {
    private final int copperCoins;
    private final int silverCoins;
    private final int goldCoins;
    private final int pinkCoins;
    private final LocalDateTime lastUBIPayment;
    private final String bankBalance;

    public BankDetails(int copperCoins, int silverCoins, int goldCoins, int pinkCoins, LocalDateTime lastUBIPayment) {
        this.copperCoins = copperCoins;
        this.silverCoins = silverCoins;
        this.goldCoins = goldCoins;
        this.pinkCoins = pinkCoins;
        this.lastUBIPayment = lastUBIPayment;
        this.bankBalance = String.format("Copper Coins: %d, Silver Coins: %d, Gold Coins: %d, Pink Coins: %d",
                copperCoins, silverCoins, goldCoins, pinkCoins);
    }

    public BankDetails update(int copperCoins, int silverCoins, int goldCoins, int pinkCoins, LocalDateTime lastUBIPayment) {
        return new BankDetails(copperCoins, silverCoins, goldCoins, pinkCoins, lastUBIPayment);
    }

    public BankDetails update(LocalDateTime lastUBIPayment) {
        return new BankDetails(copperCoins, silverCoins, goldCoins, pinkCoins, lastUBIPayment);
    }

    public BankDetails update(int copperCoins, int silverCoins, int goldCoins, int pinkCoins) {
        return new BankDetails(copperCoins, silverCoins, goldCoins, pinkCoins, lastUBIPayment);
    }

    public boolean getUBI(LocalDateTime now) {
        return Duration.between(getLastUpdated(),  now).toDays() >= 1;
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

    public String getBankBalance() {
        return bankBalance;
    }

    public int getBankBalanceValue() {
        return CurrencyUtil.calculateTotalValue(copperCoins, silverCoins, goldCoins, pinkCoins);
    }

    public LocalDateTime getLastUpdated() {
        return lastUBIPayment;
    }
}
