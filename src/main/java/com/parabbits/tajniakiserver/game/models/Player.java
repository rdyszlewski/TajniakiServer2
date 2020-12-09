package com.parabbits.tajniakiserver.game.models;

public class Player {

    protected final String sessionId;
    protected long id;
    protected String nickname;
    protected Team team;
    protected Role role;

    public Player(String sessionId, String nickname) {
        this.sessionId = sessionId;
        this.nickname = nickname;
    }

    public Player(final long id, final String sessionId, String nickname) {
        this.id = id;
        this.sessionId = sessionId;
        this.nickname = nickname;
    }

    public Player(String sessionId, long id, String nickname, Team team) {
        this(id, sessionId, nickname);
        this.team = team;
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
