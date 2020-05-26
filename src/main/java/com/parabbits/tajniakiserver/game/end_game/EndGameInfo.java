package com.parabbits.tajniakiserver.game.end_game;

import com.parabbits.tajniakiserver.game.models.EndGameCause;
import com.parabbits.tajniakiserver.game.models.Team;


public class EndGameInfo {

    private Team winner;
    private EndGameCause cause;

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
}
