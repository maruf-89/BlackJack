package blackjack.game;


public class BlackjackGame {


    private final Deck deck =
            new Deck();


    private final GameState state =
            new GameState();



    public BlackjackGame() {


        state.getPlayer()
                .addCard(
                        deck.drawCard()
                );


        state.getDealer()
                .addCard(
                        deck.drawCard()
                );


        state.getPlayer()
                .addCard(
                        deck.drawCard()
                );


        state.getDealer()
                .addCard(
                        deck.drawCard()
                );

    }



    public void hit() {


        if(state.getStatus()
                != GameStatus.RUNNING) {

            return;

        }


        state.getPlayer()
                .addCard(
                        deck.drawCard()
                );


        if(state.getPlayer()
                .isBusted()) {


            state.setResult(
                    GameResult.DEALER_WIN
            );


            state.setStatus(
                    GameStatus.FINISHED
            );

        }

    }



    public void stand() {


        if(state.getStatus()
                != GameStatus.RUNNING) {

            return;

        }


        while(
                state.getDealer()
                        .getValue() < 17
        ){

            state.getDealer()
                    .addCard(
                            deck.drawCard()
                    );

        }


        calculateResult();

    }



    private void calculateResult() {


        int player =
                state.getPlayer()
                        .getValue();


        int dealer =
                state.getDealer()
                        .getValue();



        if(
                dealer > 21 ||
                        player > dealer
        ) {


            state.setResult(
                    GameResult.PLAYER_WIN
            );


        }
        else if(
                player == dealer
        ) {


            state.setResult(
                    GameResult.DRAW
            );


        }
        else {


            state.setResult(
                    GameResult.DEALER_WIN
            );

        }



        state.setStatus(
                GameStatus.FINISHED
        );

    }



    public GameState getState() {

        return state;

    }

}