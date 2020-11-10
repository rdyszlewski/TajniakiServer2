package com.parabbits.tajniakiserver.connection;

import com.parabbits.tajniakiserver.game.models.Player;
import com.parabbits.tajniakiserver.game.models.Team;
import com.parabbits.tajniakiserver.shared.DisconnectMessage;
import com.parabbits.tajniakiserver.shared.game.Game;
import com.parabbits.tajniakiserver.shared.game.GameStep;

public class Disconnector {

    protected static final String DISCONNECT_PATH = "/queue/common/disconnect";
    protected static final String NEW_BOSS_PATH = "/queue/game/new_boss";

    protected static boolean isEnoughPlayers(Game game){
        return game.getPlayers().getPlayers(Team.BLUE).size() >= game.getSettings().getMinTeamSize()
                && game.getPlayers().getPlayers(Team.RED).size() >= game.getSettings().getMinTeamSize();
    }

    protected static DisconnectMessage createDisconnectMessage(Player player, GameStep step){
        DisconnectMessage message = new DisconnectMessage();
        message.setDisconnectedPlayer(player);
        message.setCurrentStep(step);
        return message;
    }

}
