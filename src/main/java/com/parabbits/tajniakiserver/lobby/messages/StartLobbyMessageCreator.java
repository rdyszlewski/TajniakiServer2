package com.parabbits.tajniakiserver.lobby.messages;

import com.parabbits.tajniakiserver.lobby.manager.Lobby;
import com.parabbits.tajniakiserver.lobby.manager.LobbyPlayer;

public class StartLobbyMessageCreator {

    public static StartLobbyMessage create(Lobby lobby, LobbyPlayer player) {
        // TODO: można jeszcze wysłać jakąś informacje o ustawieniach rozgrywki
        StartLobbyMessage message = new StartLobbyMessage(lobby.getPlayers(), lobby.getSettings());
        message.setMinPlayersInTeam(lobby.getSettings().getMinTeamSize());
        message.setMaxPlayersInTeam(lobby.getSettings().getMaxTeamSize());
        message.setPlayerId(player.getId());
        message.setGameId(lobby.getID());
        return message;
    }
}
