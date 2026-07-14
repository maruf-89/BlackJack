package blackjack.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;



@Entity
@Table(name="cards")
@Getter
@Setter
public class Card {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



    private String suit;



    @Column(name="card_rank")
    private String rank;



    private int value;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="game_round_id")
    private GameRound gameRound;



}