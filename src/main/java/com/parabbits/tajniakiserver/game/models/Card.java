package com.parabbits.tajniakiserver.game.models;

public class Card {

    private int index;
    private String word;
    private WordColor color;
    private boolean checked;
    // TODO: dorobić informacje o tym, kto zaznaczył które karty i tak dalej


    public Card(int index, String word, WordColor color, boolean checked) {
        this.index = index;
        this.word = word;
        this.color = color;
        this.checked = checked;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
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

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
