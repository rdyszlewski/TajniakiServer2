package com.parabbits.tajniakiserver.game.models;

import com.parabbits.tajniakiserver.summary.SummaryCard;
import com.parabbits.tajniakiserver.summary.SummaryEntry;

import java.util.List;

public class SummaryMessage {
    private Team winner;
    private int blueRemaining;
    private int redRemaining;
    private int blueFound;
    private int redFound;
    private List<SummaryEntry> process;
    private EndGameCause cause;
    private List<SummaryCard> cards;

    public Team getWinner() {
        return winner;
    }

    public void setWinner(Team winner) {
        this.winner = winner;
    }

    public int getBlueRemaining() {
        return blueRemaining;
    }

    public void setBlueRemaining(int blueRemaining) {
        this.blueRemaining = blueRemaining;
    }

    public int getRedRemaining() {
        return redRemaining;
    }

    public void setRedRemaining(int redRemaining) {
        this.redRemaining = redRemaining;
    }

    public int getBlueFound() {
        return blueFound;
    }

    public void setBlueFound(int blueFound) {
        this.blueFound = blueFound;
    }

    public int getRedFound() {
        return redFound;
    }

    public void setRedFound(int redFound) {
        this.redFound = redFound;
    }

    public List<SummaryEntry> getProcess() {
        return process;
    }

    public void setProcess(List<SummaryEntry> process) {
        this.process = process;
    }

    public EndGameCause getCause() {
        return cause;
    }

    public void setCause(EndGameCause cause) {
        this.cause = cause;
    }

    public List<SummaryCard> getCards() {
        return cards;
    }

    public void setCards(List<SummaryCard> cards) {
        this.cards = cards;
    }
}
