package blackjack.game;

public class GameState {


    private final Hand player =
            new Hand();


    private final Hand dealer =
            new Hand();


    private double bet;


    private GameStatus status =
            GameStatus.RUNNING;


    private GameResult result;


    public Hand getPlayer() {

        return player;

    }


    public Hand getDealer() {

        return dealer;

    }


    public double getBet() {

        return bet;

    }


    public void setBet(double bet) {

        this.bet = bet;

    }


    public GameStatus getStatus() {

        return status;

    }


    public void setStatus(GameStatus status) {

        this.status = status;

    }


    public GameResult getResult() {

        return result;

    }


    public void setResult(GameResult result) {

        this.result = result;

    }

}