package blackjack.game;


import blackjack.entity.Card;

import java.util.List;



public class BlackjackGame {



    private final Deck deck =
            new Deck();



    private final GameState state =
            new GameState();





    public BlackjackGame(){

        startNewGame();

    }





    public BlackjackGame(boolean newGame){


        if(newGame){

            startNewGame();

        }

    }







    private void startNewGame(){


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









    public void restore(
            List<Card> cards
    ){


        state.getPlayer()
                .getCards()
                .clear();



        state.getDealer()
                .getCards()
                .clear();




        boolean playerTurn =
                true;



        int playerCards = 0;



        for(Card card : cards){


            blackjack.game.Card gameCard =
                    new blackjack.game.Card(
                            card.getSuit(),
                            card.getCardRank(),
                            card.getValue()
                    );



            /*
             * Vi sparar spelaren först
             * och dealern efter.
             *
             * Detta matchar saveCards()
             */


            if(playerCards < cards.size()/2){


                state.getPlayer()
                        .addCard(gameCard);


                playerCards++;


            }
            else {


                state.getDealer()
                        .addCard(gameCard);


            }


        }


    }









    public void hit(){


        if(
                state.getStatus()
                        != GameStatus.RUNNING
        ){

            return;

        }




        state.getPlayer()
                .addCard(
                        deck.drawCard()
                );





        if(
                state.getPlayer()
                        .isBusted()
        ){


            state.setResult(
                    GameResult.DEALER_WIN
            );



            state.setStatus(
                    GameStatus.FINISHED
            );


        }


    }









    public void stand(){



        if(
                state.getStatus()
                        != GameStatus.RUNNING
        ){

            return;

        }





        while(
                state.getDealer()
                        .getValue()
                        <17
        ){


            state.getDealer()
                    .addCard(
                            deck.drawCard()
                    );


        }




        calculateResult();


    }









    private void calculateResult(){



        int player =
                state.getPlayer()
                        .getValue();



        int dealer =
                state.getDealer()
                        .getValue();





        if(
                dealer >21 ||
                        player > dealer
        ){


            state.setResult(
                    GameResult.PLAYER_WIN
            );


        }



        else if(
                player == dealer
        ){


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








    public GameState getState(){

        return state;

    }


}