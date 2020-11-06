package com.parabbits.tajniakiserver.voting.service;

import java.util.ArrayList;
import java.util.List;

public class VotingPlayer {
    private long id;
    private String nickname;
    private List<Long> votes = new ArrayList<>();

    public VotingPlayer(long id, String nickname) {
        this.id = id;
        this.nickname = nickname;
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

    public void addVote(Long vote){
        votes.add(vote);
    }

    public void removeVote(Long vote){
        votes.remove(vote);
    }

    public List<Long> getVotes() {
        return votes;
    }
}
