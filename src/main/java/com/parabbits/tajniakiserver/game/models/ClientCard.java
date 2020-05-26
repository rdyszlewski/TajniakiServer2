package com.parabbits.tajniakiserver.game.models;

import java.util.HashSet;
import java.util.Set;

public class ClientCard extends Card{

    private Set<Long> answers;
    private Set<Long> flags;


    public ClientCard(int index, String word, CardColor color, boolean checked) {
        super(index, word, color, checked);
        answers = new HashSet<>();
        flags = new HashSet<>();
    }

    public Set<Long> getAnswers() {
        return answers;
    }

    public void setAnswers(Set<Long> answers) {
        this.answers = answers;
    }

    public Set<Long> getFlags() {
        return flags;
    }

    public void setFlags(Set<Long> flags) {
        this.flags = flags;
    }
}
