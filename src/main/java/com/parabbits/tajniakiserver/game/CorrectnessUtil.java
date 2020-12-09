package com.parabbits.tajniakiserver.game;

import com.parabbits.tajniakiserver.game.models.Card;
import com.parabbits.tajniakiserver.game.models.CardColor;
import com.parabbits.tajniakiserver.game.models.Team;

public class CorrectnessUtil {

    public static ClickCorrectness getCorrectness(Card card, Team team) {
        if ((card.getColor() == CardColor.BLUE) && team == Team.BLUE
                || card.getColor() == CardColor.RED && team == Team.RED) {
            return ClickCorrectness.CORRECT;
        } else if (card.getColor() == CardColor.KILLER) {
            return ClickCorrectness.KILLER;
        } else if (card.getColor() == CardColor.NEUTRAL) {
            return ClickCorrectness.INCORRECT;
        } else {
            return ClickCorrectness.INCORRECT;
        }
    }

    public static boolean isCorrect(Card card, Team team) {
        ClickCorrectness correctness = getCorrectness(card, team);
        return isCorrect(correctness);
    }

    public static boolean isCorrect(ClickCorrectness correctness) {
        return correctness == ClickCorrectness.CORRECT;
    }
}
