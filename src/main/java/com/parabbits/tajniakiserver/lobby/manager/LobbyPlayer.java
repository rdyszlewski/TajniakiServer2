package com.parabbits.tajniakiserver.lobby.manager;

import com.parabbits.tajniakiserver.game.models.Player;
import com.parabbits.tajniakiserver.game.models.Team;

public class LobbyPlayer extends Player {

    private boolean ready = false;

    public LobbyPlayer(final String sessionId, final String nickname){
        super(sessionId, nickname);
    }
    public LobbyPlayer(final long id, final String sessionId, final String nickanme){
        super(id, sessionId, nickanme);
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }
}
