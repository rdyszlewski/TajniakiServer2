package com.parabbits.tajniakiserver.connection;


import com.parabbits.tajniakiserver.game.messages.StartGameMessage;
import com.parabbits.tajniakiserver.game.models.Player;
import com.parabbits.tajniakiserver.game.models.Role;
import com.parabbits.tajniakiserver.game.models.Team;
import com.parabbits.tajniakiserver.shared.DisconnectMessage;
import com.parabbits.tajniakiserver.shared.game.Game;
import com.parabbits.tajniakiserver.shared.game.GameStep;
import com.parabbits.tajniakiserver.utils.MessageManager;

import java.util.ArrayList;
import java.util.List;

// TODO: zamiast statycznych metod spróbować to gdzieś wstrzyknąć
public class DisconnectController {

    private static final String DISCONNECT_PATH = "/queue/common/disconnect";
    private static final String NEW_BOSS_PATH = "/queue/game/new_boss";

    public static void disconnectPlayer(Player player, Game game, MessageManager messageManager){
        System.out.println("Wyrzucanie gracza");
        switch (game.getState().getCurrentStep()){
            case LOBBY:
                System.out.println("Wywalenie z LOBBY");
                sendDisconnectFromLobby(player, game, messageManager);
                break;
            case VOTING:
                System.out.println("Wywalenie z VOTING");
                sendDisconnectFromVoting(player, game, messageManager);
                break;
            case GAME:
                System.out.println("Wywalenie z GAME");
                sendDisconnectFromGame(player, game, messageManager);
                break;
        }
    }

    private static void sendDisconnectFromLobby(Player player, Game game, MessageManager messageManager){
        DisconnectMessage message = createDisconnectMessage(player, GameStep.LOBBY);
        messageManager.sendToAll(message, DISCONNECT_PATH, game);
    }

    private static DisconnectMessage createDisconnectMessage(Player player, GameStep step){
        DisconnectMessage message = new DisconnectMessage();
        message.setDisconnectedPlayer(player);
        message.setCurrentStep(step);
        return message;
    }

    private static void sendDisconnectFromVoting(Player player, Game game, MessageManager messageManager){
        GameStep currentStep = isTeamCorrect(player.getTeam(), game) ? GameStep.VOTING : GameStep.LOBBY;
        DisconnectMessage message = createDisconnectMessage(player, currentStep);
        game.getState().setCurrentStep(currentStep);
        game.reset();
        messageManager.sendToAll(message, DISCONNECT_PATH, game);
    }

    private static boolean isTeamCorrect(Team team, Game game){
        List<Player> teamPlayers = game.getPlayers().getPlayers(team);
        long bosses = teamPlayers.stream().filter(x-> x.getRole() == Role.BOSS).count();
        long players = teamPlayers.stream().filter(x->x.getRole() == Role.PLAYER).count();
        return bosses == 1 && players >= 1;
    }

    private static void sendDisconnectFromGame(Player player, Game game, MessageManager messageManager){
        if(player.getRole() == Role.BOSS){
            handleDisconnectBoss(player, game, messageManager);
        } else if(player.getRole() == Role.PLAYER){
            handleDisconnectPlayer(player, game, messageManager);
        }
    }

    private static void handleDisconnectPlayer(Player player, Game game, MessageManager messageManager) {
        if(!isEnoughPlayers(player, game)){
            goToStep(player, GameStep.LOBBY, game, messageManager);
        } else {
            goToStep(player, GameStep.GAME, game, messageManager);
        }
    }

    private static void handleDisconnectBoss(Player player, Game game, MessageManager messageManager) {
        if(isEnoughPlayers(player, game)){
            setNewBoss(player, game, messageManager);
        } else {
            goToStep(player, GameStep.LOBBY, game, messageManager);
        }
    }

    private static boolean isEnoughPlayers(Player player, Game game) {
        return game.getPlayers().getPlayers(player.getTeam()).size() >= game.getSettings().getMinTeamSize();
    }

    private static void setNewBoss(Player player, Game game, MessageManager messageManager) {
        Player newBoss = game.getPlayers().getPlayers(player.getTeam()).get(0);

        newBoss.setRole(Role.BOSS);
        DisconnectMessage message = createDisconnectMessage(player, GameStep.GAME);
        message.setPlayers(new ArrayList<>(game.getPlayers().getAllPlayers()));
        messageManager.sendToAll(message, DISCONNECT_PATH, game);
        // TODO: przyjrzeć się bliżej statycznej metodzie w GameController. Spróbować zrobić to inaczej
        // TODO: odkomencić to
//        StartGameMessage messageForNewBoss =  GameController.createStartGameMessage(Role.BOSS, newBoss, game);
//        messageManager.send(messageForNewBoss, newBoss.getSessionId(), NEW_BOSS_PATH);
    }


    private static void goToStep(Player player, GameStep step, Game game, MessageManager messageManager){
        DisconnectMessage message = createDisconnectMessage(player, step);
        messageManager.sendToAll(message, DISCONNECT_PATH, game);
        game.getState().setCurrentStep(step);
        game.reset();
    }

}
