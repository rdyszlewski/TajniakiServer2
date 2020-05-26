package com.parabbits.tajniakiserver.summary;

import com.parabbits.tajniakiserver.game.models.SummaryAnswer;
import com.parabbits.tajniakiserver.game.models.Team;
import com.parabbits.tajniakiserver.game.models.CardColor;

import java.util.ArrayList;
import java.util.List;

public class SummaryEntry {

    private String question;
    private int number;
    private List<SummaryAnswer> answers = new ArrayList<>();
    private boolean omitted;
    private Team team;

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

    public List<SummaryAnswer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<SummaryAnswer> answers) {
        this.answers = answers;
    }

    public void addAnswer(String answer, CardColor color){
        answers.add(new SummaryAnswer(answer, color));
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
