package com.parabbits.tajniakiserver.lobby;

public class TeamMessage {

    private String nickname;
    private String team;

    public TeamMessage(String nickname, String team){
        this.nickname = nickname;
        this.team = team;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }
}
