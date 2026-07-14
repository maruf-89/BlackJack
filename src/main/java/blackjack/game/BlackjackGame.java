package blackjack.game;


public class BlackjackGame {


    private final GameState state;


    public BlackjackGame() {

        state = new GameState();

        dealInitialCards();

    }


    private void dealInitialCards() {

        state.getPlayer()
                .addCard(
                        state.getDeck().drawCard()
                );


        state.getDealer()
                .addCard(
                        state.getDeck().drawCard()
                );


        state.getPlayer()
                .addCard(
                        state.getDeck().drawCard()
                );


        state.getDealer()
                .addCard(
                        state.getDeck().drawCard()
                );

    }


    public void hit() {

        state.getPlayer()
                .addCard(
                        state.getDeck().drawCard()
                );


        if(state.getPlayer().isBusted()) {

            finish();

        }

    }


    public void stand() {


        while(state.getDealer().getValue() < 17) {

            state.getDealer()
                    .addCard(
                            state.getDeck().drawCard()
                    );

        }


        finish();

    }


    private void finish() {

        state.setResult(
                calculateResult()
        );


        state.setStatus(
                GameStatus.FINISHED
        );

    }


    private GameResult calculateResult() {


        int player =
                state.getPlayer().getValue();


        int dealer =
                state.getDealer().getValue();


        if(player > 21) {

            return GameResult.DEALER_WIN;

        }


        if(dealer > 21) {

            return GameResult.PLAYER_WIN;

        }


        if(player > dealer) {

            return GameResult.PLAYER_WIN;

        }


        if(dealer > player) {

            return GameResult.DEALER_WIN;

        }


        return GameResult.DRAW;

    }


    public GameState getState() {

        return state;

    }

}