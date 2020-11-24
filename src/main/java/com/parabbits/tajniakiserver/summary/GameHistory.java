package com.parabbits.tajniakiserver.summary;

import com.parabbits.tajniakiserver.game.models.CardColor;
import com.parabbits.tajniakiserver.game.models.Team;

import java.util.ArrayList;
import java.util.List;

public class GameHistory {

    private final List<SummaryEntry> history = new ArrayList<>();
    private List<String> blueWords;
    private List<String> redWords;
    private String killer;

    private SummaryEntry currentEntry;

    public void addQuestion(String question, int number, Team team) {
        SummaryEntry entry = new SummaryEntry();
        entry.setQuestion(question);
        entry.setTeam(team);
        entry.setNumber(number);
        currentEntry = entry;
        history.add(entry);

    }

    public void addAnswer(String answer, CardColor color) {
        currentEntry.addAnswer(answer, color);
        System.out.println(answer);
    }

    public void setWords(List<String> words, Team team) {
        if (team == Team.BLUE) {
            blueWords = words;
        } else if (team == Team.RED) {
            redWords = words;
        }
    }

    public List<String> getWords(Team team) {
        switch (team) {
            case BLUE:
                return blueWords;
            case RED:
                return redWords;
        }
        return new ArrayList<>();
    }

    public List<SummaryEntry> getEntries() {
        return history;
    }

    public String getKiller() {
        return killer;
    }

    public void setKiller(String killer) {
        this.killer = killer;
    }
}
