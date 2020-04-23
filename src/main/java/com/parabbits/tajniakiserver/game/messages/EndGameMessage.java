package com.parabbits.tajniakiserver.game.messages;

import com.parabbits.tajniakiserver.game.models.EndGameCause;
import com.parabbits.tajniakiserver.game.models.Team;

public class EndGameMessage {

    private Team winner;
    private EndGameCause cause;
    private int remainingBlue;
    private int remainingRed;

    public Team getWinner() {
        return winner;
    }

    public void setWinner(Team winner) {
        this.winner = winner;
    }

    public EndGameCause getCause() {
        return cause;
    }

    public void setCause(EndGameCause cause) {
        this.cause = cause;
    }

    public int getRemainingBlue() {
        return remainingBlue;
    }

    public void setRemainingBlue(int remainingBlue) {
        this.remainingBlue = remainingBlue;
    }

    public int getRemainingRed() {
        return remainingRed;
    }

    public void setRemainingRed(int remainingRed) {
        this.remainingRed = remainingRed;
    }
}
