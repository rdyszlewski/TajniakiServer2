package com.parabbits.tajniakiserver.voting.messages;

import com.parabbits.tajniakiserver.shared.game.Game;
import com.parabbits.tajniakiserver.voting.service.Voting;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class VotingManager {

    private final Map<UUID, Voting> votingMap = new HashMap<>();

    public Voting getVoting(Game game) {
        UUID gameId = game.getID();
        if (votingMap.containsKey(gameId)) {
            return votingMap.get(gameId);
        }
        Voting voting = createVoting(game);
        votingMap.put(gameId, voting);
        return voting;
    }

    private Voting createVoting(Game game) {
        return new Voting(game.getPlayers().getAllPlayers());
    }

    public void removeVoting(Game game) {
        votingMap.remove(game.getID());
    }
}
