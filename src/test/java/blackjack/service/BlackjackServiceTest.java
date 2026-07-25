package blackjack.service;

import blackjack.dto.GameHistoryEntry;
import blackjack.dto.GameResponse;
import blackjack.entity.Game;
import blackjack.entity.GameRound;
import blackjack.entity.Transaction;
import blackjack.entity.User;
import blackjack.repository.CardRepository;
import blackjack.repository.GameRepository;
import blackjack.repository.GameRoundRepository;
import blackjack.repository.TransactionRepository;
import blackjack.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BlackjackServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private GameRoundRepository gameRoundRepository;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private TransactionRepository transactionRepository;

    private BlackjackService blackjackService;

    @BeforeEach
    void setUp() {
        blackjackService = new BlackjackService(
                userRepository,
                gameRepository,
                gameRoundRepository,
                cardRepository,
                transactionRepository
        );
    }

    @Test
    @DisplayName("startGame() throws when the user does not exist")
    void startGameThrowsWhenUserMissing() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> blackjackService.startGame("ghost", new BigDecimal("10")));
    }

    @Test
    @DisplayName("startGame() throws when the balance is too low, without touching the user's balance")
    void startGameThrowsWhenBalanceTooLow() {
        User user = new User();
        user.setUsername("player");
        user.setBalance(new BigDecimal("5.00"));

        when(userRepository.findByUsername("player")).thenReturn(Optional.of(user));

        assertThrows(RuntimeException.class, () -> blackjackService.startGame("player", new BigDecimal("10.00")));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("startGame() deducts the bet, records a BET transaction, and persists a new game and round")
    void startGameDeductsBetAndPersistsGame() {
        User user = new User();
        user.setUsername("player");
        user.setBalance(new BigDecimal("100.00"));

        when(userRepository.findByUsername("player")).thenReturn(Optional.of(user));

        ArgumentCaptor<Game> gameCaptor = ArgumentCaptor.forClass(Game.class);
        ArgumentCaptor<Transaction> txCaptor = ArgumentCaptor.forClass(Transaction.class);

        String gameId = blackjackService.startGame("player", new BigDecimal("10.00"));

        assertThat(user.getBalance()).isEqualByComparingTo(new BigDecimal("90.00"));

        verify(userRepository).save(user);
        verify(gameRepository).save(gameCaptor.capture());
        verify(gameRoundRepository).save(any(GameRound.class));
        verify(cardRepository).saveAll(argThat(cards -> StreamSupport.stream(cards.spliterator(), false).count() == 4));
        verify(transactionRepository).save(txCaptor.capture());

        assertEquals(gameId, gameCaptor.getValue().getGameId());
        assertThat(txCaptor.getValue().getAmount()).isEqualByComparingTo(new BigDecimal("-10.00"));
        assertEquals("BET", txCaptor.getValue().getType());
    }

    @Test
    @DisplayName("hit() busts the player when their hand is already at 20 and finishes the game")
    void hitBustsAndFinishesGame() {
        Game dbGame = new Game();
        dbGame.setGameId("game-1");
        dbGame.setStatus("RUNNING");
        dbGame.setBetAmount(new BigDecimal("10.00"));
        dbGame.setUser(new User());

        GameRound round = new GameRound();
        round.setGame(dbGame);
        round.setRoundNumber(1);

        when(gameRepository.findByGameId("game-1")).thenReturn(Optional.of(dbGame));
        when(gameRoundRepository.findTopByGameOrderByRoundNumberDesc(dbGame)).thenReturn(Optional.of(round));
        when(cardRepository.findByGameRound(round)).thenReturn(List.of(
                entityCard("Hearts", "K", 10),
                entityCard("Spades", "K", 10),
                entityCard("Clubs", "9", 9),
                entityCard("Diamonds", "8", 8)
        ));

        GameResponse response = blackjackService.hit("game-1");

        assertEquals("FINISHED", dbGame.getStatus());
        assertEquals("DEALER_WIN", dbGame.getResult());
        assertTrue(response.getPlayerValue() > 21);

        verify(cardRepository).saveAll(anyList());
    }



    @Test
    @DisplayName("stand() pays out double the bet and records a WIN transaction when the player wins")
    void standPlayerWinPaysOutAndRecordsTransaction() {
        User user = new User();
        user.setUsername("player");
        user.setBalance(new BigDecimal("90.00"));

        Game dbGame = new Game();
        dbGame.setGameId("game-2");
        dbGame.setStatus("RUNNING");
        dbGame.setBetAmount(new BigDecimal("10.00"));
        dbGame.setUser(user);

        GameRound round = new GameRound();
        round.setGame(dbGame);
        round.setRoundNumber(1);

        when(gameRepository.findByGameId("game-2")).thenReturn(Optional.of(dbGame));
        when(gameRoundRepository.findTopByGameOrderByRoundNumberDesc(dbGame)).thenReturn(Optional.of(round));
        when(cardRepository.findByGameRound(round)).thenReturn(List.of(
                entityCard("Hearts", "K", 10),
                entityCard("Spades", "K", 10),
                entityCard("Clubs", "K", 10),
                entityCard("Diamonds", "7", 7)
        ));

        blackjackService.stand("game-2");

        assertEquals("PLAYER_WIN", dbGame.getResult());
        assertThat(user.getBalance()).isEqualByComparingTo(new BigDecimal("110.00"));

        ArgumentCaptor<Transaction> txCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(txCaptor.capture());
        assertThat(txCaptor.getValue().getAmount()).isEqualByComparingTo(new BigDecimal("20.00"));
        assertEquals("WIN", txCaptor.getValue().getType());
    }

    @Test
    @DisplayName("getHistory() splits each game's cards by owner and sorts games newest first")
    void getHistoryReturnsGamesSortedNewestFirstWithSplitCards() {
        User user = new User();
        user.setUsername("player");

        Game olderGame = new Game();
        olderGame.setGameId("game-old");
        olderGame.setStatus("FINISHED");
        olderGame.setResult("PLAYER_WIN");
        olderGame.setBetAmount(new BigDecimal("10.00"));
        olderGame.setPlayerScore(20);
        olderGame.setDealerScore(18);
        olderGame.setUser(user);
        olderGame.setCreatedAt(LocalDateTime.now().minusDays(1));

        Game newerGame = new Game();
        newerGame.setGameId("game-new");
        newerGame.setStatus("FINISHED");
        newerGame.setResult("DEALER_WIN");
        newerGame.setBetAmount(new BigDecimal("20.00"));
        newerGame.setPlayerScore(15);
        newerGame.setDealerScore(19);
        newerGame.setUser(user);
        newerGame.setCreatedAt(LocalDateTime.now());

        GameRound olderRound = new GameRound();
        olderRound.setGame(olderGame);
        olderRound.setRoundNumber(1);

        GameRound newerRound = new GameRound();
        newerRound.setGame(newerGame);
        newerRound.setRoundNumber(1);

        when(userRepository.findByUsername("player")).thenReturn(Optional.of(user));
        when(gameRepository.findByUser(user)).thenReturn(List.of(olderGame, newerGame));
        when(gameRoundRepository.findTopByGameOrderByRoundNumberDesc(newerGame)).thenReturn(Optional.of(newerRound));
        when(gameRoundRepository.findTopByGameOrderByRoundNumberDesc(olderGame)).thenReturn(Optional.of(olderRound));
        when(cardRepository.findByGameRound(newerRound)).thenReturn(List.of(
                ownedCard("Hearts", "K", 10, "PLAYER"),
                ownedCard("Spades", "9", 9, "DEALER")
        ));
        when(cardRepository.findByGameRound(olderRound)).thenReturn(List.of(
                ownedCard("Clubs", "Q", 10, "PLAYER"),
                ownedCard("Diamonds", "8", 8, "DEALER")
        ));

        List<GameHistoryEntry> history = blackjackService.getHistory("player");

        assertEquals(2, history.size());
        assertEquals("game-new", history.get(0).getGameId());
        assertEquals("game-old", history.get(1).getGameId());
        assertEquals(List.of("K of Hearts"), history.get(0).getPlayerCards());
        assertEquals(List.of("9 of Spades"), history.get(0).getDealerCards());
    }

    private blackjack.entity.Card entityCard(String suit, String rank, int value) {
        blackjack.entity.Card card = new blackjack.entity.Card();
        card.setSuit(suit);
        card.setCardRank(rank);
        card.setValue(value);
        return card;
    }

    private blackjack.entity.Card ownedCard(String suit, String rank, int value, String owner) {
        blackjack.entity.Card card = entityCard(suit, rank, value);
        card.setOwner(owner);
        return card;
    }
}