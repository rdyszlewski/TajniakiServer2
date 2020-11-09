package com.parabbits.tajniakiserver.shared.game;

import com.parabbits.tajniakiserver.game.models.Player;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GameManager {

    private Map<UUID, Game> gamesMap;

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
        // TODO: sprawdzenie, czy w grze nikogo nie ma
    }

    public Game createGame() {
        UUID id = UUID.randomUUID();
        Game game = new Game(id);
        game.getState().setCurrentStep(GameStep.LOBBY); // TODO: to później będzie można zmienić
        gamesMap.put(id, game);
        return game;
    }

    /**
     * Remove all games without players
     */
    public void removeEmptyGames() {
        // TODO: zaimplementować
    }
}
