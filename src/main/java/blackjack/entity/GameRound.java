package blackjack.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Entity
@Table(name="game_rounds")
@Getter
@Setter
public class GameRound {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="game_id")
    private Game game;



    private int roundNumber;



    private String playerAction;



    private int playerScore;



    private int dealerScore;



    @OneToMany(
            mappedBy="gameRound",
            cascade = CascadeType.ALL
    )
    private List<Card> cards;

}