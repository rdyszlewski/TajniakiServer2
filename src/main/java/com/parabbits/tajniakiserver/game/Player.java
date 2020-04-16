package com.parabbits.tajniakiserver.game;

public class Player {

    private long id;
    private String sessionId;
    private String nickname;
    private Team team;
    private Role role;
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
        this.team = Team.OBSERVER;
        this.ready = false;
    }

    public long getId(){
        return id;
    }

    public void setId(long id){
        this.id = id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
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
