package com.parabbits.tajniakiserver.game;


public class StartGameMessage {

    private Role role;
    private String[] words;
    private WordColor[] colors;
    private Team firstGroup;

    public Role getRole(){
        return role;
    }

    public void setRole(Role role){
        this.role = role;
    }

    public String[] getWords() {
        return words;
    }

    public void setWords(String[] words) {
        this.words = words;
    }

    public WordColor[] getColors() {
        return colors;
    }

    public void setColors(WordColor[] colors) {
        this.colors = colors;
    }

    public Team getFirstGroup() {
        return firstGroup;
    }

    public void setFirstGroup(Team firstGroup) {
        this.firstGroup = firstGroup;
    }
}