package com.parabbits.tajniakiserver.game.end_game;

import com.parabbits.tajniakiserver.game.models.Card;
import com.parabbits.tajniakiserver.game.models.CardColor;
import com.parabbits.tajniakiserver.game.models.EndGameCause;
import com.parabbits.tajniakiserver.game.models.Team;
import com.parabbits.tajniakiserver.shared.game.Game;

public class EndGameHelper {

    public static EndGameInfo getEndGameInfo(Game game) {
        EndGameInfo info = new EndGameInfo();
        if (game.getState().getRemainings(Team.RED) == 0) {
            setAllFound(Team.RED, info);
        } else if (game.getState().getRemainings(Team.BLUE) == 0) {
            setAllFound(Team.BLUE, info);
        } else if (isKillerChecked(game)) {
            setKillerFound(game, info);
        } else { // TODO: dodać inne powody. Np. rozłączenie
            setUnknownCause(game, info);
        }
        return info;
    }

    private static void setAllFound(Team winner, EndGameInfo info) {
        info.setCause(EndGameCause.ALL_FOUND);
        info.setWinner(winner);
    }

    private static boolean isKillerChecked(Game game) {
        for (Card card : game.getCards()) {
            if (card.getColor() == CardColor.KILLER && card.isChecked()) {
                return true;
            }
        }
        return false;
    }

    private static void setKillerFound(Game game, EndGameInfo info) {
        info.setCause(EndGameCause.KILLER);
        // TODO: sprawdzić, czy na pewno będzie to w ten sposób
        info.setWinner(game.getState().getCurrentTeam());
    }

    private static void setUnknownCause(Game game, EndGameInfo info) {
        info.setCause(EndGameCause.UNKNOWN);
        info.setWinner(getBetterTeam(game, info));
    }

    private static Team getBetterTeam(Game game, EndGameInfo info) {

        int remainingBlue = game.getState().getRemainings(Team.BLUE);
        int remainingRed = game.getState().getRemainings(Team.RED);
        if (remainingBlue == remainingRed) {
            return Team.LACK;
        } else if (remainingBlue < remainingRed) {
            return Team.BLUE;
        } else {
            return Team.RED;
        }
    }
}
