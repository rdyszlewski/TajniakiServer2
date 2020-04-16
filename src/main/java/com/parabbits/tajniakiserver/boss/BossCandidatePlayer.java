package com.parabbits.tajniakiserver.boss;

public class BossCandidatePlayer {
    private long id;
    private String nickname;
    private int votes = 0;

    public BossCandidatePlayer(long id, String nickname, int votes) {
        this.id = id;
        this.nickname = nickname;
        this.votes = votes;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }
}
