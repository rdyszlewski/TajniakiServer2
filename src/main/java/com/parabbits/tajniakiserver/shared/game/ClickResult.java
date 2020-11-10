package com.parabbits.tajniakiserver.shared.game;

import com.parabbits.tajniakiserver.game.ClickCorrectness;
import com.parabbits.tajniakiserver.game.UseCardType;
import com.parabbits.tajniakiserver.game.models.Card;

import java.util.List;

public class ClickResult {

    private UseCardType type;
    private ClickCorrectness correctness;
    private List<Card> updatedCards;
    private Card card;

    public ClickResult(UseCardType type, ClickCorrectness correctness, List<Card> updatedCards, Card card){
        this.type = type;
        this.correctness = correctness;
        this.updatedCards = updatedCards;
        this.card = card;
    }

    // TODO: można dodać, coś, co będzie informowało o wygranej
    // TODO: tutaj powinno być coś jeszcze, ale nie pamiętam co


    public UseCardType getType() {
        return type;
    }

    public void setType(UseCardType type) {
        this.type = type;
    }

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

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }
}
