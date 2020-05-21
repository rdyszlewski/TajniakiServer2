package com.parabbits.tajniakiserver.game.models;

import com.parabbits.tajniakiserver.history.HistoryEntry;

import java.util.List;

public class SummaryMessage {
    private Team winner;
    private int blueRemaining;
    private int redRemaining;
    private List<HistoryEntry> process;
    private WinnerCause cause;

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

    public List<HistoryEntry> getProcess() {
        return process;
    }

    public void setProcess(List<HistoryEntry> process) {
        this.process = process;
    }

    public WinnerCause getCause() {
        return cause;
    }

    public void setCause(WinnerCause cause) {
        this.cause = cause;
    }
}
