package com.parabbits.tajniakiserver.shared.game;

import com.parabbits.tajniakiserver.game.models.Team;
import com.parabbits.tajniakiserver.voting.VotingPlayer;
import com.parabbits.tajniakiserver.voting.VotingService;

import java.util.List;

// TODO: można zrobić jakoś w ten sposób, że to nie będzie w grze, ale w jakimś innym miejscu i będzie do tego taki sam dostęp
public class VotingManager {

    private final VotingService blueVoting = new VotingService(Team.BLUE);
    private final VotingService redVoting = new VotingService(Team.RED);

    public List<VotingPlayer> getCandidates(Team team){
        return getVoting(team).getCandidates();
    }

    private VotingService getVoting(Team team){
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
}
