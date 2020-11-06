package com.parabbits.tajniakiserver.lobby.messages;

import com.parabbits.tajniakiserver.game.models.Player;
import com.parabbits.tajniakiserver.shared.game.GameSettings;

import java.util.List;
import java.util.UUID;

public class StartLobbyMessage {

    private UUID gameId;
    private long playerId;
    private int minPlayersInTeam;
    private int maxPlayersInTeam;
    private List<Player> players;
    private GameSettings settings;

    public StartLobbyMessage(List<Player> players, GameSettings settings) {
        this.players = players;
        this.settings = settings;
    }

    public UUID getGameId() {
        return gameId;
    }

    public void setGameId(UUID gameId) {
        this.gameId = gameId;
    }

    public long getPlayerId(){
        return playerId;
    }

    public void setPlayerId(long id){
        playerId = id;
    }

    public int getMinPlayersInTeam() {
        return minPlayersInTeam;
    }

    public void setMinPlayersInTeam(int minPlayersInTeam) {
        this.minPlayersInTeam = minPlayersInTeam;
    }

    public int getMaxPlayersInTeam() {
        return maxPlayersInTeam;
    }

    public void setMaxPlayersInTeam(int maxPlayersInTeam) {
        this.maxPlayersInTeam = maxPlayersInTeam;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public GameSettings getSettings() {
        return settings;
    }

    public void setSettings(GameSettings settings) {
        this.settings = settings;
    }
}
