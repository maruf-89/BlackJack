package blackjack.repository;


import blackjack.entity.Card;
import blackjack.entity.GameRound;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface CardRepository
        extends JpaRepository<Card, Long> {


    List<Card> findByGameRound(GameRound gameRound);


}