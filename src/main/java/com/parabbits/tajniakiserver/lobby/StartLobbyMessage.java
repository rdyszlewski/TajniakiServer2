package com.parabbits.tajniakiserver.lobby;

import com.parabbits.tajniakiserver.game.models.Player;
import com.parabbits.tajniakiserver.shared.GameSettings;

import java.util.List;

public class StartLobbyMessage {

    private List<Player> players;
    private GameSettings settings;

    public StartLobbyMessage(List<Player> player, GameSettings settings) {
        this.players = player;
        this.settings = settings;
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
