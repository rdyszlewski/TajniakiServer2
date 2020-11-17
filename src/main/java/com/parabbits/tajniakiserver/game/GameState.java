package com.parabbits.tajniakiserver.game;

import com.parabbits.tajniakiserver.game.models.*;
import com.parabbits.tajniakiserver.shared.game.GameStep;


public class GameState {

    private GameStep currentStep;
    private boolean active;
    private Team currentTeam;
    private Role currentPlayer;

    private int pointsBlue;
    private int pointsRed;
    private int remainingBlue;
    private int remainingRed;

    private String currentWord;
    private int remainingAnswers;

    public GameStep getCurrentStep(){
        return currentStep;
    }

    public synchronized void setCurrentStep(GameStep currentStep){
        if(this.currentStep == null || !this.currentStep.equals(currentStep)){
            System.out.println(currentStep);
            this.currentStep = currentStep;
        }
    }

    public Team getCurrentTeam() {
        return currentTeam;
    }

    public Role getCurrentStage() {
        return currentPlayer;
    }

    public int getPointsBlue(){
        return pointsBlue;
    }

    public int getPointsRed(){
        return pointsRed;
    }

    public int getRemainings(Team team){
        switch (team){
            case BLUE:
                return remainingBlue;
            case RED:
                return remainingRed;
            default:
                throw new IllegalArgumentException();
        }
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

    public UseCardResult useCard(Card card){
        decreaseRemainingCards(card);
        ClickCorrectness correctness = CorrectnessUtil.getCorrectness(card, getCurrentTeam());
        if(correctness == ClickCorrectness.CORRECT){
            remainingAnswers--;
            addPoints(card);
        }

        if(!isEndGame(card)){
            if(isTeamChange(card, correctness)){
                changeTeams();
            }
        } else {
            active = false;
        }
        if(!isPassCard(card)){
            card.setChecked(true);
        }

        return UseCardResult.getResult(correctness, !active);
    }

    private boolean isEndGame(Card card) {
        return remainingRed==0 || remainingBlue == 0 || card.getColor() == CardColor.KILLER;
    }

    private boolean isTeamChange(Card card, ClickCorrectness correctness) {
        return correctness == ClickCorrectness.INCORRECT || remainingAnswers <= 0 || isPassCard(card);
    }

    private boolean isPassCard(Card card) {
        return card.getId() < 0;
    }

    private void addPoints(Card card) {
        switch (card.getColor()){
            case BLUE:
                pointsBlue++;
                break;
            case RED:
                pointsRed++;
        }
    }

    private void decreaseRemainingCards(Card card) {
        switch (card.getColor()){
            case BLUE:
                remainingBlue--;
                break;
            case RED:
                remainingRed--;
                break;
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
