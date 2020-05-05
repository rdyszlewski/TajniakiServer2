package com.parabbits.tajniakiserver.shared;

public class GameSettings {

    private int maxTeamSize = 5;
    private int firstTeamWords = 9;
    private int numbersOfCards = 25;

    public int getMaxTeamSize() {
        return maxTeamSize;
    }

    public void setMaxTeamSize(int maxTeamSize) {
        this.maxTeamSize = maxTeamSize;
    }

    public int getFirstTeamWords() {
        return firstTeamWords;
    }

    public void setFirstTeamWords(int firstTeamWords) {
        this.firstTeamWords = firstTeamWords;
    }

    public int getNumbersOfCards() {
        return numbersOfCards;
    }

    public void setNumbersOfCards(int numbersOfCards) {
        this.numbersOfCards = numbersOfCards;
    }
}
