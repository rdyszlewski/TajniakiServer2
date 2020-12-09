package com.parabbits.tajniakiserver.voting.service;

import com.parabbits.tajniakiserver.game.models.Player;
import com.parabbits.tajniakiserver.game.models.Team;

import java.util.*;
import java.util.stream.Collectors;


public class TeamVoting {

    private final Map<String, String> votes = new HashMap<>();
    private Map<String, VotingPlayer> candidates = new HashMap<>();
    private final Team team;

    public TeamVoting(Team team) {
        this.team = team;
    }

    public void init(List<Player> players) {
        Map<String, Player> playersMap = players.stream().collect(Collectors.toMap(Player::getSessionId, player -> player));
        this.candidates = createCandidates(playersMap);
    }

    private Map<String, VotingPlayer> createCandidates(Map<String, Player> players) {
        return players.values().stream().filter(player -> player.getTeam() == team)
                .collect(Collectors.toMap(Player::getSessionId,
                        player -> new VotingPlayer(player.getId(), player.getNickname())));
    }

    public List<VotingPlayer> vote(String sessionId, String vote) {
        if (isVoteAlready(sessionId, vote)) {
            return null;
        }
        return setVote(sessionId, vote);
    }

    private List<VotingPlayer> setVote(String sessionId, String vote) {
        List<VotingPlayer> playersToUpdate = new ArrayList<>();
        VotingPlayer votedPlayer = candidates.get(vote);
        VotingPlayer votingPlayer = candidates.get(sessionId);
        votedPlayer.addVote(votingPlayer.getId());
        playersToUpdate.add(votedPlayer);

        if (isVoteForAnotherPlayer(sessionId, vote)) {
            VotingPlayer previousVotedPlayer = candidates.get(votes.get(sessionId));
            previousVotedPlayer.removeVote(votingPlayer.getId());
            playersToUpdate.add(previousVotedPlayer);
        }
        votes.put(sessionId, vote);
        return playersToUpdate;
    }

    private boolean isVoteAlready(String sessionId, String vote) {
        return votes.containsKey(sessionId) && votes.get(sessionId).equals(vote);
    }

    private boolean isVoteForAnotherPlayer(String sessionId, String vote) {
        return votes.containsKey(sessionId) && !votes.get(sessionId).equals(vote);
    }

    public void reset() {
        votes.clear();
    }

    public VotingPlayer getWinner() {
        VotingPlayer bestCandidate = null;
        Random random = new Random();
        int maxVotes = 0;
        for (VotingPlayer candidate : candidates.values()) {
            int votes = candidate.getVotes().size();
            if (votes > maxVotes) {
                bestCandidate = candidate;
                maxVotes = votes;
            } else if (votes == maxVotes) {
                int val = random.nextInt(100);
                bestCandidate = val > 50 ? bestCandidate : candidate;
                maxVotes = val > 50 ? maxVotes : votes;
            }
        }
        if (bestCandidate == null && !candidates.isEmpty()) {
            return candidates.values().stream().findFirst().get();
        }
        return bestCandidate;
    }

    public List<VotingPlayer> getCandidates() {
        return new ArrayList<>(candidates.values());
    }
}
