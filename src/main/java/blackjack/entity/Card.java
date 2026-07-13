package blackjack.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "cards")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "game_round_id")
    private GameRound gameRound;

    private String owner;

    private String suit;

    @Column(name = "card_rank")
    private String cardRank;

    private Integer value;

    public Card() {
    }

    public Long getId() {
        return id;
    }

    public GameRound getGameRound() {
        return gameRound;
    }

    public String getOwner() {
        return owner;
    }

    public String getSuit() {
        return suit;
    }

    public String getCardRank() {
        return cardRank;
    }

    public Integer getValue() {
        return value;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setGameRound(GameRound gameRound) {
        this.gameRound = gameRound;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setSuit(String suit) {
        this.suit = suit;
    }

    public void setCardRank(String cardRank) {
        this.cardRank = cardRank;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}