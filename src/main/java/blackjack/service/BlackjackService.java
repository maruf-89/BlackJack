package blackjack.service;

import blackjack.game.GameResult;
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

    public GameResult stand(String gameId) {

        GameState game = games.get(gameId);

        if (game == null) {
            throw new RuntimeException("Game not found");
        }

        while (game.getDealer().getValue() < 17) {

            game.getDealer().addCard(
                    game.getDeck().drawCard()
            );

        }

        int playerValue = game.getPlayer().getValue();
        int dealerValue = game.getDealer().getValue();

        String result;

        if (playerValue > 21) {

            result = "PLAYER_BUST";

        } else if (dealerValue > 21) {

            result = "PLAYER_WIN";

        } else if (playerValue > dealerValue) {

            result = "PLAYER_WIN";

        } else if (dealerValue > playerValue) {

            result = "DEALER_WIN";

        } else {

            result = "PUSH";

        }

        games.remove(gameId);

        return new GameResult(
                game,
                result
        );

    }

}