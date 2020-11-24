package com.parabbits.tajniakiserver.game.messages;

import com.parabbits.tajniakiserver.shared.game.Game;

public class SpymasterMessageCreator {
    public static SpymasterMessage create(String question, int number, Game game) {
        return new SpymasterMessage(question, number, game.getState());
    }
}
