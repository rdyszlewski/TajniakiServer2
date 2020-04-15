package com.parabbits.tajniakiserver.lobby;

import com.parabbits.tajniakiserver.game.Game;
import com.parabbits.tajniakiserver.game.Player;
import com.parabbits.tajniakiserver.game.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;

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
            sendToAllExcept(player, "/queue/connect", player.getNickName());
        }
        messagingTemplate.convertAndSendToUser(player.getSessionId(), "/lobby/players", getAllPlayersInLobby(), createHeaders(player.getSessionId()));
    }

    private void sendToAllExcept(Player player, String path, Object message) {
        for (Player p : game.getPlayers()) {
            if (!p.getSessionId().equals(player.getSessionId())) {
                System.out.println("Wysyłam wiadomość o podłączeniu do " + p.getNickName());
                messagingTemplate.convertAndSendToUser(p.getSessionId(), path, message, createHeaders(p.getSessionId()));
            }
        }
    }

    // TODO: przenieść do odzielnej klasy
    private MessageHeaders createHeaders(String sessionId) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(sessionId);
        headerAccessor.setLeaveMutable(true);
        return headerAccessor.getMessageHeaders();
    }

    private List<Player> getAllPlayersInLobby() {
        return game.getPlayers();
    }

    @MessageMapping("lobby/team")
    @SendTo("/topic/lobby/team")
    public ChangeTeamMessage changeTeam(@Payload String teamText, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        Player player = game.getPlayer(sessionId);
        Team team = getTeam(teamText);
        if (canChangeTeam(team)) {
            player.setTeam(team);
        }
        return new ChangeTeamMessage(player.getNickName(), player.getTeam().toString());
//
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

    // TODO: gotowość
}
