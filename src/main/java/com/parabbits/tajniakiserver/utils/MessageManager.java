package com.parabbits.tajniakiserver.utils;

import com.parabbits.tajniakiserver.connection.HeaderUtils;
import com.parabbits.tajniakiserver.shared.Game;
import com.parabbits.tajniakiserver.game.models.Player;
import com.parabbits.tajniakiserver.game.models.Role;
import com.parabbits.tajniakiserver.game.models.Team;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public class MessageManager {

    private final SimpMessagingTemplate messagingTemplate;

    public MessageManager(SimpMessagingTemplate messagingTemplate){
        this.messagingTemplate = messagingTemplate;
    }

    public void sendToAll(Object message, String path, Game game){
        for(Player player: game.getPlayers()){
            send(message, player.getSessionId(), path);
        }
    }

    public void sendToPlayersWithRole(Object message, Role role, String path, Game game){
        for(Player player: game.getPlayers()){
            if(player.getRole()== role){
                System.out.println("Wysyłanie do użytkownika " + player.getNickname());
                send(message, player.getSessionId(), path);
            }
        }
    }

    public void sendToTeam(Object message, Team team, String path, Game game){
        for(Player player: game.getPlayers(team)){
            send(message, player.getSessionId(), path);
        }
    }

    public void sendToRoleFromTeam(Object message, Role role, Team team, String path, Game game){
        for(Player player: game.getPlayers(team)){
            if(player.getRole() == role){
                send(message, player.getSessionId(), path);
            }
        }
    }

    public void send(Object message, String sessionId, String path){
        messagingTemplate.convertAndSendToUser(sessionId, path, message, HeaderUtils.createHeaders(sessionId));
    }

}
