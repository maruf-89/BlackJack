package blackjack.repository;

import blackjack.entity.Game;
import blackjack.entity.GameRound;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GameRoundRepository
        extends JpaRepository<GameRound, Long> {

    List<GameRound> findByGame(Game game);

    Optional<GameRound> findTopByGameOrderByRoundNumberDesc(Game game);
}