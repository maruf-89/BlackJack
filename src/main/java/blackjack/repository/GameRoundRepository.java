package blackjack.repository;

import blackjack.entity.GameRound;
import blackjack.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameRoundRepository extends JpaRepository<GameRound, Long> {

    List<GameRound> findByGame(Game game);
}