package com.parabbits.tajniakiserver.shared;

import com.parabbits.tajniakiserver.game.models.GamePlayer;

import java.util.List;

public class DisconnectMessage {

    private GamePlayer disconnectedPlayer;
    private GameStep currentStep;
    private List<GamePlayer> players;

    public GamePlayer getDisconnectedPlayer() {
        return disconnectedPlayer;
    }

    public void setDisconnectedPlayer(GamePlayer disconnectedPlayer) {
        this.disconnectedPlayer = disconnectedPlayer;
    }

    public GameStep getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(GameStep currentStep) {
        System.out.println(currentStep);
        this.currentStep = currentStep;
    }

    public List<GamePlayer> getPlayers() {
        return players;
    }

    public void setPlayers(List<GamePlayer> players) {
        this.players = players;
    }
}
