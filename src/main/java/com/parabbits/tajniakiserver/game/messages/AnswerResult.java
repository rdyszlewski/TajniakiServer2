package com.parabbits.tajniakiserver.game.messages;

import com.parabbits.tajniakiserver.game.models.Role;
import com.parabbits.tajniakiserver.game.models.Team;
import com.parabbits.tajniakiserver.game.models.WordColor;

public class AnswerResult {

    private String word;
    private WordColor correctColor;
    private boolean correct;
    private int remainingAnswers;
    private Team currentTeam;
    private Role currentRole;


    public AnswerResult(String word, WordColor correctColor) {
        this.word = word;
        this.correctColor = correctColor;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public WordColor getCorrectColor() {
        return correctColor;
    }

    public void setCorrectColor(WordColor correctColor) {
        this.correctColor = correctColor;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public int getRemainingAnswers() {
        return remainingAnswers;
    }

    public void setRemainingAnswers(int remainingAnswers) {
        this.remainingAnswers = remainingAnswers;
    }

    public Team getCurrentTeam(){
        return currentTeam;
    }

    public void setCurrentTeam(Team team){
        this.currentTeam = team;
    }

    public Role getCurrentRole(){
        return currentRole;
    }

    public void setCurrentRole(Role role){
        this.currentRole = role;
    }
}
