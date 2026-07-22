package blackjack.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class GameHistoryEntry {

    private final String gameId;
    private final BigDecimal betAmount;
    private final String status;
    private final String result;
    private final int playerScore;
    private final int dealerScore;
    private final List<String> playerCards;
    private final List<String> dealerCards;
    private final LocalDateTime createdAt;

    public GameHistoryEntry(
            String gameId,
            BigDecimal betAmount,
            String status,
            String result,
            int playerScore,
            int dealerScore,
            List<String> playerCards,
            List<String> dealerCards,
            LocalDateTime createdAt
    ) {
        this.gameId = gameId;
        this.betAmount = betAmount;
        this.status = status;
        this.result = result;
        this.playerScore = playerScore;
        this.dealerScore = dealerScore;
        this.playerCards = playerCards;
        this.dealerCards = dealerCards;
        this.createdAt = createdAt;
    }

    public String getGameId() {
        return gameId;
    }

    public BigDecimal getBetAmount() {
        return betAmount;
    }

    public String getStatus() {
        return status;
    }

    public String getResult() {
        return result;
    }

    public int getPlayerScore() {
        return playerScore;
    }

    public int getDealerScore() {
        return dealerScore;
    }

    public List<String> getPlayerCards() {
        return playerCards;
    }

    public List<String> getDealerCards() {
        return dealerCards;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}