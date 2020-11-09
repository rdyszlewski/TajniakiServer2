package com.parabbits.tajniakiserver.game.models;

public class Player {

    protected String sessionId;
    protected long id;
    protected String nickname;
    protected Team team;
    protected Role role;

    protected Player() {
    }

    public Player(String sessionId, String nickname) {
        init(sessionId, nickname);
    }

    public Player(String sessionId, long id, String nickname, Team team) {
        init(sessionId, nickname);
        this.id = id;
        this.team = team;
    }

    private void init(String sessionid, String nickname) {
        this.sessionId = sessionid;
        this.nickname = nickname;
        this.team = Team.LACK;
    }

    public long getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

    public Team getTeam() {
        return team;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getSessionId() {
        return sessionId;
    }
}
