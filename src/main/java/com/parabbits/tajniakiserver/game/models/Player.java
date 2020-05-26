package com.parabbits.tajniakiserver.game.models;

public class Player extends GamePlayer{

    private String sessionId;
    private boolean ready;

    public Player(String sessionId, String nickname){
        init(sessionId, nickname);
    }

    public Player(String sessionId, long id, String nickname){
        init(sessionId, nickname);
        this.id = id;
    }

    private void init(String sessionid, String nickname){
        this.sessionId = sessionid;
        this.nickname = nickname;
        this.team = Team.LACK;
        this.ready = false;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public boolean isReady(){
        return ready;
    }

    public void setReady(boolean ready){
        this.ready = ready;
    }
}
