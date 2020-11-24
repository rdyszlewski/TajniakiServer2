package com.parabbits.tajniakiserver.lobby.manager;

import com.parabbits.tajniakiserver.shared.game.GameStep;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class LobbyFinder {

    public static Lobby findBestLobby(Collection<Lobby> lobbyList) {
        Lobby lobby = findBestLobbyWithInsufficientPlayers(lobbyList);
        if (lobby == null) {
            lobby = findBestNotFullLobby(lobbyList);
        }
        return lobby;
    }

    private static Lobby findBestLobbyWithInsufficientPlayers(Collection<Lobby> lobbyList) {
        // TODO: sprawdzenie, czy gra się nie torzy
        List<Lobby> lobbies = lobbyList.stream().filter(LobbyFinder::isLobbyState).collect(Collectors.toList());
        if (!lobbies.isEmpty()) {
            return lobbies.stream().max(new PlayerComparator()).get();
        }
        return null;
    }

    private static Lobby findBestNotFullLobby(Collection<Lobby> lobbyList) {
        List<Lobby> lobbies = lobbyList.stream().filter(x -> isLobbyState(x) && !x.isFull()).collect(Collectors.toList());
        if (!lobbies.isEmpty()) {
            return lobbies.stream().min(new PlayerComparator()).get();
        }
        return null;
    }

    private static boolean isLobbyState(Lobby lobby) {
        return lobby.getGameState() == GameStep.LOBBY;
    }

    private static class PlayerComparator implements Comparator<Lobby> {
        @Override
        public int compare(Lobby first, Lobby second) {
            int firstSize = first.getPlayersCount();
            int secondSize = second.getPlayersCount();
            if (firstSize > secondSize) {
                return -1;
            } else if (firstSize < secondSize) {
                return 1;
            }
            return 0;
        }
    }
}
