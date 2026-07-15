package blackjack.dto;

import java.math.BigDecimal;

public class BalanceUpdateRequest {

    private BigDecimal balance;

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}