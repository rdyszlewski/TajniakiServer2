package com.parabbits.tajniakiserver.shared;

import com.parabbits.tajniakiserver.game.models.Player;
import com.parabbits.tajniakiserver.shared.game.GameStep;

import java.util.List;

public class DisconnectMessage {

    private Player disconnectedPlayer;
    private GameStep currentStep;
    private List<Player> players;

    public Player getDisconnectedPlayer() {
        return disconnectedPlayer;
    }

    public void setDisconnectedPlayer(Player disconnectedPlayer) {
        this.disconnectedPlayer = disconnectedPlayer;
    }

    public GameStep getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(GameStep currentStep) {
        System.out.println(currentStep);
        this.currentStep = currentStep;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }
}
