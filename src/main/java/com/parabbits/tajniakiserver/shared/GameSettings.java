package com.parabbits.tajniakiserver.shared;

public class GameSettings {

    private int minTeamSize = 2;
    private int maxTeamSize = 3;
//    private int firstTeamWords = 9;
    private int firstTeamWords = 2;
    private int numbersOfCards = 25;

    public int getMinTeamSize() {
        return minTeamSize;
    }

    public void setMinTeamSize(int minTeamSize) {
        this.minTeamSize = minTeamSize;
    }

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
