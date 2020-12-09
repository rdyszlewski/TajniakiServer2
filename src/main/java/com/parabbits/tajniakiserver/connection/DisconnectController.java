package com.parabbits.tajniakiserver.connection;


import com.parabbits.tajniakiserver.game.messages.StartGameMessage;
import com.parabbits.tajniakiserver.game.messages.StartGameMessageCreator;
import com.parabbits.tajniakiserver.game.models.Player;
import com.parabbits.tajniakiserver.lobby.manager.Lobby;
import com.parabbits.tajniakiserver.lobby.manager.LobbyManager;
import com.parabbits.tajniakiserver.lobby.manager.LobbyPlayer;
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
import java.util.stream.Collectors;

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
        LobbyPlayer lobbyPlayer = lobby.getPlayer(playerSession);
        lobby.removePlayer(playerSession);
        DisconnectMessage message = null;
        switch (game.getState().getCurrentStep()) {
            case LOBBY:
                message = handleLobbyDisconnection(playerSession, lobby, lobbyPlayer);
                break;
            case VOTING:
                message = handleVotingDisconnection(playerSession, lobby, game, player);
                break;
            case GAME:
                message = handleGameDisconnection(playerSession, lobby, game, player);
                break;
        }
        assert message != null;
        game.getState().setCurrentStep(message.getCurrentStep());
        if (message.getCurrentStep() == GameStep.LOBBY) {
            lobby.reset();
        }

    }
    private DisconnectMessage handleGameDisconnection(String playerSession, Lobby lobby, Game game, Player player) {
        DisconnectMessage message;
        message = GameDisconnector.getMessage(player, game);
        sendToAll(message, lobby, playerSession);
        if(!GameDisconnector.isEnoughPlayers(game)){
            Player newSpymaster = GameDisconnector.prepareNewSpymaster(player, game);
            sendMessageToNewSpymaster(game, newSpymaster);
        }
        return message;
    }

    private DisconnectMessage handleVotingDisconnection(String playerSession, Lobby lobby, Game game, Player player) {
        DisconnectMessage message;
        message = VotingDisconnector.getMessage(player, game);
        sendToAll(message, lobby, playerSession);
        return message;
    }

    private DisconnectMessage handleLobbyDisconnection(String playerSession, Lobby lobby, LobbyPlayer lobbyPlayer) {
        DisconnectMessage message;
        message = LobbyDisconnector.getMessage(lobbyPlayer);
        sendToAll(message, lobby, playerSession);
        return message;
    }

    private void sendToAll(DisconnectMessage message, Lobby lobby, String disconnectedSessionId){
        List<LobbyPlayer> remainingPlayers = lobby.getPlayers().stream().filter(x-> !x.getSessionId().equals(disconnectedSessionId)).collect(Collectors.toList());
        List<String> remainingPlayersSessions = PlayerSessionId.getSessionsIds(remainingPlayers);
        messageManager.sendToAll(message, DISCONNECT_PATH, remainingPlayersSessions);
    }

    private void sendMessageToNewSpymaster(Game game, Player newSpymaster) {
        StartGameMessage messageForNewSpymaster = StartGameMessageCreator.create(newSpymaster.getRole(), newSpymaster, game);
        messageManager.send(messageForNewSpymaster, newSpymaster.getSessionId(), NEW_SPYMASTER_PATH);
    }
}
