package com.parabbits.tajniakiserver.lobby.messages;

import com.parabbits.tajniakiserver.lobby.manager.LobbyPlayer;
import com.parabbits.tajniakiserver.shared.game.GameSettings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class StartLobbyMessage {

    private UUID gameId;
    // TODO: tutaj jakoś prytnie trzeba zmienić nazwę
    private long playerId;
    // TODO: jeżeli dołączamy settings, to to może być niepotrzebne
    private int minPlayersInTeam;
    private int maxPlayersInTeam;
    private List<LobbyPlayer> players;
    private GameSettings settings;

    public StartLobbyMessage(Collection<LobbyPlayer> players, GameSettings settings) {
        this.players = new ArrayList<>(players);
        this.settings = settings;
    }

    public UUID getGameId() {
        return gameId;
    }

    public void setGameId(UUID gameId) {
        this.gameId = gameId;
    }

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long id) {
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

    public List<LobbyPlayer> getPlayers() {
        return players;
    }

    public void setPlayers(List<LobbyPlayer> players) {
        this.players = players;
    }

    public GameSettings getSettings() {
        return settings;
    }

    public void setSettings(GameSettings settings) {
        this.settings = settings;
    }
}
