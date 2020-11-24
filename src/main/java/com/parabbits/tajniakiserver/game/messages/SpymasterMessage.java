package com.parabbits.tajniakiserver.game.messages;

import com.parabbits.tajniakiserver.game.GameState;

import javax.validation.constraints.NotNull;


public class SpymasterMessage {

    private final String word;
    private final int number;
    private final GameStateMessage gameState;

    public SpymasterMessage(@NotNull String word, @NotNull int number, @NotNull GameState gameState) {
        this.word = word;
        this.number = number;
        this.gameState = new GameStateMessage(gameState);

    }

    public String getWord() {
        return word;
    }

    public int getNumber() {
        return number;
    }

    public GameStateMessage getGameState() {
        return gameState;
    }
}

