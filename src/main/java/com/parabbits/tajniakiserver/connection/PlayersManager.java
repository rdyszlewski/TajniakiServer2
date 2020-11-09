package com.parabbits.tajniakiserver.connection;

import com.parabbits.tajniakiserver.lobby.manager.Lobby;
import com.parabbits.tajniakiserver.shared.game.Game;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class PlayersManager {

    private Map<String, Lobby> playersMap = new HashMap<>();

    public void addPlayer(String sessionId, Lobby lobby){
       playersMap.put(sessionId, lobby);
    }

    public Lobby findGame(String sessionId){
        if(playersMap.containsKey(sessionId)){
            return playersMap.get(sessionId);
        }
        return null;
    }

    public void removePlayer(String sessionId){
        Lobby lobby = findGame(sessionId);
        if(lobby != null){
            lobby.removePlayer(sessionId);
        }
        playersMap.remove(sessionId);
    }
}
