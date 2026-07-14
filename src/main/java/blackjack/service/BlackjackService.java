package blackjack.service;


import blackjack.dto.GameResponse;

import blackjack.entity.Card;
import blackjack.entity.Game;
import blackjack.entity.GameRound;
import blackjack.entity.User;

import blackjack.game.BlackjackGame;
import blackjack.game.GameResult;
import blackjack.game.GameState;

import blackjack.repository.CardRepository;
import blackjack.repository.GameRepository;
import blackjack.repository.GameRoundRepository;
import blackjack.repository.UserRepository;

import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;



@Service
public class BlackjackService {



    private final UserRepository userRepository;

    private final GameRepository gameRepository;

    private final GameRoundRepository gameRoundRepository;

    private final CardRepository cardRepository;



    public BlackjackService(
            UserRepository userRepository,
            GameRepository gameRepository,
            GameRoundRepository gameRoundRepository,
            CardRepository cardRepository
    ){

        this.userRepository = userRepository;

        this.gameRepository = gameRepository;

        this.gameRoundRepository = gameRoundRepository;

        this.cardRepository = cardRepository;

    }






    public String startGame(
            String username,
            BigDecimal bet
    ){


        User user =
                userRepository
                        .findByUsername(username)
                        .orElseThrow();




        if(
                user.getBalance()
                        .compareTo(bet) < 0
        ){

            throw new RuntimeException(
                    "Insufficient balance"
            );

        }





        user.setBalance(
                user.getBalance()
                        .subtract(bet)
        );


        userRepository.save(user);





        Game game =
                new Game();



        game.setGameId(
                UUID.randomUUID()
                        .toString()
        );



        game.setUser(user);



        game.setStatus(
                "RUNNING"
        );



        game.setBetAmount(
                bet
        );



        gameRepository.save(game);







        GameRound round =
                new GameRound();



        round.setGame(game);


        round.setRoundNumber(1);



        gameRoundRepository.save(round);






        BlackjackGame blackjackGame =
                new BlackjackGame();



        blackjackGame.getState()
                .setBet(
                        bet.doubleValue()
                );





        saveCards(
                round,
                blackjackGame
        );





        return game.getGameId();

    }










    public GameResponse hit(
            String gameId
    ){



        BlackjackGame game =
                loadGame(gameId);




        game.hit();





        saveCards(
                getRound(gameId),
                game
        );





        saveIfFinished(
                gameId,
                game.getState()
        );




        return response(
                game.getState()
        );

    }









    public GameResponse stand(
            String gameId
    ){



        BlackjackGame game =
                loadGame(gameId);





        game.stand();





        saveCards(
                getRound(gameId),
                game
        );






        saveIfFinished(
                gameId,
                game.getState()
        );





        return response(
                game.getState()
        );

    }










    public GameResponse getGame(
            String gameId
    ){



        BlackjackGame game =
                loadGame(gameId);




        return response(
                game.getState()
        );

    }









    private BlackjackGame loadGame(
            String gameId
    ){



        Game databaseGame =
                gameRepository
                        .findByGameId(gameId)
                        .orElseThrow(
                                () ->
                                        new RuntimeException(
                                                "Game not found"
                                        )
                        );





        BlackjackGame game =
                new BlackjackGame(false);





        GameRound round =
                getRound(gameId);





        List<Card> cards =
                cardRepository
                        .findByGameRound(round);





        game.restore(cards);





        game.getState()
                .setBet(
                        databaseGame
                                .getBetAmount()
                                .doubleValue()
                );





        return game;

    }









    private GameRound getRound(
            String gameId
    ){


        Game game =
                gameRepository
                        .findByGameId(gameId)
                        .orElseThrow();




        return gameRoundRepository
                .findByGame(game)
                .get(0);

    }









    private void saveCards(
            GameRound round,
            BlackjackGame game
    ){



        List<Card> cards =
                new ArrayList<>();





        game.getState()
                .getPlayer()
                .getCards()
                .forEach(
                        c -> {

                            Card card =
                                    convert(c);


                            card.setGameRound(round);


                            cards.add(card);

                        }
                );






        game.getState()
                .getDealer()
                .getCards()
                .forEach(
                        c -> {

                            Card card =
                                    convert(c);


                            card.setGameRound(round);


                            cards.add(card);

                        }
                );






        cardRepository.saveAll(cards);

    }









    private Card convert(
            blackjack.game.Card gameCard
    ){



        Card card =
                new Card();



        card.setSuit(
                gameCard.getSuit()
        );



        card.setCardRank(
                gameCard.getRank()
        );



        card.setValue(
                gameCard.getValue()
        );



        return card;

    }









    private void saveIfFinished(
            String gameId,
            GameState state
    ){



        if(
                state.getStatus()
                        != blackjack.game.GameStatus.FINISHED
        ){

            return;

        }






        Game game =
                gameRepository
                        .findByGameId(gameId)
                        .orElseThrow();





        game.setStatus(
                state.getStatus()
                        .name()
        );




        game.setPlayerScore(
                state.getPlayer()
                        .getValue()
        );




        game.setDealerScore(
                state.getDealer()
                        .getValue()
        );




        game.setResult(
                state.getResult()
                        .name()
        );





        gameRepository.save(game);





        payout(
                game.getUser(),
                state
        );

    }









    private void payout(
            User user,
            GameState state
    ){



        BigDecimal bet =
                BigDecimal.valueOf(
                        state.getBet()
                );





        if(
                state.getResult()
                        == GameResult.PLAYER_WIN
        ){


            user.setBalance(
                    user.getBalance()
                            .add(
                                    bet.multiply(
                                            BigDecimal.valueOf(2)
                                    )
                            )
            );

        }





        else if(
                state.getResult()
                        == GameResult.DRAW
        ){


            user.setBalance(
                    user.getBalance()
                            .add(bet)
            );

        }





        userRepository.save(user);

    }









    private GameResponse response(
            GameState state
    ){


        return new GameResponse(


                state.getPlayer()
                        .getCards()
                        .stream()
                        .map(Object::toString)
                        .toList(),



                state.getPlayer()
                        .getValue(),



                state.getDealer()
                        .getCards()
                        .stream()
                        .map(Object::toString)
                        .toList(),



                state.getDealer()
                        .getValue(),



                state.getStatus(),



                state.getResult()

        );

    }


}