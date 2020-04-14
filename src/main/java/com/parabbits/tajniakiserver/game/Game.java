package com.parabbits.tajniakiserver.game;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class Game {

    public static final int MAX_PLAYERS = 10;

    private Map<String, Player> players = new HashMap<>();

    public void addPlayer(String sessionId, String nickname){
        if (players.size() < MAX_PLAYERS){
            System.out.println("Dodanie gracza");
            Player player = new Player();
            player.setNickName(nickname);
            players.put(sessionId, player);
        }
    }

    public void testPrint(){
        for(Map.Entry<String, Player> entry: players.entrySet()){
            Player player = entry.getValue();
            System.out.println(player.getSessionId() + "  " + player.getNickName());
        }
    }

    public void removePlayer(String sessionId){
        System.out.println(players.size());
        if (players.containsKey(sessionId)){
            System.out.println("Taki gracz jest podłączony do gry");
            players.remove(sessionId);
        }
    }
}
