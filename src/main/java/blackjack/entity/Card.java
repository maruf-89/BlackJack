package blackjack.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "cards")
public class Card {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String suit;


    @Column(name = "card_rank")
    private String cardRank;


    private int value;


    /**
     * PLAYER eller DEALER.
     * Används för att kunna återställa spelet från databasen.
     */
    private String owner;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_round_id")
    private GameRound gameRound;


    public Long getId() {

        return id;

    }


    public String getSuit() {

        return suit;

    }


    public void setSuit(String suit) {

        this.suit = suit;

    }


    public String getCardRank() {

        return cardRank;

    }


    public void setCardRank(String cardRank) {

        this.cardRank = cardRank;

    }


    public int getValue() {

        return value;

    }


    public void setValue(int value) {

        this.value = value;

    }


    public String getOwner() {

        return owner;

    }


    public void setOwner(String owner) {

        this.owner = owner;

    }


    public GameRound getGameRound() {

        return gameRound;

    }


    public void setGameRound(GameRound gameRound) {

        this.gameRound = gameRound;

    }

}