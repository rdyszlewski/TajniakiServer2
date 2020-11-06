package com.parabbits.tajniakiserver.game.messages;

import com.parabbits.tajniakiserver.game.end_game.EndGameHelper;
import com.parabbits.tajniakiserver.game.end_game.EndGameInfo;
import com.parabbits.tajniakiserver.shared.game.Game;

public class EndGameMessageCreator {
    public static EndGameMessage create(Game game){
        EndGameMessage message = new EndGameMessage();
        EndGameInfo info = EndGameHelper.getEndGameInfo(game);
        message.setCause(info.getCause());
        message.setWinner(info.getWinner());
        return message;
    }
}
