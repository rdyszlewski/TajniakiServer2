package com.parabbits.tajniakiserver.voting;

import com.parabbits.tajniakiserver.game.models.Player;
import com.parabbits.tajniakiserver.game.models.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


public class TeamVoting {

    private final Map<String, String> votes = new HashMap<>();
    private Map<String, VotingPlayer> candidates = new HashMap<>();
    private Map<String, Player> playersMap = new HashMap<>();
    private final Team team;

    public TeamVoting(Team team){
        this.team = team;
    }

    public void init(List<Player> players){
        Map<String, Player> playersMap = players.stream().collect(Collectors.toMap(Player::getSessionId, player->player));
        this.candidates = createCandidates(playersMap);
        this.playersMap = playersMap;
    }

    private Map<String, VotingPlayer> createCandidates(Map<String, Player> players){
        Map<String, VotingPlayer> candidates = new HashMap<>();
        for(Player player: players.values()){
            if(player.getTeam() == team){
                VotingPlayer candidate = new VotingPlayer(player.getId(), player.getNickname());
                candidates.put(player.getSessionId(), candidate);
            }
        }
        return candidates;
    }

    public List<VotingPlayer> vote(String sessionId, String vote){
        if(votes.containsKey(sessionId) && votes.get(sessionId).equals(vote)){
            return null;
        }

        List<VotingPlayer> playersToUpdate = new ArrayList<>();
        VotingPlayer votedPlayer = candidates.get(vote);
        VotingPlayer votingPlayer = candidates.get(sessionId);
        votedPlayer.addVote(votingPlayer.getId());
        playersToUpdate.add(votedPlayer);

        if(votes.containsKey(sessionId) && !votes.get(sessionId).equals(vote)){
            VotingPlayer previousVotedPlayer = candidates.get(votes.get(sessionId));
            previousVotedPlayer.removeVote(votingPlayer.getId());
            playersToUpdate.add(previousVotedPlayer);
        }
        votes.put(sessionId, vote);
        return playersToUpdate;
    }

    public VotingPlayer getWinner(){
        VotingPlayer bestCandidate = null;
        int maxVotes = 0;
        for (VotingPlayer candidate: candidates.values()){
            // TODO: pomyśleć, co w przypadku remisu
            int votes = candidate.getVotes().size();
            if(votes >= maxVotes){
                bestCandidate = candidate;
                maxVotes = votes;
            }
        }
        return bestCandidate;
    }

    public List<VotingPlayer> getCandidates(){
        return new ArrayList<>(candidates.values());
    }
}
