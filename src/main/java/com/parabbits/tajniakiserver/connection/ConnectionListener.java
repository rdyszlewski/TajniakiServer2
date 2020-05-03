package com.parabbits.tajniakiserver.connection;


import com.parabbits.tajniakiserver.shared.Game;
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

    // TODO: po przetestowaniu usunąć
    private static int teamCounter = 0;
    private static Team currentTeam = Team.BLUE;

    @EventListener(SessionConnectEvent.class)
    public void handleWebsocketConnectListener(SessionConnectEvent event){
        String sessionId = event.getMessage().getHeaders().get("simpSessionId").toString();
        System.out.println("Połączono " + sessionId);
        connectedSessions.add(sessionId);

        // TODO: wyłącznie do testów
        needToTest(sessionId);
    }

    private void needToTest(String sessionId) {
        // ustawianie graczy, tak, aby można było przetestować działanie aplikacji
        Player player  = new Player(sessionId, counter, "g"+connectedSessions.size());
        teamCounter++;
//        if(teamCounter == 3){
//            currentTeam = currentTeam==Team.BLUE? Team.RED : Team.BLUE;
//            teamCounter = 0;
//        }
//        player.setTeam(currentTeam);
        player.setTeam(Team.BLUE);
        if(player.getId()%2==0){
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