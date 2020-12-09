package com.parabbits.tajniakiserver.game.messages;

import com.parabbits.tajniakiserver.game.GameState;
import com.parabbits.tajniakiserver.game.models.Role;
import com.parabbits.tajniakiserver.game.models.Team;
import com.parabbits.tajniakiserver.shared.game.GameStep;

public class GameStateMessage {

    private final GameStep currentStep;
    private final Team currentTeam;
    private final Role currentRole;

    private final int remainingBlue;
    private final int remainingRed;

    private final String currentWord;
    private final int remainingAnswers;

    public GameStateMessage(GameState gameState) {
        currentStep = gameState.getCurrentStep();
        currentTeam = gameState.getCurrentTeam();
        currentRole = gameState.getCurrentStage();

        remainingBlue = gameState.getRemainings(Team.BLUE);
        remainingRed = gameState.getRemainings(Team.RED);

        currentWord = gameState.getCurrentWord();
        remainingAnswers = gameState.getRemainingAnswers();
    }

    public GameStep getCurrentStep() {
        return currentStep;
    }

    public Team getCurrentTeam() {
        return currentTeam;
    }

    public Role getCurrentRole() {
        return currentRole;
    }

    public int getRemainingBlue() {
        return remainingBlue;
    }

    public int getRemainingRed() {
        return remainingRed;
    }

    public String getCurrentWord() {
        return currentWord;
    }

    public int getRemainingAnswers() {
        return remainingAnswers;
    }
}
