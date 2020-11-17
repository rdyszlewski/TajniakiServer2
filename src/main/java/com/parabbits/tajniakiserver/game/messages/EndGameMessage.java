package com.parabbits.tajniakiserver.game.messages;

import com.parabbits.tajniakiserver.game.models.EndGameCause;
import com.parabbits.tajniakiserver.game.models.Team;

public final class EndGameMessage {

    private final Team winner;
    private final EndGameCause cause;
    private final int remainingBlue;
    private final int remainingRed;

    public EndGameMessage(final Team winner, final EndGameCause cause, final int remainingBlue, final int remainingRed){
        this.winner = winner;
        this.cause = cause;
        this.remainingBlue = remainingBlue;
        this.remainingRed = remainingRed;
    }

    public Team getWinner() {
        return winner;
    }

    public EndGameCause getCause() {
        return cause;
    }

    public int getRemainingBlue() {
        return remainingBlue;
    }

    public int getRemainingRed() {
        return remainingRed;
    }
}
