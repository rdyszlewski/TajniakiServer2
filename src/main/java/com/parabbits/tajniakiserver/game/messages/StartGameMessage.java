package com.parabbits.tajniakiserver.game.messages;


import com.parabbits.tajniakiserver.game.models.Role;
import com.parabbits.tajniakiserver.game.models.Team;
import com.parabbits.tajniakiserver.game.models.WordColor;

public class StartGameMessage {

    private Role playerRole;
    private String[] words;
    private WordColor[] colors;
    private Team firstTeam;

    public Role getPlayerRole(){
        return playerRole;
    }

    public void setPlayerRole(Role playerRole){
        this.playerRole = playerRole;
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

    public Team getFirstTeam() {
        return firstTeam;
    }

    public void setFirstTeam(Team firstTeam) {
        this.firstTeam = firstTeam;
    }
}