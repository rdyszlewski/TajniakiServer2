package com.parabbits.tajniakiserver.game.models;

public enum Team {
    BLUE(0),
    RED(1),
    LACK(2);

    private final int value;

    Team(int value) {
        this.value = value;
    }

    public Team opposite() {
        if (value == BLUE.value) {
            return RED;
        } else if (value == RED.value) {
            return BLUE;
        }
        return LACK;
    }
}
