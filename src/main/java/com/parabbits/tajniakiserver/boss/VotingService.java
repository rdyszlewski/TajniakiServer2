package com.parabbits.tajniakiserver.boss;

import com.parabbits.tajniakiserver.game.Player;
import com.parabbits.tajniakiserver.game.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class VotingService {

    private final Map<String, String> votes = new HashMap<>();
    private Map<String, BossCandidatePlayer> candidates = new HashMap<>();
    private Map<String, Player> playersMap = new HashMap<>();
    private final Team team;

    public VotingService(Team team){
        this.team = team;
    }

    public void init(Map<String, Player> players){
        this.candidates = createCandidates(players);
        this.playersMap = players;
    }

    private Map<String, BossCandidatePlayer> createCandidates(Map<String, Player> players){
        Map<String, BossCandidatePlayer> candidates = new HashMap<>();
        for(Player player: players.values()){
            BossCandidatePlayer candidate = new BossCandidatePlayer(player.getId(), player.getNickname(), 0);
            candidates.put(player.getSessionId(), candidate);
        }
        return candidates;
    }

    public void vote(String sessionId, String vote){
        // TODO: refaktoryzacja
        if(sessionId.equals(vote)){
            return;
        }
        if(!votes.containsKey(sessionId) || !votes.get(sessionId).equals(vote)){
            BossCandidatePlayer candidate = candidates.get(vote);
            candidate.setVotes(candidate.getVotes()+1);
        }
        if(votes.containsKey(sessionId) && !votes.get(sessionId).equals(vote)){
            BossCandidatePlayer candidate = candidates.get(votes.get(sessionId));
            candidate.setVotes(candidate.getVotes()-1);
        }
        votes.put(sessionId, vote);
    }

    private BossCandidatePlayer getWinner(){
        BossCandidatePlayer bestCandidate = null;
        int maxVotes = 0;
        for (BossCandidatePlayer candidate: candidates.values()){
            // TODO: pomyśleć, co w przypadku remisu
            if(candidate.getVotes() > maxVotes){
                bestCandidate = candidate;
                maxVotes = candidate.getVotes();
            }
        }
        return bestCandidate;
    }

    public List<BossCandidatePlayer> getCandidates(){
        return new ArrayList<>(candidates.values());
    }
}
