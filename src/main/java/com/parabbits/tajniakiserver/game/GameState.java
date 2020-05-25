package com.parabbits.tajniakiserver.game;

import com.parabbits.tajniakiserver.game.models.*;
import com.parabbits.tajniakiserver.shared.GameStep;

public class GameState {

    private GameStep currentStep;
    private boolean active;
    private Team currentTeam;
    private Role currentPlayer;
    private int remainingBlue;
    private int remainingRed;
    private String currentWord;
    private int remainingAnswers;

    public GameStep getCurrentStep(){
        return currentStep;
    }

    public void setCurrentStep(GameStep currentStep){
        this.currentStep = currentStep;
    }

    public Team getCurrentTeam() {
        return currentTeam;
    }

    public Role getCurrentStage() {
        return currentPlayer;
    }

    public int getRemainingBlue() {
        return remainingBlue;
    }

    public int getRemainingRed(){
        return remainingRed;
    }

    public String getCurrentWord(){
        return currentWord;
    }

    public int getRemainingAnswers() {
        return remainingAnswers;
    }

    public void initState(Team firstTeam, int wordsInFirstTeam){
        active = true;
        currentTeam = firstTeam;
        currentPlayer = Role.BOSS;
        remainingAnswers = -1;
        remainingBlue = firstTeam==Team.BLUE? wordsInFirstTeam: wordsInFirstTeam -1;
        remainingRed = firstTeam == Team.RED? wordsInFirstTeam: wordsInFirstTeam - 1;
    }

    public void setAnswerState(String word, int remainingAnswers){
        if(active){
            this.remainingAnswers = remainingAnswers;
            this.currentWord = word;
            this.currentPlayer = Role.PLAYER;
        }
    }

    public void useCard(Card card){
        if(!active){
            return;
        }
        if(card.getId() < 0){
            // TODO: sprawdzić to
            changeTeams();
            return;
        }
        card.setChecked(true);
        WordColor cardColor = card.getColor();
        boolean correct = AnswerCorrectness.isCorrect(cardColor, getCurrentTeam());
        handleUsingCard(correct, cardColor);
        // TODO: usunąć jakoś wszystkie znaczniki
    }

    private void handleUsingCard(boolean correct, WordColor color){
        switch (color) {
            case KILLER:
                active = false;
                break;
            case BLUE:
                remainingBlue--;
                break;
            case RED:
                remainingRed--;
                break;
        }
        if (correct) {
            remainingAnswers--;
        } else {
            changeTeams();
        }
        if(correct && remainingAnswers <=0){
            changeTeams();
        }
    }

    private void changeTeams(){
        // TODO: zmianę drużyny dodać do oodzielnej kllasy
        remainingAnswers = -1;
        currentTeam = currentTeam == Team.BLUE? Team.RED : Team.BLUE;
        currentPlayer = Role.BOSS;
        currentWord = "";
    }

    public boolean isGameActive(){
        return active;
    }
}
