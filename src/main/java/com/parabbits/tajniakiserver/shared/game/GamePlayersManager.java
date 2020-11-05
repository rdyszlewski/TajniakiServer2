package com.parabbits.tajniakiserver.shared.game;

import com.parabbits.tajniakiserver.game.models.Player;
import com.parabbits.tajniakiserver.game.models.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GamePlayersManager {

    private final Map<String, Player> players = new HashMap<>();
    private final GameSettings settings;
    private int currentPlayerID = 0;

    public GamePlayersManager(GameSettings settings){
        this.settings = settings;
    }

    public Player addPlayer(String sessionId, String nickname){
        if (players.size() < settings.getMaxTeamSize()*2){
            Player player = new Player(sessionId, nickname);
            addPlayer(player);
            return player;
        }
        return null;
    }

    public synchronized void addPlayer(Player player){
        player.setId(currentPlayerID);
        currentPlayerID++;
        players.put(player.getSessionId(), player);
    }

    public void removePlayer(String sessionId){
        System.out.println(players.size());
        if (players.containsKey(sessionId)){
            Player player = players.get(sessionId);
            // TODO: z tym będzie trzeba pomyśleć
//                removeUsePlayer(player);
            players.remove(sessionId);
        }
    }

    public Player getPlayer(String sessionId){
        if(players.containsKey(sessionId)){
            return players.get(sessionId);
        }
        return null;
    }

    public Player getPlayerById(long id){
        for(Player player: players.values()){
            if(player.getId() == id){
                return player;
            }
        }
        return null;
    }

    public List<Player> getAllPlayers(){
        return new ArrayList<>(players.values());
    }

    public List<Player> getPlayers(Team team){
        return players.values().stream().filter(x->x.getTeam().equals(team)).collect(Collectors.toList());
    }

    public int getPlayersCount(){
        return players.size();
    }

    public int getTeamSize(Team team){
        return getPlayers(team).size();
    }

}
