package blackjack.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Entity
@Table(name="games")
@Getter
@Setter
public class Game {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



    @Column(nullable=false,unique=true)
    private String gameId;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;



    private String status;



    private int playerScore;



    private int dealerScore;



    private BigDecimal betAmount;



    private String result;



    private LocalDateTime createdAt;



    @OneToMany(
            mappedBy = "game",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<GameRound> rounds;



    @PrePersist
    public void createdAt(){

        createdAt = LocalDateTime.now();

    }

}