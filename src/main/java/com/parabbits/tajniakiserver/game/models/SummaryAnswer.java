package com.parabbits.tajniakiserver.game.models;

public class SummaryAnswer {

    private String word;
    private CardColor color;

    public SummaryAnswer(String word, CardColor color) {
        this.word = word;
        this.color = color;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public CardColor getColor() {
        return color;
    }

    public void setColor(CardColor color) {
        this.color = color;
    }
}
