package com.parabbits.tajniakiserver.game.messages;

import com.parabbits.tajniakiserver.game.GameState;
import com.parabbits.tajniakiserver.game.models.*;

import java.util.List;

public class AnswerMessage {

//    private ClientCard card;
    private List<ClientCard> cardsToUpdate;
    private boolean correct;
    private GameState gameState;

    public AnswerMessage(List<ClientCard> cardsToUpdate, boolean correct, GameState gameState) {
//        this.card = card;
        this.cardsToUpdate = cardsToUpdate;
        this.correct = correct;
        this.gameState = gameState;
    }

//    public ClientCard getCard() {
//        return card;
//    }
//
//    public void setCard(ClientCard card) {
//        this.card = card;
//    }


    public List<ClientCard> getCardsToUpdate() {
        return cardsToUpdate;
    }

    public void setCardsToUpdate(List<ClientCard> cardsToUpdate) {
        this.cardsToUpdate = cardsToUpdate;
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
