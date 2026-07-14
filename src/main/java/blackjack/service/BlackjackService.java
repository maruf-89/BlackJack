package blackjack.service;


import blackjack.dto.GameResponse;
import blackjack.entity.User;
import blackjack.game.BlackjackGame;
import blackjack.game.Card;
import blackjack.game.GameResult;
import blackjack.game.GameState;
import blackjack.repository.UserRepository;

import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


@Service
public class BlackjackService {


    private final UserRepository userRepository;


    private final Map<String, BlackjackGame> games =
            new ConcurrentHashMap<>();


    private final Map<String, String> players =
            new ConcurrentHashMap<>();


    public BlackjackService(
            UserRepository userRepository
    ) {

        this.userRepository =
                userRepository;

    }


    public String startGame(
            String username,
            BigDecimal bet
    ) {


        User user =
                userRepository
                        .findByUsername(username)
                        .orElseThrow();


        if(
                user.getBalance()
                        .compareTo(bet) < 0
        ) {

            throw new RuntimeException(
                    "Insufficient balance"
            );

        }


        user.setBalance(
                user.getBalance()
                        .subtract(bet)
        );


        userRepository.save(user);


        BlackjackGame game =
                new BlackjackGame();


        game.getState()
                .setBet(
                        bet.doubleValue()
                );


        String id =
                UUID.randomUUID()
                        .toString();


        games.put(
                id,
                game
        );


        players.put(
                id,
                username
        );


        return id;

    }


    public GameResponse hit(
            String id
    ) {


        BlackjackGame game =
                findGame(id);


        game.hit();


        payoutIfFinished(
                id,
                game.getState()
        );


        return response(
                game.getState()
        );

    }


    public GameResponse stand(
            String id
    ) {


        BlackjackGame game =
                findGame(id);


        game.stand();


        payoutIfFinished(
                id,
                game.getState()
        );


        return response(
                game.getState()
        );

    }


    public GameResponse getGame(
            String id
    ) {


        return response(
                findGame(id)
                        .getState()
        );

    }


    private void payoutIfFinished(
            String id,
            GameState state
    ) {


        if(
                state.getStatus()
                        != blackjack.game.GameStatus.FINISHED
        ) {

            return;

        }


        String username =
                players.get(id);


        User user =
                userRepository
                        .findByUsername(username)
                        .orElseThrow();


        BigDecimal bet =
                BigDecimal.valueOf(
                        state.getBet()
                );


        if(
                state.getResult()
                        == GameResult.PLAYER_WIN
        ) {


            user.setBalance(
                    user.getBalance()
                            .add(
                                    bet.multiply(
                                            BigDecimal.valueOf(2)
                                    )
                            )
            );

        }


        if(
                state.getResult()
                        == GameResult.DRAW
        ) {


            user.setBalance(
                    user.getBalance()
                            .add(bet)
            );

        }


        userRepository.save(user);

    }


    private BlackjackGame findGame(
            String id
    ) {


        BlackjackGame game =
                games.get(id);


        if(game == null) {

            throw new RuntimeException(
                    "Game not found"
            );

        }


        return game;

    }


    private GameResponse response(
            GameState state
    ) {


        return new GameResponse(

                state.getPlayer()
                        .getCards()
                        .stream()
                        .map(Card::toString)
                        .toList(),

                state.getPlayer()
                        .getValue(),

                state.getDealer()
                        .getCards()
                        .stream()
                        .map(Card::toString)
                        .toList(),

                state.getDealer()
                        .getValue(),

                state.getStatus(),

                state.getResult()

        );

    }

}