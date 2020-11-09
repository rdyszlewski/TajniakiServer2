package com.parabbits.tajniakiserver.lobby.manager;

import com.parabbits.tajniakiserver.game.models.Player;
import com.parabbits.tajniakiserver.game.models.Team;

public class LobbyPlayer extends Player {

    private boolean ready = false;

    public LobbyPlayer(){
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
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
