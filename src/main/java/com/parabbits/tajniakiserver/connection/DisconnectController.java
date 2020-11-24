package com.parabbits.tajniakiserver.connection;


import com.parabbits.tajniakiserver.game.messages.StartGameMessage;
import com.parabbits.tajniakiserver.game.messages.StartGameMessageCreator;
import com.parabbits.tajniakiserver.game.models.Player;
import com.parabbits.tajniakiserver.lobby.manager.Lobby;
import com.parabbits.tajniakiserver.lobby.manager.LobbyManager;
import com.parabbits.tajniakiserver.shared.DisconnectMessage;
import com.parabbits.tajniakiserver.shared.PlayerSessionId;
import com.parabbits.tajniakiserver.shared.game.Game;
import com.parabbits.tajniakiserver.shared.game.GameManager;
import com.parabbits.tajniakiserver.shared.game.GameStep;
import com.parabbits.tajniakiserver.utils.MessageManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class DisconnectController {

    protected static final String NEW_SPYMASTER_PATH = "/queue/game/new_spymaster";
    private final String DISCONNECT_PATH = "/queue/common/disconnect";
    @Autowired
    private MessageManager messageManager;

    @Autowired
    private GameManager gameManager;

    @Autowired
    private LobbyManager lobbyManager;

    public void disconnectPlayer(String playerSession, UUID gameId) {
        Lobby lobby = lobbyManager.findLobby(gameId);
        Game game = gameManager.findGame(gameId);
        Player player = game.getPlayers().getPlayer(playerSession);
        lobby.removePlayer(playerSession);
        List<String> remainingPlayersSessions = PlayerSessionId.getSessionsIds(lobby.getPlayers());
        DisconnectMessage message = null;
        switch (game.getState().getCurrentStep()) {
            case LOBBY:
                message = LobbyDisconnector.getMessage(player, remainingPlayersSessions);
                break;
            case VOTING:
                message = VotingDisconnector.getMessage(player, game);
                break;
            case GAME:
                Player newSpymaster = GameDisconnector.prepareNewSpymaster(player, game);
                message = GameDisconnector.getMessage(player, game);
                sendMessageToNewSpymaster(game, newSpymaster);
                break;
        }
        if (message != null) {
            messageManager.sendToAll(message, DISCONNECT_PATH, remainingPlayersSessions);
            game.getState().setCurrentStep(message.getCurrentStep());
            if (message.getCurrentStep() == GameStep.LOBBY) {
                lobby.reset();
            }
        }
    }

    private void sendMessageToNewSpymaster(Game game, Player newSpymaster) {
        StartGameMessage messageForNewSpymaster = StartGameMessageCreator.create(newSpymaster.getRole(), newSpymaster, game);
        messageManager.send(messageForNewSpymaster, newSpymaster.getSessionId(), NEW_SPYMASTER_PATH);
    }
}
