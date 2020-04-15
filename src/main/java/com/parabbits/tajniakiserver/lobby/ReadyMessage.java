package com.parabbits.tajniakiserver.lobby;

public class ReadyMessage {

    private String nickname;
    private boolean ready;

    public ReadyMessage(String nickname, boolean ready) {
        this.nickname = nickname;
        this.ready = ready;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }
}
