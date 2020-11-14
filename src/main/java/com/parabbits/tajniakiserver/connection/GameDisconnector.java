package com.parabbits.tajniakiserver.connection;

import com.parabbits.tajniakiserver.game.models.Player;
import com.parabbits.tajniakiserver.game.models.Role;
import com.parabbits.tajniakiserver.game.models.Team;
import com.parabbits.tajniakiserver.shared.DisconnectMessage;
import com.parabbits.tajniakiserver.shared.game.Game;
import com.parabbits.tajniakiserver.shared.game.GameStep;



public class GameDisconnector extends Disconnector{

    public static DisconnectMessage getMessage(Player disconnectedPlayer, Game game){
        if (isEnoughPlayers(game)){
            switch (disconnectedPlayer.getRole()){
                case BOSS:
                    return handleDisconnectBoss(disconnectedPlayer, game);
                case PLAYER:
                    return handleDisconnectPlayer(disconnectedPlayer, game);
            }

        } else {
            return createDisconnectMessage(disconnectedPlayer, GameStep.LOBBY);
        }
        return null;
    }

    private static DisconnectMessage handleDisconnectPlayer(Player player, Game game){
        return createDisconnectMessage(player, GameStep.GAME);
    }

    private static DisconnectMessage handleDisconnectBoss(Player player, Game game){
        setNewBoss(player, game);
        DisconnectMessage message = createDisconnectMessage(player, GameStep.GAME);
        message.setPlayers(game.getPlayers().getAllPlayers());
        return message;
    }

    private static void setNewBoss(Player player, Game game) {
        Player newBoss = game.getPlayers().getPlayers(player.getTeam()).get(0);
        newBoss.setRole(Role.BOSS);
    }

    public static Player prepareNewBoss(Player disconectedPlayer, Game game){
        Player newBoss = game.getPlayers().getPlayers(disconectedPlayer.getTeam()).get(0);
        newBoss.setRole(Role.BOSS);
        return newBoss;
    }

}
