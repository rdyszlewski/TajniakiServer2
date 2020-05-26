package com.parabbits.tajniakiserver.summary;

import com.parabbits.tajniakiserver.game.models.CardColor;
import com.parabbits.tajniakiserver.game.models.Team;

public class SummaryCard {

    private int id;
    private String word;
    private CardColor color;
    private Team team;
    private String question;

    public SummaryCard(){

    }

    public SummaryCard(int id, String word, CardColor color, Team team, String question) {
        this.id = id;
        this.word = word;
        this.color = color;
        this.team = team;
        this.question = question;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public CardColor getColor() {
        return color;
    }

    public void setColor(CardColor color) {
        this.color = color;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}
