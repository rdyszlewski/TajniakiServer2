package com.parabbits.tajniakiserver.utils;

import com.parabbits.tajniakiserver.connection.HeaderUtils;
import com.parabbits.tajniakiserver.shared.game.Game;
import com.parabbits.tajniakiserver.game.models.Player;
import com.parabbits.tajniakiserver.game.models.Role;
import com.parabbits.tajniakiserver.game.models.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class MessageManager {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void sendToAll(Object message, String path, Game game){
        for(Player player: game.getPlayers().getAllPlayers()){
            send(message, player.getSessionId(), path);
        }
    }

    public void sendToAll(Object message, String path, List<String> sessionsId){
        for(String sessionId: sessionsId){
            send(message, sessionId, path);
        }
    }

    public void sendToPlayersWithRole(Object message, Role role, String path, Game game){
        for(Player player: game.getPlayers().getAllPlayers()){
            if(player.getRole()== role){
                System.out.println("Wysyłanie do użytkownika " + player.getNickname());
                send(message, player.getSessionId(), path);
            }
        }
    }

    public void sendToTeam(Object message, Team team, String path, Game game){
        for(Player player: game.getPlayers().getPlayers(team)){
            send(message, player.getSessionId(), path);
        }
    }

    public void sendToRoleFromTeam(Object message, Role role, Team team, String path, Game game){
        for(Player player: game.getPlayers().getPlayers(team)){
            if(player.getRole() == role){
                send(message, player.getSessionId(), path);
            }
        }
    }

    public void send(Object message, String sessionId, String path){
        messagingTemplate.convertAndSendToUser(sessionId, path, message, HeaderUtils.createHeaders(sessionId));
    }

}
