package com.parabbits.tajniakiserver.game.messages;

import com.parabbits.tajniakiserver.game.GameState;
import com.parabbits.tajniakiserver.game.models.*;

import java.util.List;

public class AnswerMessage {

    private final List<ClientCard> cardsToUpdate;
    private final boolean correct;
    private final GameStateMessage gameState;

    public AnswerMessage(List<ClientCard> cardsToUpdate, boolean correct, GameState gameState) {
        this.cardsToUpdate = cardsToUpdate;
        this.correct = correct;
        this.gameState = new GameStateMessage(gameState);
    }

    public List<ClientCard> getCardsToUpdate() {
        return cardsToUpdate;
    }

    public boolean isCorrect() {
        return correct;
    }


    public GameStateMessage getGameState() {
        return gameState;
    }
}
