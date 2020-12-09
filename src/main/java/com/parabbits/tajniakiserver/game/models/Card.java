package com.parabbits.tajniakiserver.game.models;

public class Card {

    private final int index;
    private final String word;
    private CardColor color;
    private boolean checked;

    public Card(int index, String word, CardColor color, boolean checked) {
        this.index = index;
        this.word = word;
        this.color = color;
        this.checked = checked;
    }

    public int getId() {
        return index;
    }

    public String getWord() {
        return word;
    }

    public CardColor getColor() {
        return color;
    }

    public void setColor(CardColor color) {
        this.color = color;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
