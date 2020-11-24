package com.parabbits.tajniakiserver.lobby.manager;

import com.parabbits.tajniakiserver.game.models.Player;

public class LobbyPlayerAdapter {

    public static Player createPlayer(LobbyPlayer player) {
        Player resultPlayer = new Player(player.getSessionId(), player.getId(), player.getNickname(), player.getTeam());
        resultPlayer.setRole(player.getRole());
        return resultPlayer;
    }
}
