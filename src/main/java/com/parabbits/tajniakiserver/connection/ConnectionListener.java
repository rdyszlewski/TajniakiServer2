package com.parabbits.tajniakiserver.connection;


import com.parabbits.tajniakiserver.game.Game;
import com.parabbits.tajniakiserver.game.models.Player;
import com.parabbits.tajniakiserver.game.models.Role;
import com.parabbits.tajniakiserver.game.models.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
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
        needToTest(sessionId);
    }

    private void needToTest(String sessionId) {
        Player player  = new Player(sessionId, counter, "g"+connectedSessions.size());
        player.setTeam(Team.BLUE);
        if(player.getId()%3==0){
            player.setRole(Role.BOSS);
        } else {
            player.setRole(Role.PLAYER);
        }
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