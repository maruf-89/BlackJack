package blackjack.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "games")
@Getter
@Setter
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;


    @Column(nullable = false)
    private String status;


    private int playerScore;


    private int dealerScore;


    private BigDecimal betAmount;


    private String result;


    private LocalDateTime createdAt;


    @OneToMany(mappedBy = "game")
    private List<GameRound> rounds;


    @PrePersist
    private void createdAt() {
        createdAt = LocalDateTime.now();
    }
}