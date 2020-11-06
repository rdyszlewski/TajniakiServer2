package com.parabbits.tajniakiserver.voting.messages;

import com.parabbits.tajniakiserver.voting.VotingPlayer;

import java.util.List;

public class StartVotingMessage {
    
    private int time;
    private List<VotingPlayer> players;

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public List<VotingPlayer> getPlayers() {
        return players;
    }

    public void setPlayers(List<VotingPlayer> players) {
        this.players = players;
    }
}
