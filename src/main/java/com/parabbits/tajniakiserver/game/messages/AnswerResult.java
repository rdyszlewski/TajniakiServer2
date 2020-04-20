package com.parabbits.tajniakiserver.game.messages;

import com.parabbits.tajniakiserver.game.GameState;
import com.parabbits.tajniakiserver.game.models.*;

import java.util.List;

public class AnswerResult {

    private String word;
    private WordColor correctColor;
    private boolean correct;
    private GameState gameState;

    public AnswerResult(String word, WordColor correctColor) {
        this.word = word;
        this.correctColor = correctColor;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public WordColor getCorrectColor() {
        return correctColor;
    }

    public void setCorrectColor(WordColor correctColor) {
        this.correctColor = correctColor;
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
