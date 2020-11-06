package com.parabbits.tajniakiserver.shared.game;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

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

    public Game findFreeGame() throws IOException {
        Game game = findBestGame();
        if (game == null) {
            game = createGame();
        }

        return game;
    }

    private Game findBestGame() {
        Game game = findBestGameWithInsufficientPlayers();
        if (game == null) {
            game = findBestNotFullGame();
        }
        return game;
    }

    private Game findBestGameWithInsufficientPlayers() {
        List<Game> games = gamesMap.values().stream().filter(game -> {
            // TODO: to trzeba będzie jakoś skrócić
            return isLobbyState(game) && isNotMinPlayers(game);
        }).collect(Collectors.toList());
        if (!games.isEmpty()) {
            return games.stream().max(new PlayersComparator()).get();
        }
        return null;
    }

    private Game findBestNotFullGame() {
        List<Game> games = gamesMap.values().stream().filter(game -> {
            return isLobbyState(game) && isNotFull(game);
        }).collect(Collectors.toList());
        if (!games.isEmpty()) {
            return games.stream().min(new PlayersComparator()).get();
        }
        return null;
    }

    private boolean isNotMinPlayers(Game game) {
        return game.getPlayers().getPlayersCount() < game.getSettings().getMinTeamSize() * 2;
    }

    private boolean isLobbyState(Game game) {
        return game.getState().getCurrentStep() == GameStep.LOBBY;
    }

    private boolean isNotFull(Game game) {
        return game.getPlayers().getPlayersCount() < game.getSettings().getMaxTeamSize() * 2;
    }


    public void removeGame(UUID id) {
        // TODO: sprawdzenie, czy w grze nikogo nie ma
    }

    public Game createGame() throws IOException {
        UUID id = UUID.randomUUID();
        Game game = new Game(id);
        game.initializeGame();
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

    private class PlayersComparator implements Comparator<Game> {

        @Override
        public int compare(Game first, Game second) {
            int firstSize = first.getPlayers().getPlayersCount();
            int secondSize = second.getPlayers().getPlayersCount();
            if (firstSize > secondSize) {
                return -1;
            } else if (firstSize < secondSize) {
                return 1;
            }
            return 0;
        }
    }
}
