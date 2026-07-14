package blackjack.game;


public class GameState {


    private final Deck deck;

    private final Hand player;

    private final Hand dealer;


    private double bet;


    private GameResult result;

    private GameStatus status;


    public GameState() {

        this.deck = new Deck();

        this.player = new Hand();

        this.dealer = new Hand();

        this.status = GameStatus.ACTIVE;

        this.result = null;

    }


    public Deck getDeck() {

        return deck;

    }


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


    public GameResult getResult() {

        return result;

    }


    public void setResult(GameResult result) {

        this.result = result;

    }


    public GameStatus getStatus() {

        return status;

    }


    public void setStatus(GameStatus status) {

        this.status = status;

    }

}