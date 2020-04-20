package com.parabbits.tajniakiserver.game.messages;

import com.parabbits.tajniakiserver.game.GameState;


public class BossMessage {

    // TODO: przemysleć, jak to powinno wyglądać
    private String word;
    private int number;
    private GameState gameState;

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }
}

