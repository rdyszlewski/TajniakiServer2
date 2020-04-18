package com.parabbits.tajniakiserver.game;

import com.parabbits.tajniakiserver.game.models.Role;
import com.parabbits.tajniakiserver.game.models.Team;

public class GameState {

    private Team currentTeam;
    private Role currentPlayer;
    private int remainingAnswers;
    private int remainingBlue;
    private int remainingRed;
    // TODO: można dodać informacje o czasie

    public Team getCurrentTeam() {
        return currentTeam;
    }

    public Role getCurrentPlayer() {
        return currentPlayer;
    }

    public int getRemainingAnswers() {
        return remainingAnswers;
    }

    public int getRemainingBlue() {
        return remainingBlue;
    }

    public int getRemainingRed(){
        return remainingRed;
    }

    public void initState(Team firstTeam, int wordsInFirstTeam){
        currentTeam = firstTeam;
        currentPlayer = Role.BOSS;
        remainingAnswers = -1;
        remainingBlue = firstTeam==Team.BLUE? wordsInFirstTeam: wordsInFirstTeam -1;
        remainingRed = firstTeam == Team.RED? wordsInFirstTeam: wordsInFirstTeam - 1;
    }

    public void nextTeam(){
        currentTeam = currentTeam == Team.BLUE? Team.RED: Team.BLUE;
        currentPlayer = Role.BOSS;
        remainingAnswers = -1;
    }

    public void setAnswerState(int remainingAnswers){
        this.remainingAnswers = remainingAnswers;
        this.currentPlayer = Role.PLAYER;
    }

    // TODO: może tutaj dodać jakieś sprawdzanie, czy odpowiedź jest odpowiednia i reagowanie na nią
}
