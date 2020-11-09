package com.parabbits.tajniakiserver.lobby.messages;

import com.parabbits.tajniakiserver.game.models.Player;
import com.parabbits.tajniakiserver.lobby.manager.Lobby;
import com.parabbits.tajniakiserver.lobby.manager.LobbyPlayer;
import com.parabbits.tajniakiserver.shared.game.Game;

import java.util.List;

public class StartLobbyMessageCreator {

    public static StartLobbyMessage create(Lobby lobby, LobbyPlayer player){
        // TODO: można jeszcze wysłać jakąś informacje o ustawieniach rozgrywki
        StartLobbyMessage message = new StartLobbyMessage(lobby.getPlayers(), lobby.getSettings());
        message.setMinPlayersInTeam(lobby.getSettings().getMinTeamSize());
        message.setMaxPlayersInTeam(lobby.getSettings().getMaxTeamSize());
        message.setPlayerId(player.getId());
        message.setGameId(lobby.getID());
        return message;
    }

}
