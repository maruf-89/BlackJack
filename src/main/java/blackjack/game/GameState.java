package blackjack.game;

public class GameState {

    private final Deck deck = new Deck();

    private final Hand player = new Hand();

    private final Hand dealer = new Hand();

    private double bet;

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