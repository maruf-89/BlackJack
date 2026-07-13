package blackjack.service;

import blackjack.game.GameState;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BlackjackService {

    private final Map<String, GameState> games =
            new ConcurrentHashMap<>();

    public String startGame() {

        GameState game = new GameState();

        game.getPlayer().addCard(game.getDeck().drawCard());
        game.getDealer().addCard(game.getDeck().drawCard());

        game.getPlayer().addCard(game.getDeck().drawCard());
        game.getDealer().addCard(game.getDeck().drawCard());

        String gameId = UUID.randomUUID().toString();

        games.put(gameId, game);

        return gameId;

    }

    public GameState getGame(String gameId) {

        return games.get(gameId);

    }

    public GameState hit(String gameId) {

        GameState game = games.get(gameId);

        if (game == null) {
            throw new RuntimeException("Game not found");
        }

        game.getPlayer().addCard(
                game.getDeck().drawCard()
        );

        return game;

    }

}