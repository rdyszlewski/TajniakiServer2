package com.parabbits.tajniakiserver.lobby.team;

import com.parabbits.tajniakiserver.game.models.Player;
import com.parabbits.tajniakiserver.game.models.Team;
import com.parabbits.tajniakiserver.shared.game.Game;

import java.util.List;
import java.util.stream.Collectors;

public class LobbyHelper {

    public static boolean isEndChoosing(Game game){
        return areAllReady(game) && isMinNumberOfPlayers(game);
    }

    private static boolean areAllReady(Game game) {
        List<Player> readyPlayers = game.getPlayers().getAllPlayers().stream().filter(Player::isReady).collect(Collectors.toList());
        return readyPlayers.size() == game.getPlayers().getAllPlayers().size();
    }

    private static boolean isMinNumberOfPlayers(Game game){
        int bluePlayers = game.getPlayers().getPlayers(Team.BLUE).size();
        int redPlayers = game.getPlayers().getPlayers(Team.RED).size();
        return bluePlayers >= game.getSettings().getMinTeamSize() && redPlayers >= game.getSettings().getMinTeamSize();
    }
}
