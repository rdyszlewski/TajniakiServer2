package com.parabbits.tajniakiserver.connection;


import com.parabbits.tajniakiserver.shared.game.Game;
import com.parabbits.tajniakiserver.game.models.Player;
import com.parabbits.tajniakiserver.game.models.Role;
import com.parabbits.tajniakiserver.game.models.Team;
import com.parabbits.tajniakiserver.utils.MessageManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class ConnectionListener {



    @Autowired
    private Game game;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private MessageManager messageManager;

    @PostConstruct
    public void init(){
        messageManager = new MessageManager(messagingTemplate);
    }

    private long counter = 0;
    private List<String> connectedSessions = new ArrayList<>();

    @EventListener(SessionConnectEvent.class)
    public void handleWebsocketConnectListener(SessionConnectEvent event){
        String sessionId = event.getMessage().getHeaders().get("simpSessionId").toString();
        System.out.println("Połączono " + sessionId);
        connectedSessions.add(sessionId);

//        preparePlayerForVotingTest(sessionId);
        preparePlayersForGameTest(sessionId);
    }

    private void preparePlayerForVotingTest(String sessionId){
        Player player = new Player(sessionId, counter, "g"+connectedSessions.size());
        player.setTeam(Team.BLUE);
        player.setId(counter);
        game.getPlayers().addPlayer(player);
        messageManager.send(counter, player.getSessionId(), "/user/player/id");
        counter++;
    }

    private void preparePlayersForGameTest(String sessionId){
        Player player = new Player(sessionId, counter, "g"+connectedSessions.size());
        Team team = counter % 2 ==0? Team.BLUE : Team.RED;
        player.setTeam(team);
        player.setId(counter);
        if(game.getPlayers().getPlayers(team).size()==0){
            player.setRole(Role.BOSS);
        } else {
            player.setRole(Role.PLAYER);
        }
        game.getPlayers().addPlayer(player);
        messageManager.send(counter, player.getSessionId(), "/user/player/id");
        counter++;
    }

    @EventListener(SessionDisconnectEvent.class)
    public void handleWebsocketDisconnectListener(SessionDisconnectEvent event){
        // TODO: można zrobić oddzielną klasę odpowiedzialną za rozłączenia
        String sessionId = event.getMessage().getHeaders().get("simpSessionId").toString();
        System.out.println("Rozłączono gracza " + sessionId);
        Player player = game.getPlayers().getPlayer(sessionId);
        game.getPlayers().removePlayer(sessionId);

        DisconnectController.disconnectPlayer(player, game, messageManager);
        connectedSessions.remove(sessionId);
    }



    public List<String> getSessions(){
        return connectedSessions;
    }
}