package com.parabbits.tajniakiserver.lobby;

import com.parabbits.tajniakiserver.connection.HeaderUtils;
import com.parabbits.tajniakiserver.game.Game;
import com.parabbits.tajniakiserver.game.Player;
import com.parabbits.tajniakiserver.game.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class LobbyController {

    @Autowired
    public Game game;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/lobby/connect")
    public void connectToGame(@Payload String nickname, SimpMessageHeaderAccessor headerAccessor) throws Exception {
        String sessionId = headerAccessor.getSessionId();
        Player player = game.addPlayer(sessionId, nickname);
        game.testPrint();

        if (player != null) {
            sendToAllExcept(player, "/queue/connect", player.getNickname());
        }
        messagingTemplate.convertAndSendToUser(player.getSessionId(), "/lobby/players", getAllPlayersInLobby(), HeaderUtils.createHeaders(player.getSessionId()));
    }

    private void sendToAllExcept(Player player, String path, Object message) {
        for (Player p : game.getPlayers()) {
            if (!p.getSessionId().equals(player.getSessionId())) {
                System.out.println("Wysyłam wiadomość o podłączeniu do " + p.getNickname());
                messagingTemplate.convertAndSendToUser(p.getSessionId(), path, message, HeaderUtils.createHeaders(p.getSessionId()));
            }
        }
    }

    private List<Player> getAllPlayersInLobby() {
        return game.getPlayers();
    }

    @MessageMapping("lobby/team")
    @SendTo("/topic/lobby/team")
    public TeamMessage changeTeam(@Payload String teamText, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        Player player = game.getPlayer(sessionId);
        Team team = getTeam(teamText);
        if (canChangeTeam(team)) {
            player.setTeam(team);
        }
        return new TeamMessage(player.getNickname(), player.getTeam().toString());
    }

    private boolean canChangeTeam(Team team) {
        // TODO: zrobić sprawdzenie, czy gracz możę dołączyć do tej drużyny
        return true;
    }

    private Team getTeam(String team) {
        switch (team) {
            case "RED":
                return Team.RED;
            case "BLUE":
                return Team.BLUE;
            default:
                return Team.OBSERVER;
        }
    }

    @MessageMapping("/lobby/ready")
    @SendTo("/topic/lobby/ready")
    public ReadyMessage changeReady(@Payload boolean ready, SimpMessageHeaderAccessor headerAccessor) {
        System.out.println("Zmiana gotowości");
        String sessionId = headerAccessor.getSessionId();
        Player player = game.getPlayer(sessionId);
        player.setReady(ready);
        if (areAllReady()) {
            finishChoosing();
        }
        return new ReadyMessage(player.getNickname(), ready);
    }

    private void finishChoosing(){
        int TIME = 1000;
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        for (Player p : game.getPlayers()) {
                            messagingTemplate.convertAndSend("/queue/lobby/start", "START");
                        }
                    }
                },
                TIME
        );
    }

    private boolean areAllReady() {
        int numPlayers = game.getPlayers().size();
        List<Player> readyPlayers = game.getPlayers().stream().filter(Player::isReady).collect(Collectors.toList());
        return numPlayers == readyPlayers.size();
    }
}
