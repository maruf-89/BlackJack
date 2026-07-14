package blackjack.game;

public class GameState {

    private final Deck deck;

    private final Hand player;

    private final Hand dealer;

    private double bet;


    public GameState() {

        this.deck = new Deck();
        this.player = new Hand();
        this.dealer = new Hand();

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

}