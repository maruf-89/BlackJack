package blackjack.game;

public class GameResult {

    private final GameState game;

    private final String result;

    public GameResult(
            GameState game,
            String result
    ) {

        this.game = game;
        this.result = result;

    }

    public GameState getGame() {

        return game;

    }

    public String getResult() {

        return result;

    }

}