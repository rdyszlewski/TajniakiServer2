package com.parabbits.tajniakiserver.history;

import com.parabbits.tajniakiserver.game.models.Team;

import java.util.ArrayList;
import java.util.List;

public class HistoryEntry {

    private String question;
    private int number;
    private List<String> answers = new ArrayList<>();
    private boolean omitted;
    private Team team;

    public void addAnswer(String answer){
        answers.add(answer);
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public void setAnswers(List<String> answers) {
        this.answers = answers;
    }

    public boolean isOmitted() {
        return omitted;
    }

    public void setOmitted(boolean omitted) {
        this.omitted = omitted;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }
}
