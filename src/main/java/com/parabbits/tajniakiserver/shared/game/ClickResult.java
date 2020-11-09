package com.parabbits.tajniakiserver.shared.game;

import com.parabbits.tajniakiserver.game.models.Card;
import com.parabbits.tajniakiserver.game.models.Player;

import java.util.List;

public class ClickResult {

    public enum ClickCorrectness{
        CORRECT,
        INCORRECT,
        KILLER,
        NONE
    }

    private ClickCorrectness correctness;
    private List<Card> updatedCards;
    private Player player;
    private Card card;

    public ClickResult(ClickCorrectness correctness, List<Card> updatedCards, Card card, Player player){
        this.correctness = correctness;
        this.updatedCards = updatedCards;
        this.card = card;
        this.player = player;
    }

    // TODO: można dodać, coś, co będzie informowało o wygranej
    // TODO: tutaj powinno być coś jeszcze, ale nie pamiętam co


    public ClickCorrectness getCorrectness() {
        return correctness;
    }

    public void setCorrectness(ClickCorrectness correctness) {
        this.correctness = correctness;
    }

    public List<Card> getUpdatedCards() {
        return updatedCards;
    }

    public void setUpdatedCards(List<Card> updatedCards) {
        this.updatedCards = updatedCards;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }
}
