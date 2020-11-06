package com.parabbits.tajniakiserver.lobby.messages;

import com.parabbits.tajniakiserver.game.models.Player;
import com.parabbits.tajniakiserver.shared.game.Game;

import java.util.List;

public class StartLobbyMessageCreator {

    public static StartLobbyMessage create(Game game, Player player){
        // TODO: można jeszcze wysłać jakąś informacje o ustawieniach rozgrywki
        StartLobbyMessage message = new StartLobbyMessage(getAllPlayersInLobby(game), game.getSettings());
        message.setMinPlayersInTeam(game.getSettings().getMinTeamSize());
        message.setMaxPlayersInTeam(game.getSettings().getMaxTeamSize());
        message.setPlayerId(player.getId());
        message.setGameId(game.getID());
        return message;
    }

    private static List<Player> getAllPlayersInLobby(Game game) {
        return game.getPlayers().getAllPlayers();
    }
}
