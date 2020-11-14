package com.parabbits.tajniakiserver.game.messages;

import com.parabbits.tajniakiserver.shared.game.Game;

public class BossMessageCreator {
    public static BossMessage create(String question, int number, Game game) {
        return new BossMessage(question, number, game.getState());
    }
}
