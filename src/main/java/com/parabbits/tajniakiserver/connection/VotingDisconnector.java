package com.parabbits.tajniakiserver.connection;

import com.parabbits.tajniakiserver.game.models.Player;
import com.parabbits.tajniakiserver.shared.DisconnectMessage;
import com.parabbits.tajniakiserver.shared.game.Game;
import com.parabbits.tajniakiserver.shared.game.GameStep;

public class VotingDisconnector extends Disconnector {

    public static DisconnectMessage getMessage(Player player, Game game){
        GameStep step = isEnoughPlayers(game)? GameStep.VOTING: GameStep.LOBBY;
        return createDisconnectMessage(player, step);
    }

}
