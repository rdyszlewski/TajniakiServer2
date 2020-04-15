package com.parabbits.tajniakiserver.lobby;

public class ChangeTeamMessage {

    private String nickname;
    private String team;

    public ChangeTeamMessage(String nickname, String team){
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
