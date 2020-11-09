package com.parabbits.tajniakiserver.connection;


import com.parabbits.tajniakiserver.game.models.Player;
import com.parabbits.tajniakiserver.game.models.Role;
import com.parabbits.tajniakiserver.game.models.Team;
import com.parabbits.tajniakiserver.lobby.manager.Lobby;
import com.parabbits.tajniakiserver.lobby.manager.LobbyManager;
import com.parabbits.tajniakiserver.lobby.manager.LobbyPlayer;
import com.parabbits.tajniakiserver.lobby.manager.LobbyPlayerAdapter;
import com.parabbits.tajniakiserver.lobby.messages.StartLobbyMessage;
import com.parabbits.tajniakiserver.lobby.messages.StartLobbyMessageCreator;
import com.parabbits.tajniakiserver.shared.game.Game;
import com.parabbits.tajniakiserver.shared.game.GameManager;
import com.parabbits.tajniakiserver.utils.MessageManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class ConnectionListener {

    private final String LOBBY_START = "/queue/lobby/players";
    private final String LOBBY_CONNECT = "/queue/connect";

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private PlayersManager playersManager;

    @Autowired
    private LobbyManager lobbyManager;

    @Autowired
    private GameManager gameManager;

    private MessageManager messageManager;

    @PostConstruct
    public void init(){
        messageManager = new MessageManager(messagingTemplate);
    }

    private long counter = 0;
    private List<String> connectedSessions = new ArrayList<>();

    @EventListener(SessionConnectEvent.class)
    public void handleWebsocketConnectListener(SessionConnectEvent event) throws IOException {
        String sessionId = event.getMessage().getHeaders().get("simpSessionId").toString();
        System.out.println("Połączono " + sessionId);
        connectedSessions.add(sessionId);

//        preparePlayerForVotingTest(sessionId);
        preparePlayersForGameTest(sessionId);
    }

//    private void preparePlayerForVotingTest(String sessionId){
//        Player player = new Player(sessionId, counter, "g"+connectedSessions.size());
//        player.setTeam(Team.BLUE);
//        player.setId(counter);
//        game.getPlayers().addPlayer(player);
//        messageManager.send(counter, player.getSessionId(), "/user/player/id");
//        counter++;
//    }

    private void preparePlayersForGameTest(String sessionId) throws IOException {
        Lobby lobby = lobbyManager.findFreeLobby();

//        Lobby lobby = playersManager.findGame(sessionId);

        // TODO: to za bardzo nie chciało działać
//        Game game = gameManager.findGame(lobby.getID());
//        Team team = counter % 2 ==0? Team.BLUE : Team.RED;
//        Player player = new Player(sessionId, counter, "g"+connectedSessions.size(), team);
//        player.setRole(isBossRole(game, team) ? Role.BOSS : Role.PLAYER);
//        game.getPlayers().addPlayer(player);
//
//        messageManager.send(counter, player.getSessionId(), "/user/player/id");
//        counter++;
//        if(counter == 4){
//            lobby.startGame();
//        }
//        LobbyPlayer lobbyPlayer = new LobbyPlayer();
//        lobbyPlayer.setId(player.getId());
//        sendStartLobbyMessage(player.getSessionId(), StartLobbyMessageCreator.create(lobby, lobbyPlayer), lobby);
    }

    private void sendStartLobbyMessage(String playersSessionId, StartLobbyMessage message, Lobby lobby) {
        messageManager.send(message, playersSessionId, LOBBY_START);
        System.out.println("Wysłano");
//        List<String> sessionsIds = PlayerSessionId.getSessionsIds(lobby.getPlayers());
//        messageManager.sendToAll(playersSessionId, LOBBY_CONNECT, sessionsIds);
    }

    private boolean isBossRole(Game game, Team team) {
        return game.getPlayers().getPlayers(team).size() == 0;
    }

    @EventListener(SessionDisconnectEvent.class)
    public void handleWebsocketDisconnectListener(SessionDisconnectEvent event){
        // TODO: można zrobić oddzielną klasę odpowiedzialną za rozłączenia
        String sessionId = event.getMessage().getHeaders().get("simpSessionId").toString();
        Lobby lobby = playersManager.findGame(sessionId);
        Game game = gameManager.findGame(lobby.getID());
        LobbyPlayer lobbyPlayer = lobby.getPlayer(sessionId);
        Player player = LobbyPlayerAdapter.createPlayer(lobbyPlayer);
        playersManager.removePlayer(sessionId);

        DisconnectController.disconnectPlayer(player, game, messageManager);
        connectedSessions.remove(sessionId);

        // TODO: bardzo mocno skrócić tę metodę
    }

    public List<String> getSessions(){
        return connectedSessions;
    }
}