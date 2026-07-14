package blackjack.game;


import java.util.ArrayList;
import java.util.List;


public class Hand {



    private final List<Card> cards =
            new ArrayList<>();




    public void addCard(Card card){

        cards.add(card);

    }




    public List<Card> getCards(){

        return cards;

    }





    public int getValue(){

        return cards.stream()
                .mapToInt(Card::getValue)
                .sum();

    }





    public boolean isBusted(){

        return getValue()>21;

    }


}