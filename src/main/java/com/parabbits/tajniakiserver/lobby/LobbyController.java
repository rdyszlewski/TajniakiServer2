package com.parabbits.tajniakiserver.lobby;

import com.parabbits.tajniakiserver.lobby.manager.Lobby;
import com.parabbits.tajniakiserver.lobby.manager.LobbyManager;
import com.parabbits.tajniakiserver.lobby.manager.LobbyPlayer;
import com.parabbits.tajniakiserver.lobby.messages.LobbyReadyMessage;
import com.parabbits.tajniakiserver.lobby.messages.StartLobbyMessage;
import com.parabbits.tajniakiserver.lobby.messages.StartLobbyMessageCreator;
import com.parabbits.tajniakiserver.lobby.messages.TeamMessage;
import com.parabbits.tajniakiserver.shared.parameters.BoolParam;
import com.parabbits.tajniakiserver.shared.parameters.IdParam;
import com.parabbits.tajniakiserver.shared.parameters.StringParam;
import com.parabbits.tajniakiserver.shared.timer.ITimerCallback;
import com.parabbits.tajniakiserver.shared.timer.TimerService;
import com.parabbits.tajniakiserver.utils.MessageManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class LobbyController {

    public static final String LOBBY_TEAM = "/lobby/team";
    public static final int FINISH_CHOOSING_TIME = 5;
    private final String LOBBY_START = "/lobby/players";
    private final String LOBBY_CONNECT = "/queue/connect";
    private final String LOBBY_END = "/queue/lobby/start";
    private final String LOBBY_READY = "/lobby/ready"; // TODO: zmienić to w aplikacji

    @Autowired
    private LobbyManager lobbyManager;

    @Autowired
    private TimerService timerService;

    @Autowired SimpMessagingTemplate messagingTemplate;

    private MessageManager messageManager;

    @PostConstruct
    private void init(){
        messageManager = new MessageManager(messagingTemplate);
    }

    @MessageMapping("/lobby/connect")
    public void connectToGame(@Payload String nickname, SimpMessageHeaderAccessor headerAccessor) throws Exception {
        Lobby lobby = lobbyManager.findFreeLobby();
        LobbyPlayer player = lobby.addPlayer(nickname, headerAccessor.getSessionId());
        sendStartLobbyMessage(player, StartLobbyMessageCreator.create(lobby, player), lobby);
    }

    private void sendStartLobbyMessage(LobbyPlayer player, StartLobbyMessage message, Lobby lobby) {
        messageManager.send(message, player.getSessionId(), LOBBY_START);
        List<String> sessionsIds = getSessionsIds(lobby);
        messageManager.sendToAll(player, LOBBY_CONNECT, sessionsIds);
    }

    @MessageMapping("/lobby/team")
    public void changeTeam(@Payload StringParam param, SimpMessageHeaderAccessor headerAccessor) {
        Lobby lobby = lobbyManager.findLobby(param.getGameId());
        LobbyPlayer player = lobby.getPlayer(headerAccessor.getSessionId());
        if(lobby.changeTeam(player, TeamConverter.getTeam(param.getValue()))){
            handleChangeTeam(lobby, player);
        }
    }

    private void handleChangeTeam(Lobby lobby, LobbyPlayer player) {
        TeamMessage message = new TeamMessage(player.getId(), player.getTeam().toString());
        List<String> sessionsIds = getSessionsIds(lobby);
        messageManager.sendToAll(message, LOBBY_TEAM, sessionsIds);
        endChoosingIfCan(lobby, sessionsIds);
    }

    private void endChoosingIfCan(Lobby lobby, List<String> sessionsIds) {
        if (lobby.canStart()) {
            finishChoosing(lobby, sessionsIds);
        }
    }

    private List<String> getSessionsIds(Lobby lobby){
        return lobby.getPlayers().stream().map(LobbyPlayer::getSessionId).collect(Collectors.toList());
    }

    @MessageMapping("/lobby/auto_team")
    public void joinAuto(@Payload IdParam param, SimpMessageHeaderAccessor headerAccessor){
        Lobby lobby = lobbyManager.findLobby(param.getGameId());
        LobbyPlayer player = lobby.getPlayer(headerAccessor.getSessionId());
        if(lobby.autoChangeTeam(player)){
            handleChangeTeam(lobby, player);
        }
    }

    @MessageMapping("/lobby/ready")
    public void changeReady(@Payload BoolParam param, SimpMessageHeaderAccessor headerAccessor) {
        Lobby lobby = lobbyManager.findLobby(param.getGameId());
        LobbyPlayer player = lobby.getPlayer(headerAccessor.getSessionId());
        if(lobby.setReady(player)){ // TODO: zrobić to, żeby się odpstykiwało
            LobbyReadyMessage message = new LobbyReadyMessage(player.getId(), player.isReady());
            List<String> sessionsIds = getSessionsIds(lobby);
            messageManager.sendToAll(message, LOBBY_READY, getSessionsIds(lobby));
            endChoosingIfCan(lobby, sessionsIds);
        }
    }

    private void finishChoosing(Lobby lobby, List<String> sessionsIds){
        timerService.startTimer(lobby.getID(), FINISH_CHOOSING_TIME, new ITimerCallback() {
            @Override
            public void onFinish() throws IOException {
                lobby.startGame();
                messageManager.sendToAll("START", LOBBY_END, sessionsIds);
            }

            @Override
            public void onTick(Long time) {
                // TODO: zrobienie odliczania
            }
        });
    }
}