package blackjack.service;


import blackjack.dto.GameResponse;
import blackjack.entity.Card;
import blackjack.entity.Game;
import blackjack.entity.GameRound;
import blackjack.entity.User;

import blackjack.game.BlackjackGame;
import blackjack.game.GameResult;
import blackjack.game.GameState;
import blackjack.repository.GameRepository;
import blackjack.repository.UserRepository;

import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;



@Service
public class BlackjackService {



    private final UserRepository userRepository;

    private final GameRepository gameRepository;



    private final Map<String, BlackjackGame> games =
            new ConcurrentHashMap<>();


    private final Map<String,String> players =
            new ConcurrentHashMap<>();




    public BlackjackService(
            UserRepository userRepository,
            GameRepository gameRepository
    ){

        this.userRepository = userRepository;

        this.gameRepository = gameRepository;

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




        BlackjackGame blackjackGame =
                new BlackjackGame();



        blackjackGame.getState()
                .setBet(
                        bet.doubleValue()
                );




        String gameId =
                UUID.randomUUID()
                        .toString();




        games.put(
                gameId,
                blackjackGame
        );


        players.put(
                gameId,
                username
        );



        return gameId;

    }







    public GameResponse hit(
            String gameId
    ){


        BlackjackGame game =
                findGame(gameId);



        game.hit();



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
                findGame(gameId);



        game.stand();



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


        return response(
                findGame(gameId)
                        .getState()
        );

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



        User user =
                userRepository
                        .findByUsername(
                                players.get(gameId)
                        )
                        .orElseThrow();




        Game game =
                new Game();


        game.setUser(user);


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


        game.setBetAmount(
                BigDecimal.valueOf(
                        state.getBet()
                )
        );



        game.setResult(
                state.getResult()
                        .name()
        );



        gameRepository.save(game);





        payout(
                user,
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








    private BlackjackGame findGame(
            String id
    ){


        BlackjackGame game =
                games.get(id);



        if(game == null){

            throw new RuntimeException(
                    "Game not found"
            );

        }


        return game;

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