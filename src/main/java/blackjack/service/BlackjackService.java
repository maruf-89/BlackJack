package blackjack.service;

import blackjack.dto.GameResponse;
import blackjack.entity.Card;
import blackjack.entity.Game;
import blackjack.entity.GameRound;
import blackjack.entity.User;
import blackjack.game.BlackjackGame;
import blackjack.game.GameResult;
import blackjack.game.GameState;
import blackjack.game.GameStatus;
import blackjack.repository.CardRepository;
import blackjack.repository.GameRepository;
import blackjack.repository.GameRoundRepository;
import blackjack.repository.UserRepository;

import jakarta.transaction.Transactional;
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
    ) {
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
        this.gameRoundRepository = gameRoundRepository;
        this.cardRepository = cardRepository;
    }

    @Transactional
    public String startGame(String username, BigDecimal bet) {
        User user = userRepository.findByUsername(username).orElseThrow();

        if (user.getBalance().compareTo(bet) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        user.setBalance(user.getBalance().subtract(bet));
        userRepository.save(user);

        Game game = new Game();
        game.setGameId(UUID.randomUUID().toString());
        game.setUser(user);
        game.setStatus("RUNNING");
        game.setBetAmount(bet);
        gameRepository.save(game);

        GameRound round = new GameRound();
        round.setGame(game);
        round.setRoundNumber(1);
        gameRoundRepository.save(round);

        BlackjackGame blackjackGame = new BlackjackGame();
        blackjackGame.getState().setBet(bet.doubleValue());

        saveCards(round, blackjackGame);

        return game.getGameId();
    }

    @Transactional
    public GameResponse hit(String gameId) {
        BlackjackGame game = loadGame(gameId);
        validateRunning(game);

        game.hit();

        saveNewCards(getRound(gameId), game);
        saveIfFinished(gameId, game.getState());

        return response(game.getState());
    }

    @Transactional
    public GameResponse stand(String gameId) {
        BlackjackGame game = loadGame(gameId);
        validateRunning(game);

        game.stand();

        saveNewCards(getRound(gameId), game);
        saveIfFinished(gameId, game.getState());

        return response(game.getState());
    }

    public GameResponse getGame(String gameId) {
        BlackjackGame game = loadGame(gameId);
        return response(game.getState());
    }

    private BlackjackGame loadGame(String gameId) {
        Game databaseGame = gameRepository.findByGameId(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found"));

        BlackjackGame game = new BlackjackGame(false);

        GameRound round = getRound(gameId);
        List<Card> cards = cardRepository.findByGameRound(round);
        game.restore(cards);

        GameState state = game.getState();

        // Återställ data från Game-tabellen
        state.setBet(databaseGame.getBetAmount().doubleValue());

        if (databaseGame.getStatus().equals("FINISHED")) {
            state.setStatus(GameStatus.FINISHED);

            if (databaseGame.getResult() != null) {
                state.setResult(GameResult.valueOf(databaseGame.getResult()));
            }
        } else {
            state.setStatus(GameStatus.RUNNING);
        }

        return game;
    }

    private void validateRunning(BlackjackGame game) {
        if (game.getState().getStatus() == GameStatus.FINISHED) {
            throw new RuntimeException("Game already finished");
        }
    }

    private GameRound getRound(String gameId) {

        Game game = gameRepository
                .findByGameId(gameId)
                .orElseThrow();

        return gameRoundRepository
                .findTopByGameOrderByRoundNumberDesc(game)
                .orElseThrow(() ->
                        new RuntimeException("Round not found"));
    }

    private void saveCards(GameRound round, BlackjackGame game) {
        List<Card> cards = new ArrayList<>();

        game.getState().getPlayer().getCards().forEach(c -> {
            Card card = convert(c);
            card.setGameRound(round);
            card.setOwner("PLAYER");
            cards.add(card);
        });

        game.getState().getDealer().getCards().forEach(c -> {
            Card card = convert(c);
            card.setGameRound(round);
            card.setOwner("DEALER");
            cards.add(card);
        });

        cardRepository.saveAll(cards);
    }

    private void saveNewCards(GameRound round, BlackjackGame game) {
        List<Card> existing = cardRepository.findByGameRound(round);
        int existingCount = existing.size();

        List<Card> allCards = new ArrayList<>();

        game.getState().getPlayer().getCards().forEach(c -> {
            Card card = convert(c);
            card.setGameRound(round);
            card.setOwner("PLAYER");
            allCards.add(card);
        });

        game.getState().getDealer().getCards().forEach(c -> {
            Card card = convert(c);
            card.setGameRound(round);
            card.setOwner("DEALER");
            allCards.add(card);
        });

        if (allCards.size() <= existingCount) {
            return;
        }

        List<Card> newCards = allCards.subList(existingCount, allCards.size());
        cardRepository.saveAll(newCards);
    }

    private Card convert(blackjack.game.Card gameCard) {
        Card card = new Card();
        card.setSuit(gameCard.getSuit());
        card.setCardRank(gameCard.getRank());
        card.setValue(gameCard.getValue());
        return card;
    }

    private void saveIfFinished(String gameId, GameState state) {
        if (state.getStatus() != GameStatus.FINISHED) {
            return;
        }

        Game game = gameRepository.findByGameId(gameId).orElseThrow();

        game.setStatus("FINISHED");
        game.setPlayerScore(state.getPlayer().getValue());
        game.setDealerScore(state.getDealer().getValue());

        if (state.getResult() != null) {
            game.setResult(state.getResult().name());
        }

        gameRepository.save(game);

        payout(game.getUser(), state);
    }

    private void payout(User user, GameState state) {
        if (state.getResult() == null) {
            return;
        }

        BigDecimal bet = BigDecimal.valueOf(state.getBet());

        if (state.getResult() == GameResult.PLAYER_WIN) {
            user.setBalance(user.getBalance().add(bet.multiply(BigDecimal.valueOf(2))));
        } else if (state.getResult() == GameResult.DRAW) {
            user.setBalance(user.getBalance().add(bet));
        }

        userRepository.save(user);
    }

    private GameResponse response(GameState state) {
        return new GameResponse(
                state.getPlayer().getCards().stream().map(Object::toString).toList(),
                state.getPlayer().getValue(),
                state.getDealer().getCards().stream().map(Object::toString).toList(),
                state.getDealer().getValue(),
                state.getStatus(),
                state.getResult()
        );
    }
}