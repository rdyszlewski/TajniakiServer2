package com.parabbits.tajniakiserver.lobby;

import com.parabbits.tajniakiserver.lobby.messages.LobbyReadyMessage;
import com.parabbits.tajniakiserver.lobby.messages.StartLobbyMessage;
import com.parabbits.tajniakiserver.lobby.messages.StartLobbyMessageCreator;
import com.parabbits.tajniakiserver.lobby.messages.TeamMessage;
import com.parabbits.tajniakiserver.lobby.team.LobbyHelper;
import com.parabbits.tajniakiserver.lobby.team.TeamChanger;
import com.parabbits.tajniakiserver.shared.parameters.BoolParam;
import com.parabbits.tajniakiserver.shared.parameters.IdParam;
import com.parabbits.tajniakiserver.shared.parameters.StringParam;
import com.parabbits.tajniakiserver.shared.game.Game;
import com.parabbits.tajniakiserver.game.models.Player;
import com.parabbits.tajniakiserver.game.models.Team;
import com.parabbits.tajniakiserver.shared.game.GameManager;
import com.parabbits.tajniakiserver.shared.timer.TimerService;
import com.parabbits.tajniakiserver.utils.MessageManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;

@Controller
public class LobbyController {

    public static final String LOBBY_TEAM = "/lobby/team";
    public static final int FINISH_CHOOSING_TIME = 5;
    private final String LOBBY_START = "/lobby/players";
    private final String LOBBY_CONNECT = "/queue/connect";
    private final String LOBBY_END = "/queue/lobby/start";
    private final String LOBBY_READY = "/lobby/ready"; // TODO: zmienić to w aplikacji

    @Autowired
    private GameManager gameManager;

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
        Game game = gameManager.findFreeGame();
        Player player = game.getPlayers().addPlayer(headerAccessor.getSessionId(), nickname);
        sendStartLobbyMessage(player, StartLobbyMessageCreator.create(game, player), game);
    }

    private void sendStartLobbyMessage(Player player, StartLobbyMessage message, Game game) {
        messageManager.send(message, player.getSessionId(), LOBBY_START);
        messageManager.sendToAll(player, LOBBY_CONNECT, game);
    }

    @MessageMapping("/lobby/team")
    public void changeTeam(@Payload StringParam param, SimpMessageHeaderAccessor headerAccessor) {
        Game game = gameManager.findGame(param.getGameId());
        Player player = game.getPlayers().getPlayer(headerAccessor.getSessionId());
        if(TeamChanger.changePlayerTeam(player, param.getValue(), game)){
            handleChangeTeam(game, player);
        }
    }


    @MessageMapping("/lobby/auto_team")
    public void joinAuto(@Payload IdParam param, SimpMessageHeaderAccessor headerAccessor){
        Game game = gameManager.findGame(param.getId());
        Player player = game.getPlayers().getPlayer(headerAccessor.getSessionId());
        Team smallerTeam = TeamChanger.getSmallerTeam(game);
        if(TeamChanger.changePlayerTeam(player, smallerTeam, game)){
            handleChangeTeam(game, player);
        }
    }

    private void handleChangeTeam(Game game, Player player) {
        TeamMessage message = new TeamMessage(player.getId(), player.getTeam().toString());
        messageManager.sendToAll(message, LOBBY_TEAM, game);
        // TODO: to właściwie nie powinno się pojawić w tym miejscu. Trzeba zablokować dostęp do zmiany, kiedy jest gotowy
        if (LobbyHelper.isEndChoosing(game)) {
            finishChoosing(game);
        }
    }

    @MessageMapping("/lobby/ready")
    public void changeReady(@Payload BoolParam param, SimpMessageHeaderAccessor headerAccessor) {
        Game game = gameManager.findGame(param.getGameId());
        Player player = game.getPlayers().getPlayer(headerAccessor.getSessionId());
        if (canSetReady(player)){
            setPlayerReady(param, game, player);
        }

        if (LobbyHelper.isEndChoosing(game)) {
            finishChoosing(game);
        }
    }

    private void setPlayerReady(BoolParam param, Game game, Player player) {
        boolean ready = param.getValue();
        player.setReady(ready);
        LobbyReadyMessage message = new LobbyReadyMessage(player.getId(), ready);
        messageManager.sendToAll(message, LOBBY_READY, game);
    }

    private boolean canSetReady(Player player){
        return player.getTeam() == Team.BLUE || player.getTeam() == Team.RED;
    }

    private void finishChoosing(Game game){
        timerService.startTimer(game.getID(), FINISH_CHOOSING_TIME, () -> {
            messageManager.sendToAll("START", LOBBY_END, game);
        });
    }
}