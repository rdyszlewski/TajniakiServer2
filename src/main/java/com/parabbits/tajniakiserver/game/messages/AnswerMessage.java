package com.parabbits.tajniakiserver.game.messages;

import com.parabbits.tajniakiserver.game.GameState;
import com.parabbits.tajniakiserver.game.models.*;

public class AnswerMessage {

    private ClientCard card;
    private boolean correct;
    private GameState gameState;

    public AnswerMessage(ClientCard card, boolean correct, GameState gameState) {
        this.card = card;
        this.correct = correct;
        this.gameState = gameState;
    }

    public ClientCard getCard() {
        return card;
    }

    public void setCard(ClientCard card) {
        this.card = card;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }
}
