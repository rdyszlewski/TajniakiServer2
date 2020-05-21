package com.parabbits.tajniakiserver.game.models;

public class SummaryAnswer {

    private String word;
    private WordColor color;

    public SummaryAnswer(String word, WordColor color) {
        this.word = word;
        this.color = color;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public WordColor getColor() {
        return color;
    }

    public void setColor(WordColor color) {
        this.color = color;
    }
}
