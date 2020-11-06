package com.parabbits.tajniakiserver.voting.service;

import com.parabbits.tajniakiserver.game.models.Player;
import com.parabbits.tajniakiserver.game.models.Team;

import java.util.List;
import java.util.stream.Collectors;

// TODO: można zrobić jakoś w ten sposób, że to nie będzie w grze, ale w jakimś innym miejscu i będzie do tego taki sam dostęp
public class Voting {

    private final TeamVoting blueVoting = new TeamVoting(Team.BLUE);
    private final TeamVoting redVoting = new TeamVoting(Team.RED);

    private boolean started = false;

    public Voting(List<Player> players){
        initTeam(Team.BLUE, players, blueVoting);
        initTeam(Team.RED, players, redVoting);
    }

    private void initTeam(Team team, List<Player> players, TeamVoting voting){
        List<Player> teamPlayers = players.stream().filter(x->x.getTeam() == team).collect(Collectors.toList());
        voting.init(teamPlayers);
    }

    public List<VotingPlayer> getCandidates(Team team){
        return getVoting(team).getCandidates();
    }

    private TeamVoting getVoting(Team team){
        switch (team){
            case BLUE:
                return blueVoting;
            case RED:
                return redVoting;
            default:
                throw new IllegalArgumentException();
        }
    }

    public List<VotingPlayer> vote(String playerSessionId, String votedPlayerSessionId, Team team){
        return getVoting(team).vote(playerSessionId, votedPlayerSessionId);
    }

    public VotingPlayer getWinner(Team team){
        return getVoting(team).getWinner();
    }

    public void reset(){
        // TODO: resetowanie tego cholerstwa
    }

    public boolean isStarted(){
        return started;
    }

    public void startVoting(){
        this.started = true;
    }
}
