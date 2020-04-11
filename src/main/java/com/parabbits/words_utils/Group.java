package com.parabbits.words_utils;

public enum Group {
    RED(0),
    BLUE(1),
    KILLER(2),
    NEUTRAL(-1);

    public final int value;

    private Group(int value){
        this.value = value;
    }
}