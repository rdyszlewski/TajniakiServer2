package com.parabbits.tajniakiserver.utils;

import com.parabbits.tajniakiserver.connection.HeaderUtils;
import com.parabbits.tajniakiserver.shared.game.Game;
import com.parabbits.tajniakiserver.game.models.Player;
import com.parabbits.tajniakiserver.game.models.Role;
import com.parabbits.tajniakiserver.game.models.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageManager {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void sendToAll(Object message, String path, Game game){
        getAllPlayers(game).parallelStream().forEach(player -> {
            send(message, player.getSessionId(), path);
        });
    }

    public void sendToAll(Object message, String path, List<String> sessionsId){
        sessionsId.parallelStream().forEach(s -> {
            send(message, s, path);
        });
    }

    public void sendToPlayersWithRole(Object message, Role role, String path, Game game){
        getAllPlayers(game).parallelStream().filter(player -> player.getRole() == role).forEach(player -> {
            send(message, player.getSessionId(), path);
        });
    }

    public void sendToTeam(Object message, Team team, String path, Game game){
        getAllPlayers(game).parallelStream().filter(player -> player.getTeam() == team).forEach(player -> {
            send(message, player.getSessionId(), path);
        });
    }

    private List<Player> getAllPlayers(Game game){
        return game.getPlayers().getAllPlayers();
    }

    public void sendToRoleFromTeam(Object message, Role role, Team team, String path, Game game){
        getAllPlayers(game).parallelStream().filter(player -> player.getRole() == role &&  player.getTeam() == team).forEach(player -> {
            send(message, player.getSessionId(), path);
        });
    }

    public void send(Object message, String sessionId, String path){
        messagingTemplate.convertAndSendToUser(sessionId, path, message, HeaderUtils.createHeaders(sessionId));
    }

}
