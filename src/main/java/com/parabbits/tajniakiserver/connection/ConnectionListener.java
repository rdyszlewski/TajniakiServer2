package com.parabbits.tajniakiserver.connection;


import com.parabbits.tajniakiserver.game.GameController;
import com.parabbits.tajniakiserver.game.messages.StartGameMessage;
import com.parabbits.tajniakiserver.shared.DisconnectMessage;
import com.parabbits.tajniakiserver.shared.Game;
import com.parabbits.tajniakiserver.game.models.Player;
import com.parabbits.tajniakiserver.game.models.Role;
import com.parabbits.tajniakiserver.game.models.Team;
import com.parabbits.tajniakiserver.shared.GameStep;
import com.parabbits.tajniakiserver.utils.MessageManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class ConnectionListener {

    private final String DISCONNECT_PATH = "/queue/common/disconnect";
    private final String NEW_BOSS_PATH = "/queue/game/new_boss";

    @Autowired
    private Game game;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private MessageManager messageManager;

    @PostConstruct
    public void init(){
        messageManager = new MessageManager(messagingTemplate);
    }

    private long counter = 0;
    private List<String> connectedSessions = new ArrayList<>();

    // TODO: po przetestowaniu usunąć
    private static int teamCounter = 0;
    private static Team currentTeam = Team.BLUE;

    @EventListener(SessionConnectEvent.class)
    public void handleWebsocketConnectListener(SessionConnectEvent event){
        String sessionId = event.getMessage().getHeaders().get("simpSessionId").toString();
        System.out.println("Połączono " + sessionId);
        connectedSessions.add(sessionId);

//        preparePlayerForVotingTest(sessionId);
//        preparePlayersForGameTest(sessionId);

    }

    private void preparePlayerForVotingTest(String sessionId){
        Player player = new Player(sessionId, counter, "g"+connectedSessions.size());
        player.setTeam(Team.BLUE);
        player.setId(counter);
        game.addPlayer(player);
        messageManager.send(counter, player.getSessionId(), "/user/player/id");
        counter++;
    }

    private void preparePlayersForGameTest(String sessionId){
        Player player = new Player(sessionId, counter, "g"+connectedSessions.size());
        Team team = counter % 2 ==0? Team.BLUE : Team.RED;
        player.setTeam(team);
        player.setId(counter);
        if(game.getPlayers(team).size()==0){
            player.setRole(Role.BOSS);
        } else {
            player.setRole(Role.PLAYER);
        }
        game.addPlayer(player);
        messageManager.send(counter, player.getSessionId(), "/user/player/id");
        counter++;
    }

    @EventListener(SessionDisconnectEvent.class)
    public void handleWebsocketDisconnectListener(SessionDisconnectEvent event){
        // TODO: można zrobić oddzielną klasę odpowiedzialną za rozłączenia
        String sessionId = event.getMessage().getHeaders().get("simpSessionId").toString();
        System.out.println("Rozłączono gracza " + sessionId);
        Player player = game.getPlayer(sessionId);
        game.removePlayer(sessionId);

        sendDisconnectMessage(player, game);
        connectedSessions.remove(sessionId);
    }

    // PRZERZUCIĆ DO INNEJ KLASY ==============================================================

    private void sendDisconnectMessage(Player player, Game game){
        System.out.println("Wysyłąm komunikat o usunięciu gracza");
        switch (game.getState().getCurrentStep()){
            case LOBBY:
                sendDisconnectFromLobby(player, game);
                break;
            case VOTING:
                sendDisconnectFromVoting(player, game);
                break;
            case GAME:
                sendDisconnectFromGame(player, game);
                break;
        }
    }

    private void sendDisconnectFromLobby(Player player, Game game){
        DisconnectMessage message = createDisconnectMessage(player, GameStep.LOBBY);
        messageManager.sendToAll(message, DISCONNECT_PATH, game);
    }

    private DisconnectMessage createDisconnectMessage(Player player, GameStep step){
        DisconnectMessage message = new DisconnectMessage();
        message.setDisconnectedPlayer(player);
        message.setCurrentStep(step);
        return message;
    }

    private void sendDisconnectFromVoting(Player player, Game game){
        GameStep currentStep = isTeamCorrect(player.getTeam(), game) ? GameStep.VOTING : GameStep.LOBBY;
        DisconnectMessage message = createDisconnectMessage(player, currentStep);
        messageManager.sendToAll(message, DISCONNECT_PATH, game);
    }

    private boolean isTeamCorrect(Team team, Game game){
        List<Player> teamPlayers = game.getPlayers(team);
        long bosses = teamPlayers.stream().filter(x-> x.getRole() == Role.BOSS).count();
        long players = teamPlayers.stream().filter(x->x.getRole() == Role.PLAYER).count();
        return bosses == 1 && players >= 1;
    }

    private void sendDisconnectFromGame(Player player, Game game){
        if(player.getRole() == Role.BOSS){
            handleDisconnectBoss(player, game);
        } else if(player.getRole() == Role.PLAYER){
            handleDisconnectPlayer(player, game);
        }
    }

    private void handleDisconnectPlayer(Player player, Game game) {
        if(!isEnoughPlayers(player, game)){
            goToStep(player, GameStep.LOBBY, game);
        } else {
            goToStep(player, GameStep.GAME, game);
        }
    }

    private void handleDisconnectBoss(Player player, Game game) {
        if(isEnoughPlayers(player, game)){
            setNewBoss(player, game);
        } else {
            goToStep(player, GameStep.LOBBY, game);
        }
    }

    private boolean isEnoughPlayers(Player player, Game game) {
        return game.getPlayers(player.getTeam()).size() >= game.getSettings().getMinTeamSize();
    }

    private void setNewBoss(Player player, Game game) {
        Player newBoss = game.getPlayers(player.getTeam()).get(0);
        newBoss.setRole(Role.BOSS);
        DisconnectMessage message = createDisconnectMessage(player, GameStep.GAME);
        message.setPlayers(new ArrayList<>(game.getPlayers()));
        messageManager.sendToAll(message, DISCONNECT_PATH, game);
        StartGameMessage messageForNewBoss =  GameController.createStartGameMessage(Role.BOSS, newBoss, game);
        messageManager.send(messageForNewBoss, newBoss.getSessionId(), NEW_BOSS_PATH); // TODO: przenieść gdzieś ścieżkę
    }


    private void goToStep(Player player, GameStep step, Game game){
        DisconnectMessage message = createDisconnectMessage(player, step);
        messageManager.sendToAll(message, DISCONNECT_PATH, game);
        game.getState().setCurrentStep(step);
    }
    // ===================================================================================

    public List<String> getSessions(){
        return connectedSessions;
    }
}