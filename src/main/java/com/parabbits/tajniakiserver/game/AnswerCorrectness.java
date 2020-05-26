package com.parabbits.tajniakiserver.game;

import com.parabbits.tajniakiserver.game.models.Team;
import com.parabbits.tajniakiserver.game.models.CardColor;

public class AnswerCorrectness {

    public enum Correctness{
        CORRECT,
        INCORRECT,
        KILLER
    }

    public static Correctness checkCorrectness(CardColor color, Team team){
        if(color == CardColor.KILLER){
            return Correctness.KILLER;
        }
        if((color == CardColor.BLUE && team == Team.BLUE) || (color == CardColor.RED && team == Team.RED)){
            return Correctness.CORRECT;
        }
        return Correctness.INCORRECT;
    }

    public static boolean isCorrect(CardColor color, Team team){
        return (color == CardColor.BLUE && team == Team.BLUE) || (color == CardColor.RED && team == Team.RED);
    }
}
