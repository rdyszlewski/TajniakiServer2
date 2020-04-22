package com.parabbits.tajniakiserver.game.models;

import java.util.HashSet;
import java.util.Set;

public class ClientCard extends Card{

    private Set<String> answers;
    private Set<String> flags;

    public ClientCard(int index, String word, WordColor color, boolean checked) {
        super(index, word, color, checked);
        answers = new HashSet<>();
        flags = new HashSet<>();
    }

    public Set<String> getAnswers() {
        return answers;
    }

    public void setAnswers(Set<String> answers) {
        this.answers = answers;
    }

    public Set<String> getFlags() {
        return flags;
    }

    public void setFlags(Set<String> flags) {
        this.flags = flags;
    }
}
