package blackjack.service;

import blackjack.entity.Card;

public class CardMapper {

    public static Card convert(blackjack.game.Card gameCard) {
        Card card = new Card();
        card.setSuit(gameCard.getSuit());
        card.setCardRank(gameCard.getRank());
        card.setValue(gameCard.getValue());
        return card;
    }
}