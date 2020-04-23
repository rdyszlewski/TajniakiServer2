package com.parabbits.tajniakiserver.game;

import com.parabbits.tajniakiserver.game.models.Team;
import com.parabbits.tajniakiserver.game.models.WordColor;

public class AnswerCorrectness {

    public enum Correctness{
        CORRECT,
        INCORRECT,
        KILLER
    }

    public static Correctness checkCorrectness(WordColor color, Team team){
        if(color == WordColor.KILLER){
            return Correctness.KILLER;
        }
        if((color == WordColor.BLUE && team == Team.BLUE) || (color == WordColor.RED && team == Team.RED)){
            return Correctness.CORRECT;
        }
        return Correctness.INCORRECT;
    }

    public static boolean isCorrect(WordColor color, Team team){
        return (color == WordColor.BLUE && team == Team.BLUE) || (color == WordColor.RED && team == Team.RED);
    }
}
