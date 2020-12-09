package com.parabbits.tajniakiserver.shared.game;

import com.parabbits.tajniakiserver.game.ClickCorrectness;
import com.parabbits.tajniakiserver.game.UseCardType;
import com.parabbits.tajniakiserver.game.models.Card;

import java.util.List;

public class ClickResult {

    private final UseCardType type;
    private final ClickCorrectness correctness;
    private final List<Card> updatedCards;
    private final Card card;

    public ClickResult(UseCardType type, ClickCorrectness correctness, List<Card> updatedCards, Card card) {
        this.type = type;
        this.correctness = correctness;
        this.updatedCards = updatedCards;
        this.card = card;
    }

    public UseCardType getType() {
        return type;
    }

    public ClickCorrectness getCorrectness() {
        return correctness;
    }

    public List<Card> getUpdatedCards() {
        return updatedCards;
    }

    public Card getCard() {
        return card;
    }
}
