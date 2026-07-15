package blackjack.dto;

import java.math.BigDecimal;

public class HighscoreEntry {

    private final String username;
    private final BigDecimal balance;

    public HighscoreEntry(String username, BigDecimal balance) {
        this.username = username;
        this.balance = balance;
    }

    public String getUsername() {
        return username;
    }

    public BigDecimal getBalance() {
        return balance;
    }
}