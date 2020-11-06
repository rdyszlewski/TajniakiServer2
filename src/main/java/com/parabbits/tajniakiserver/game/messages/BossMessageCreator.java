package com.parabbits.tajniakiserver.game.messages;

import com.parabbits.tajniakiserver.shared.game.Game;

public class BossMessageCreator {
    public static BossMessage create(String question, int number, Game game) {
        BossMessage message = new BossMessage();
        message.setWord(question);
        message.setNumber(number);
        message.setGameState(game.getState());
        return message;
    }
}
