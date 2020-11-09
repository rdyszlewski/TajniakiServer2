package com.parabbits.tajniakiserver.lobby;

import com.parabbits.tajniakiserver.game.models.Team;

public class TeamConverter {

    public static Team getTeam(String team){
        switch (team){
            case "RED":
                return Team.RED;
            case "BLUE":
                return Team.BLUE;
            default:
                return Team.LACK;
        }
    }
}
