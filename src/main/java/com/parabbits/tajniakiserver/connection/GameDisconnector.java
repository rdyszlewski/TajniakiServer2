package com.parabbits.tajniakiserver.connection;

import com.parabbits.tajniakiserver.game.models.Player;
import com.parabbits.tajniakiserver.game.models.Role;
import com.parabbits.tajniakiserver.shared.DisconnectMessage;
import com.parabbits.tajniakiserver.shared.game.Game;
import com.parabbits.tajniakiserver.shared.game.GameStep;

public class GameDisconnector extends Disconnector {

    public static DisconnectMessage getMessage(Player disconnectedPlayer, Game game) {
        if (isEnoughPlayers(game)) {
            switch (disconnectedPlayer.getRole()) {
                case SPYMASTER:
                    return handleDisconnectingSpymaster(disconnectedPlayer, game);
                case ORDINARY_PLAYER:
                    return handleDisconnectingOrdinaryPlayer(disconnectedPlayer, game);
            }
        } else {
            return createDisconnectMessage(disconnectedPlayer, GameStep.LOBBY);
        }
        return null;
    }

    private static DisconnectMessage handleDisconnectingOrdinaryPlayer(Player player, Game game) {
        return createDisconnectMessage(player, GameStep.GAME);
    }

    private static DisconnectMessage handleDisconnectingSpymaster(Player player, Game game) {
        DisconnectMessage message = createDisconnectMessage(player, GameStep.GAME);
        message.setPlayers(game.getPlayers().getAllPlayers());
        return message;
    }

    public static Player prepareNewSpymaster(Player disconnectedPlayer, Game game) {
        Player newSpymaster = game.getPlayers().getPlayers(disconnectedPlayer.getTeam()).get(0);
        newSpymaster.setRole(Role.SPYMASTER);
        return newSpymaster;
    }
}