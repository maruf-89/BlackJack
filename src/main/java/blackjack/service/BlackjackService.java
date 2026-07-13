package blackjack.service;

import blackjack.game.GameState;

import org.springframework.stereotype.Service;

@Service
public class BlackjackService {

    public GameState startGame() {

        GameState game = new GameState();

        game.getPlayer().addCard(
                game.getDeck().drawCard()
        );

        game.getDealer().addCard(
                game.getDeck().drawCard()
        );

        game.getPlayer().addCard(
                game.getDeck().drawCard()
        );

        game.getDealer().addCard(
                game.getDeck().drawCard()
        );

        return game;

    }

}