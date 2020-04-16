package com.parabbits.tajniakiserver.connection;


import com.parabbits.tajniakiserver.game.Game;
import com.parabbits.tajniakiserver.game.Player;
import com.parabbits.tajniakiserver.game.Team;
import com.parabbits.tajniakiserver.lobby.LobbyController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class ConnectionListener {

    @Autowired
    private Game game;

    private long counter = 0;
    private List<String> connectedSessions = new ArrayList<>();

    @EventListener(SessionConnectEvent.class)
    public void handleWebsocketConnectListener(SessionConnectEvent event){
        String sessionId = event.getMessage().getHeaders().get("simpSessionId").toString();
        System.out.println("Połączono " + sessionId);
        connectedSessions.add(sessionId);
        // TODO: wyłącznie do testów
        Player player  = new Player(sessionId, counter, "g"+connectedSessions.size());
        player.setTeam(Team.BLUE);
        game.addPlayer(player);
        counter++;
    }

    @EventListener(SessionDisconnectEvent.class)
    public void handleWebsocketDisconnectListener(SessionDisconnectEvent event){
        String sessionId = event.getMessage().getHeaders().get("simpSessionId").toString();
        game.removePlayer(sessionId);
        System.out.println("Rozłączono gracza " + sessionId);
        connectedSessions.remove(sessionId);
    }

    public List<String> getSessions(){
        return connectedSessions;
    }


}