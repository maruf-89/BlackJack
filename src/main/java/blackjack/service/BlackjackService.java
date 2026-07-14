package blackjack.service;

import blackjack.entity.Game;
import blackjack.entity.User;
import blackjack.game.GameState;
import blackjack.repository.GameRepository;
import blackjack.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


@Service
public class BlackjackService {


    private final GameRepository gameRepository;

    private final UserRepository userRepository;


    private final Map<String, GameState> games =
            new ConcurrentHashMap<>();


    public BlackjackService(
            GameRepository gameRepository,
            UserRepository userRepository
    ) {

        this.gameRepository = gameRepository;
        this.userRepository = userRepository;

    }


    public List<Game> getAllGames() {

        return gameRepository.findAll();

    }


    public Game saveGame(Game game) {

        return gameRepository.save(game);

    }


    public String startGame(
            String username,
            BigDecimal bet
    ) {


        User user = userRepository
                .findByUsername(username)
                .orElseThrow(
                        () -> new RuntimeException("User not found")
                );


        GameState state = new GameState();


        state.setBet(
                bet.doubleValue()
        );


        state.getPlayer()
                .addCard(state.getDeck().drawCard());


        state.getDealer()
                .addCard(state.getDeck().drawCard());


        state.getPlayer()
                .addCard(state.getDeck().drawCard());


        state.getDealer()
                .addCard(state.getDeck().drawCard());


        String gameId =
                UUID.randomUUID().toString();


        games.put(
                gameId,
                state
        );


        return gameId;

    }


    public GameState getGame(String gameId) {

        return games.get(gameId);

    }

}