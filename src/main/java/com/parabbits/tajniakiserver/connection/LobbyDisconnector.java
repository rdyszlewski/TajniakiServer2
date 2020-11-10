package com.parabbits.tajniakiserver.connection;

import com.parabbits.tajniakiserver.game.models.Player;
import com.parabbits.tajniakiserver.shared.DisconnectMessage;
import com.parabbits.tajniakiserver.shared.game.Game;
import com.parabbits.tajniakiserver.shared.game.GameStep;
import com.parabbits.tajniakiserver.utils.MessageManager;

import java.util.List;

public class LobbyDisconnector extends Disconnector{

    public static DisconnectMessage getMessage(Player player, List<String> playersIds){
        return createDisconnectMessage(player, GameStep.LOBBY);
    }
}


