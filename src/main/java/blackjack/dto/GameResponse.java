package blackjack.dto;

import blackjack.game.GameResult;
import blackjack.game.GameStatus;

import java.util.List;


public class GameResponse {


    private final List<String> playerCards;

    private final int playerValue;

    private final List<String> dealerCards;

    private final int dealerValue;

    private final GameStatus status;

    private final GameResult result;


    public GameResponse(
            List<String> playerCards,
            int playerValue,
            List<String> dealerCards,
            int dealerValue,
            GameStatus status,
            GameResult result
    ) {

        this.playerCards = playerCards;

        this.playerValue = playerValue;

        this.dealerCards = dealerCards;

        this.dealerValue = dealerValue;

        this.status = status;

        this.result = result;

    }


    public List<String> getPlayerCards() {

        return playerCards;

    }


    public int getPlayerValue() {

        return playerValue;

    }


    public List<String> getDealerCards() {

        return dealerCards;

    }


    public int getDealerValue() {

        return dealerValue;

    }


    public GameStatus getStatus() {

        return status;

    }


    public GameResult getResult() {

        return result;

    }

}