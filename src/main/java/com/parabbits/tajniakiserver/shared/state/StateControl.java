package com.parabbits.tajniakiserver.shared.state;

import com.parabbits.tajniakiserver.shared.game.Game;
import com.parabbits.tajniakiserver.shared.game.GameStep;

public class StateControl {

    public static boolean isCorrectState(GameStep step, boolean initialization, Game game){
        if(initialization){
            switch (step){
                case VOTING:
                    return game.getState().getCurrentStep().equals(GameStep.LOBBY) || game.getState().getCurrentStep().equals(GameStep.VOTING);
                case SUMMARY:
                    return game.getState().getCurrentStep().equals(GameStep.GAME) || game.getState().getCurrentStep().equals(GameStep.SUMMARY);

            }
        } else {
            return game.getState().getCurrentStep() == step;
        }
        return false;
    }
}
