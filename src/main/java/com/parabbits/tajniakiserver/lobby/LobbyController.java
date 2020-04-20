package com.parabbits.tajniakiserver.lobby;

import com.parabbits.tajniakiserver.game.Game;
import com.parabbits.tajniakiserver.game.models.Player;
import com.parabbits.tajniakiserver.game.models.Team;
import com.parabbits.tajniakiserver.utils.MessageManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class LobbyController {

    private final String LOBBY_START = "/lobby/players";
    private final String LOBBY_CONNECT = "/queque/connect";
    private final String LOBBY_END = "/queue/lobby/start";
    private final String LOBBY_READY = "/lobby/ready"; // TODO: zmienić to w aplikacji

    @Autowired
    public Game game;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private MessageManager messageManager;

    @PostConstruct
    private void init(){
        messageManager = new MessageManager(messagingTemplate);
    }

    @MessageMapping("/lobby/connect")
    public void connectToGame(@Payload String nickname, SimpMessageHeaderAccessor headerAccessor) throws Exception {
        String sessionId = headerAccessor.getSessionId();
        Player player = game.addPlayer(sessionId, nickname);
        messageManager.sendToAll(player.getNickname(), LOBBY_CONNECT, game);
        messageManager.send(getAllPlayersInLobby(), player.getSessionId(), LOBBY_START);
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

    // TODO: to powinno być w innym miejscu
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
    public void changeReady(@Payload boolean ready, SimpMessageHeaderAccessor headerAccessor) {
        // TODO: sprawdzić jak działa SendTo
        System.out.println("Zmiana gotowości");
        String sessionId = headerAccessor.getSessionId();
        Player player = game.getPlayer(sessionId);
        player.setReady(ready);
        if (areAllReady()) {
            finishChoosing();
        }
        messageManager.sendToAll(player.getNickname(), LOBBY_READY, game);
    }

    private void finishChoosing(){
        int TIME = 1000;
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        messageManager.sendToAll("START", LOBBY_END, game);
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
