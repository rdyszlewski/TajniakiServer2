package com.parabbits.tajniakiserver.lobby.messages;

public class TeamMessage {

    private long id;
    private String team;

    public TeamMessage(long id, String team) {
        this.id = id;
        this.team = team;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }
}
