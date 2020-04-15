package com.parabbits.tajniakiserver.game;

public class Player {

    private String sessionId;
    private String nickName;
    private Team team;
    private Role role;
    private boolean ready;

    public Player(String sessionId, String nickName){
        this.sessionId = sessionId;
        this.nickName = nickName;
        this.team = Team.OBSERVER;
        this.ready = false;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isReady(){
        return ready;
    }

    public void setReady(boolean ready){
        this.ready = ready;
    }
}
