package com.parabbits.tajniakiserver.shared.game;

import com.parabbits.tajniakiserver.connection.DisconnectController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class GameManager {

    private final Map<UUID, Game> gamesMap;

    @Autowired
    private DisconnectController disconnectController;

    public GameManager() {
        gamesMap = new HashMap<>();
    }

    public Game findGame(UUID id) {
        if (gamesMap.containsKey(id)) {
            return gamesMap.get(id);
        }
        return null;
    }

    public void removeGame(UUID id) {
        if (gamesMap.containsKey(id)) {
            Game game = gamesMap.get(id);
            if (game.getPlayers().getPlayersCount() != 0) {
                game.getPlayers().getAllPlayers().forEach(x -> disconnectController.disconnectPlayer(x.getSessionId(), id));
            }
            gamesMap.remove(id);
        }
    }

    public Game createGame() {
        UUID id = UUID.randomUUID();
        Game game = new Game(id);
        game.getState().setCurrentStep(GameStep.LOBBY);
        gamesMap.put(id, game);
        return game;
    }
}
