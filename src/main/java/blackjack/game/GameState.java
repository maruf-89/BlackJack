package blackjack.game;

public class GameState {

    private final Deck deck;

    private final Hand player;

    private final Hand dealer;

    public GameState() {

        deck = new Deck();
        player = new Hand();
        dealer = new Hand();

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

}