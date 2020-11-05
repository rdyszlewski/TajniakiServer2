package com.parabbits.tajniakiserver.shared.game;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GameManager {

    private Map<UUID, Game> gamesMap;

    public GameManager(){
        gamesMap = new HashMap<>();
    }

    public Game findGame(UUID id){
        if(gamesMap.containsKey(id)){
            return gamesMap.get(id);
        }
        return null;
    }

    public Game findFreeGame(){
        // TODO: poszukać gry ze względu na liczbę graczy

        return new Game(); // TODO: później to usunać
    }

    public void removeGame(UUID id){
        // TODO: sprawdzenie, czy w grze nikogo nie ma
    }

    public Game createGame(){

        return new Game();
    }

    /**
     * Remove all games without players
     */
    public void removeEmptyGames(){

    }
}
