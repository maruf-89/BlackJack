package blackjack.dto;


public class StartGameResponse {


    private final String gameId;


    public StartGameResponse(String gameId) {

        this.gameId = gameId;

    }

    public String getGameId() {

        return gameId;

    }

}