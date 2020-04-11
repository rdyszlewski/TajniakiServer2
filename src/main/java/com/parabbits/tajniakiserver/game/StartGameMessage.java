package com.parabbits.tajniakiserver.game;


public class StartGameMessage {

    private String type;
    private String[] words;
    private int[] colors;
    private int firstGroup;

    public StartGameMessage(String type){
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public int getFirstGroup() {
        return firstGroup;
    }

    public void setFirstGroup(int firstGroup) {
        this.firstGroup = firstGroup;
    }

    public int[] getColors() {
        return colors;
    }

    public void setColors(int[] colors) {
        this.colors = colors;
    }

    public String[] getWords() {
        return words;
    }

    public void setWords(String[] words) {
        this.words = words;
    }

    public void setType(String type) {
        this.type = type;
    }

   

}