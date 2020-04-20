package com.parabbits.tajniakiserver.game;

import com.parabbits.tajniakiserver.game.models.Role;
import com.parabbits.tajniakiserver.game.models.Team;
import com.parabbits.tajniakiserver.game.models.WordColor;

public class GameState {

    private Team currentTeam;
    private Role currentPlayer;
    private int remainingBlue;
    private int remainingRed;
    private String currentWord;
    private int remainingAnswers;
    // TODO: można dodać informacje o czasie

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

    public void setAnswerState(String word, int remainingAnswers){
        this.remainingAnswers = remainingAnswers;
        this.currentWord = word;
        this.currentPlayer = Role.PLAYER;

    }

    public boolean useCard(WordColor cardColor){
        // TODO: refaktoryzacja
        // TODO: w przypadku złej odpowiedzi ustawiać odpowiednio stan
        if(cardColor==WordColor.BLUE){
            remainingBlue--;
            if(currentTeam==Team.BLUE){
                remainingAnswers--;
                return true;
            } else {
                remainingAnswers = -1;
                changeTeams();
                return false;
            }
        } else if(cardColor == WordColor.RED){
            remainingRed--;
            if(currentTeam == Team.RED){
                remainingAnswers--;
                return true;
            } else {
                remainingAnswers = -1;
                changeTeams();
                return false;
            }
        }
        changeTeams();
        return false;
    }

    private void changeTeams(){
        System.out.println("Zmieniam drużynę");
        currentTeam = currentTeam == Team.BLUE? Team.RED : Team.BLUE;
        currentPlayer = Role.BOSS;
    }
}
