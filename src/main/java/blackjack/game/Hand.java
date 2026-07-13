package blackjack.game;

import java.util.ArrayList;
import java.util.List;

public class Hand {

    private final List<Card> cards = new ArrayList<>();

    public void addCard(Card card) {

        cards.add(card);

    }

    public List<Card> getCards() {

        return cards;

    }

    public int getValue() {

        int value = 0;
        int aces = 0;

        for (Card card : cards) {

            value += card.getValue();

            if (card.getRank().equals("A")) {

                aces++;

            }

        }

        while (value > 21 && aces > 0) {

            value -= 10;
            aces--;

        }

        return value;

    }

}