package com.parabbits.tajniakiserver.lobby;

public class LobbyReadyMessage {

    private long playerId;
    private boolean ready;

    public LobbyReadyMessage(long playerId, boolean ready) {
        this.playerId = playerId;
        this.ready = ready;
    }

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }
}
