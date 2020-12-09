package com.parabbits.tajniakiserver.game.messages;

import com.parabbits.tajniakiserver.game.end_game.EndGameHelper;
import com.parabbits.tajniakiserver.game.end_game.EndGameInfo;
import com.parabbits.tajniakiserver.game.models.Team;
import com.parabbits.tajniakiserver.shared.game.Game;

public class EndGameMessageCreator {
    public static EndGameMessage create(Game game) {
        EndGameInfo info = EndGameHelper.getEndGameInfo(game);
        int remainingBlue = game.getState().getRemainings(Team.BLUE);
        int remainingRed = game.getState().getRemainings(Team.RED);
        return new EndGameMessage(info.getWinner(), info.getCause(), remainingBlue, remainingRed);
    }
}
