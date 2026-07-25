package blackjack.game;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DeckTest {

    @Test
    @DisplayName("A new deck has 52 cards")
    void newDeckHas52Cards() {
        Deck deck = new Deck();

        assertEquals(52, deck.size());
    }

    @Test
    @DisplayName("Drawing a card reduces the deck size by one")
    void drawCardReducesSize() {
        Deck deck = new Deck();

        deck.drawCard();

        assertEquals(51, deck.size());
    }

    @Test
    @DisplayName("All 52 cards can be drawn and their total value matches a standard deck")
    void drawingAllCardsMatchesExpectedTotalValue() {
        Deck deck = new Deck();
        int total = 0;

        for (int i = 0; i < 52; i++) {
            total += deck.drawCard().getValue();
        }

        assertEquals(0, deck.size());
        assertEquals(380, total);
    }

    @Test
    @DisplayName("Drawing from an empty deck throws")
    void drawingFromEmptyDeckThrows() {
        Deck deck = new Deck();

        for (int i = 0; i < 52; i++) {
            deck.drawCard();
        }

        assertThrows(IndexOutOfBoundsException.class, deck::drawCard);
    }
}