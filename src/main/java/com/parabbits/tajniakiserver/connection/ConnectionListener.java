package com.parabbits.tajniakiserver.connection;


import com.parabbits.tajniakiserver.lobby.manager.Lobby;
import com.parabbits.tajniakiserver.lobby.manager.LobbyManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.io.IOException;


@Configuration
public class ConnectionListener {

    private final String LOBBY_START = "/queue/lobby/players";
    private final String LOBBY_CONNECT = "/queue/connect";

    @Autowired
    private PlayersManager playersManager;

    @Autowired
    private LobbyManager lobbyManager;

    @Autowired
    private DisconnectController disconnectController;


    @EventListener(SessionConnectEvent.class)
    public void handleWebsocketConnectListener(SessionConnectEvent event) throws IOException {
        String sessionId = event.getMessage().getHeaders().get("simpSessionId").toString();
        System.out.println("Połączono " + sessionId);
    }

    @EventListener(SessionDisconnectEvent.class)
    public void handleWebsocketDisconnectListener(SessionDisconnectEvent event) {
        String sessionId = event.getMessage().getHeaders().get("simpSessionId").toString();
        Lobby lobby = playersManager.findGame(sessionId);
        disconnectController.disconnectPlayer(sessionId, lobby.getID());
    }
}