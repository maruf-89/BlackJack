package blackjack.game;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Deck {


    private final List<Card> cards = new ArrayList<>();


    public Deck() {

        String[] suits = {
                "Hearts",
                "Diamonds",
                "Clubs",
                "Spades"
        };


        String[] ranks = {
                "2",
                "3",
                "4",
                "5",
                "6",
                "7",
                "8",
                "9",
                "10",
                "J",
                "Q",
                "K",
                "A"
        };


        for(String suit : suits) {

            for(String rank : ranks) {


                int value;


                switch(rank) {

                    case "J":
                    case "Q":
                    case "K":
                        value = 10;
                        break;


                    case "A":
                        value = 11;
                        break;


                    default:
                        value = Integer.parseInt(rank);

                }


                cards.add(
                        new Card(
                                suit,
                                rank,
                                value
                        )
                );

            }

        }


        shuffle();

    }


    public void shuffle() {

        Collections.shuffle(cards);

    }


    public Card drawCard() {

        return cards.remove(0);

    }


    public int size() {

        return cards.size();

    }

}