package blackjack.game;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HandTest {

    @Test
    @DisplayName("A new hand has no cards and a value of 0")
    void newHandIsEmpty() {
        Hand hand = new Hand();

        assertTrue(hand.getCards().isEmpty());
        assertEquals(0, hand.getValue());
    }

    @Test
    @DisplayName("Adding cards sums their values")
    void addCardSumsValues() {
        Hand hand = new Hand();

        hand.addCard(new Card("Hearts", "K", 10));
        hand.addCard(new Card("Spades", "7", 7));

        assertEquals(17, hand.getValue());
        assertEquals(2, hand.getCards().size());
    }

    @Test
    @DisplayName("A hand with 21 or less is not busted")
    void handAt21IsNotBusted() {
        Hand hand = new Hand();

        hand.addCard(new Card("Hearts", "K", 10));
        hand.addCard(new Card("Spades", "A", 11));

        assertEquals(21, hand.getValue());
        assertFalse(hand.isBusted());
    }

    @Test
    @DisplayName("A hand over 21 is busted")
    void handOver21IsBusted() {
        Hand hand = new Hand();

        hand.addCard(new Card("Hearts", "K", 10));
        hand.addCard(new Card("Spades", "Q", 10));
        hand.addCard(new Card("Clubs", "5", 5));

        assertEquals(25, hand.getValue());
        assertTrue(hand.isBusted());
    }
}