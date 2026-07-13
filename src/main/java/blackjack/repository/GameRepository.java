package blackjack.repository;

import blackjack.entity.Game;
import blackjack.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameRepository extends JpaRepository<Game, Long> {

    List<Game> findByUser(User user);
}