package blackjack.dto;

import java.math.BigDecimal;

public class StartGameRequest {

    private BigDecimal bet;


    public BigDecimal getBet() {

        return bet;

    }


    public void setBet(BigDecimal bet) {

        this.bet = bet;

    }

}