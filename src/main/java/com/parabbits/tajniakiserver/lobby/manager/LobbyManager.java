package com.parabbits.tajniakiserver.lobby.manager;

import com.parabbits.tajniakiserver.shared.game.Game;
import com.parabbits.tajniakiserver.shared.game.GameManager;
import com.parabbits.tajniakiserver.shared.game.GameStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LobbyManager {

    @Autowired
    private GameManager gameManager;

    private Map<UUID, Lobby> lobbyMap = new HashMap<>();

    public Lobby findFreeLobby() throws IOException {
        Lobby lobby = LobbyFinder.findBestLobby(lobbyMap.values());
        if(lobby == null){
            lobby = createLobby();
        }
        return lobby;
    }

    public Lobby findLobby(UUID id){
        if(lobbyMap.containsKey(id)){
            return lobbyMap.get(id);
        }
        return null;
    }

    public Lobby createLobby() throws IOException {
        Game game = gameManager.createGame();
        Lobby lobby = new Lobby(game);
        UUID id = game.getID();
        lobbyMap.put(id, lobby);
        return lobby;
    }

    // TODO: można zrobic joinToLobby(UUID id, Player player)

    public void removeLobby(UUID id){
        if(lobbyMap.containsKey(id)){
            // TODO: zakończenie i wyczyszczenie lobby i gry
            lobbyMap.remove(id);
            gameManager.removeGame(id);
        }
    }

}
