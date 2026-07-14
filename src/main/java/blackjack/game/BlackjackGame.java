package blackjack.game;

import blackjack.entity.Card;

import java.util.List;

public class BlackjackGame {

    private final Deck deck = new Deck();
    private final GameState state = new GameState();

    public BlackjackGame() {
        startNewGame();
    }

    public BlackjackGame(boolean newGame) {
        if (newGame) {
            startNewGame();
        }
    }

    private void startNewGame() {
        state.getPlayer().addCard(deck.drawCard());
        state.getDealer().addCard(deck.drawCard());
        state.getPlayer().addCard(deck.drawCard());
        state.getDealer().addCard(deck.drawCard());
    }

    public void restore(List<Card> cards) {
        state.getPlayer().getCards().clear();
        state.getDealer().getCards().clear();

        int split = cards.size() / 2;

        for (int i = 0; i < cards.size(); i++) {
            Card dbCard = cards.get(i);

            blackjack.game.Card card = new blackjack.game.Card(
                    dbCard.getSuit(),
                    dbCard.getCardRank(),
                    dbCard.getValue()
            );

            if (i < split) {
                state.getPlayer().addCard(card);
            } else {
                state.getDealer().addCard(card);
            }
        }
    }

    public void hit() {
        if (state.getStatus() != GameStatus.RUNNING) {
            return;
        }

        state.getPlayer().addCard(deck.drawCard());

        if (state.getPlayer().isBusted()) {
            state.setResult(GameResult.DEALER_WIN);
            state.setStatus(GameStatus.FINISHED);
        }
    }

    public void stand() {
        if (state.getStatus() != GameStatus.RUNNING) {
            return;
        }

        while (state.getDealer().getValue() < 17) {
            state.getDealer().addCard(deck.drawCard());
        }

        calculateResult();
    }

    private void calculateResult() {
        int player = state.getPlayer().getValue();
        int dealer = state.getDealer().getValue();

        if (dealer > 21 || player > dealer) {
            state.setResult(GameResult.PLAYER_WIN);
        } else if (player == dealer) {
            state.setResult(GameResult.DRAW);
        } else {
            state.setResult(GameResult.DEALER_WIN);
        }

        state.setStatus(GameStatus.FINISHED);
    }

    public GameState getState() {
        return state;
    }
}