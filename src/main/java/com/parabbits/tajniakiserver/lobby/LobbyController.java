package com.parabbits.tajniakiserver.lobby;

import com.parabbits.tajniakiserver.connection.PlayersManager;
import com.parabbits.tajniakiserver.game.models.Role;
import com.parabbits.tajniakiserver.game.models.Team;
import com.parabbits.tajniakiserver.lobby.manager.Lobby;
import com.parabbits.tajniakiserver.lobby.manager.LobbyManager;
import com.parabbits.tajniakiserver.lobby.manager.LobbyPlayer;
import com.parabbits.tajniakiserver.lobby.messages.*;
import com.parabbits.tajniakiserver.shared.PlayerSessionId;
import com.parabbits.tajniakiserver.shared.messeges.Message;
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
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class LobbyController {

    public static final String LOBBY_TEAM = "/lobby/team";
    public static final int FINISH_CHOOSING_TIME = 5;
    private final String LOBBY_START = "/lobby/players";
    private final String LOBBY_CONNECT = "/queue/connect";
    private final String LOBBY_END = "/queue/lobby/start";
    private final String LOBBY_READY = "/lobby/ready";

    @Autowired
    private LobbyManager lobbyManager;

    @Autowired
    private TimerService timerService;

    @Autowired
    private MessageManager messageManager;

    @Autowired
    private PlayersManager playersManager;

    @MessageMapping("/lobby/connect")
    public void connectToGame(@Payload String nickname, SimpMessageHeaderAccessor headerAccessor) throws Exception {
        Lobby lobby = lobbyManager.findFreeLobby();
        String sessionId = headerAccessor.getSessionId();
        LobbyPlayer player;
        if (!lobby.containPlayer(headerAccessor.getSessionId())) {
            player = lobby.addPlayer(nickname, sessionId);
            playersManager.addPlayer(sessionId, lobby);
        } else {
            player = lobby.getPlayer(sessionId);
        }
        sendStartLobbyMessage(player, StartLobbyMessageCreator.create(lobby, player), lobby);
    }

    private void sendStartLobbyMessage(LobbyPlayer player, StartLobbyMessage message, Lobby lobby) {
        messageManager.send(message, player.getSessionId(), LOBBY_START);
        List<String> sessionsIds = PlayerSessionId.getSessionsIds(lobby.getPlayers());
        messageManager.sendToAll(player, LOBBY_CONNECT, sessionsIds);
    }

    @MessageMapping("/lobby/team")
    public void changeTeam(@Payload StringParam param, SimpMessageHeaderAccessor headerAccessor) {
        Lobby lobby = lobbyManager.findLobby(param.getGameId());
        LobbyPlayer player = lobby.getPlayer(headerAccessor.getSessionId());
        if (lobby.changeTeam(player, TeamConverter.getTeam(param.getValue()))) {
            handleChangeTeam(lobby, player);
        }
    }

    private void handleChangeTeam(Lobby lobby, LobbyPlayer player) {
        TeamMessage message = new TeamMessage(player.getId(), player.getTeam().toString());
        List<String> sessionsIds = PlayerSessionId.getSessionsIds(lobby.getPlayers());
        messageManager.sendToAll(message, LOBBY_TEAM, sessionsIds);
        endChoosingIfCan(lobby, sessionsIds);
    }

    private void endChoosingIfCan(Lobby lobby, List<String> sessionsIds) {
        if (lobby.canStart()) {
            finishChoosing(lobby, sessionsIds);
        }
    }


    @MessageMapping("/lobby/auto_team")
    public void joinAuto(@Payload IdParam param, SimpMessageHeaderAccessor headerAccessor) {
        Lobby lobby = lobbyManager.findLobby(param.getGameId());
        LobbyPlayer player = lobby.getPlayer(headerAccessor.getSessionId());
        if (lobby.autoChangeTeam(player)) {
            handleChangeTeam(lobby, player);
        }
    }

    @MessageMapping("/lobby/ready")
    public void changeReady(@Payload BoolParam param, SimpMessageHeaderAccessor headerAccessor) {
        Lobby lobby = lobbyManager.findLobby(param.getGameId());
        LobbyPlayer player = lobby.getPlayer(headerAccessor.getSessionId());
        if (lobby.setReady(player)) { // TODO: zrobić to, żeby się odpstykiwało
            LobbyReadyMessage message = new LobbyReadyMessage(player.getId(), player.isReady());
            List<String> sessionsIds = PlayerSessionId.getSessionsIds(lobby.getPlayers());
            messageManager.sendToAll(message, LOBBY_READY, sessionsIds);
            endChoosingIfCan(lobby, sessionsIds);
        }
    }

    private void finishChoosing(Lobby lobby, List<String> sessionsIds) {
        timerService.startTimer(lobby.getID(), FINISH_CHOOSING_TIME, new ITimerCallback() {
            @Override
            public void onFinish() throws IOException {
                lobby.startGame();
                messageManager.sendToAll(new Message("START"), LOBBY_END, sessionsIds);
            }

            @Override
            public void onTick(Long time) {
                // TODO: zrobienie odliczania
            }
        });
    }

    @MessageMapping("/test/start_ready")
    public void readyTest(@Payload String param, SimpMessageHeaderAccessor headerAccessor) throws IOException {
        Lobby lobby = lobbyManager.findFreeLobby();
        LobbyPlayer player = lobby.addPlayer("Jacuś", headerAccessor.getSessionId());
        Team team = lobby.getPlayersCount() % 2 == 0 ? Team.BLUE : Team.RED;
        player.setRole(isSpymasterRole(lobby, team) ? Role.SPYMASTER : Role.ORDINARY_PLAYER);
        player.setTeam(team);
        playersManager.addPlayer(player.getSessionId(), lobby);
        StartLobbyMessage message = new StartLobbyMessage(new ArrayList<>(), null);
        message.setGameId(lobby.getID());
        message.setPlayerId(player.getId());
        messageManager.send(message, headerAccessor.getSessionId(), "/queue/test/start");
    }

    @MessageMapping("/test/start")
    public void startTest(@Payload IdParam param, SimpMessageHeaderAccessor headerAccessor) throws IOException {
        Lobby lobby = lobbyManager.findLobby(param.getGameId());
        lobby.startGame();
        List<String> sessionIds = PlayerSessionId.getSessionsIds(lobby.getPlayers());
        messageManager.sendToAll(new Message("Start"), "/queue/test/startGame", sessionIds);
    }

    private boolean isSpymasterRole(Lobby lobby, Team team) {
        return lobby.getPlayers().stream().noneMatch(x -> x.getTeam() == team);
    }
}