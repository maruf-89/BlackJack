package blackjack.game;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BlackjackGameTest {

    @Test
    @DisplayName("A new game deals two cards to the player and two to the dealer")
    void newGameDealsFourCards() {
        BlackjackGame game = new BlackjackGame();

        assertEquals(2, game.getState().getPlayer().getCards().size());
        assertEquals(2, game.getState().getDealer().getCards().size());
        assertEquals(GameStatus.RUNNING, game.getState().getStatus());
    }

    @Test
    @DisplayName("restore() splits the given cards evenly between player and dealer")
    void restoreSplitsCardsEvenly() {
        BlackjackGame game = new BlackjackGame(false);

        game.restore(List.of(
                entityCard("Hearts", "K", 10),
                entityCard("Spades", "7", 7),
                entityCard("Clubs", "9", 9),
                entityCard("Diamonds", "8", 8)
        ));

        assertEquals(2, game.getState().getPlayer().getCards().size());
        assertEquals(2, game.getState().getDealer().getCards().size());
        assertEquals(17, game.getState().getPlayer().getValue());
        assertEquals(17, game.getState().getDealer().getValue());
    }

    @Test
    @DisplayName("hit() adds exactly one card to the player's hand")
    void hitAddsOneCardToPlayer() {
        BlackjackGame game = new BlackjackGame(false);
        game.restore(List.of(
                entityCard("Hearts", "5", 5),
                entityCard("Spades", "5", 5),
                entityCard("Clubs", "9", 9),
                entityCard("Diamonds", "8", 8)
        ));

        game.hit();

        assertEquals(3, game.getState().getPlayer().getCards().size());
    }

    @Test
    @DisplayName("hit() busts and finishes the game when the player goes over 21")
    void hitBustsWhenOver21() {
        BlackjackGame game = new BlackjackGame(false);
        game.restore(List.of(
                entityCard("Hearts", "K", 10),
                entityCard("Spades", "K", 10),
                entityCard("Clubs", "9", 9),
                entityCard("Diamonds", "8", 8)
        ));

        game.hit();

        assertTrue(game.getState().getPlayer().isBusted());
        assertEquals(GameStatus.FINISHED, game.getState().getStatus());
        assertEquals(GameResult.DEALER_WIN, game.getState().getResult());
    }

    @Test
    @DisplayName("stand() lets the player win against a dealer already standing at 17 or more")
    void standPlayerWinsAgainstStandingDealer() {
        BlackjackGame game = new BlackjackGame(false);
        game.restore(List.of(
                entityCard("Hearts", "K", 10),
                entityCard("Spades", "K", 10),
                entityCard("Clubs", "K", 10),
                entityCard("Diamonds", "7", 7)
        ));

        game.stand();

        assertEquals(GameStatus.FINISHED, game.getState().getStatus());
        assertEquals(GameResult.PLAYER_WIN, game.getState().getResult());
    }

    @Test
    @DisplayName("stand() lets the dealer win when the dealer's value beats the player's")
    void standDealerWinsAgainstLowerPlayer() {
        BlackjackGame game = new BlackjackGame(false);
        game.restore(List.of(
                entityCard("Hearts", "K", 10),
                entityCard("Spades", "7", 7),
                entityCard("Clubs", "K", 10),
                entityCard("Diamonds", "K", 10)
        ));

        game.stand();

        assertEquals(GameStatus.FINISHED, game.getState().getStatus());
        assertEquals(GameResult.DEALER_WIN, game.getState().getResult());
    }

    @Test
    @DisplayName("stand() results in a draw when player and dealer values are equal")
    void standDrawsOnEqualValues() {
        BlackjackGame game = new BlackjackGame(false);
        game.restore(List.of(
                entityCard("Hearts", "K", 10),
                entityCard("Spades", "8", 8),
                entityCard("Clubs", "9", 9),
                entityCard("Diamonds", "9", 9)
        ));

        game.stand();

        assertEquals(GameStatus.FINISHED, game.getState().getStatus());
        assertEquals(GameResult.DRAW, game.getState().getResult());
    }

    @Test
    @DisplayName("stand() makes the dealer draw until reaching at least 17")
    void standDealerDrawsUntil17() {
        BlackjackGame game = new BlackjackGame(false);
        game.restore(List.of(
                entityCard("Hearts", "K", 10),
                entityCard("Spades", "8", 8),
                entityCard("Clubs", "2", 2),
                entityCard("Diamonds", "3", 3)
        ));

        game.stand();

        assertTrue(game.getState().getDealer().getValue() >= 17);
        assertEquals(GameStatus.FINISHED, game.getState().getStatus());
    }

    private blackjack.entity.Card entityCard(String suit, String rank, int value) {
        blackjack.entity.Card card = new blackjack.entity.Card();
        card.setSuit(suit);
        card.setCardRank(rank);
        card.setValue(value);
        return card;
    }
}