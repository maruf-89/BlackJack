package blackjack.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionResponse {

    private final Long id;
    private final BigDecimal amount;
    private final String type;
    private final LocalDateTime createdAt;

    public TransactionResponse(Long id, BigDecimal amount, String type, LocalDateTime createdAt) {
        this.id = id;
        this.amount = amount;
        this.type = type;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getType() {
        return type;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}