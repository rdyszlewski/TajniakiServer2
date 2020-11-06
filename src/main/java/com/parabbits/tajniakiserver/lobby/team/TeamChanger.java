package com.parabbits.tajniakiserver.lobby.team;

import com.parabbits.tajniakiserver.game.models.Player;
import com.parabbits.tajniakiserver.game.models.Team;

import com.parabbits.tajniakiserver.shared.game.Game;


public class TeamChanger {

    // TODO: może przenieść to do zarządzania graczami?
    public static boolean changePlayerTeam(Player player, Team team, Game game){
        if(canChangeTeam(team, game)){
            player.setTeam(team);
            return true;
        }
        return false;
    }

    public static boolean changePlayerTeam(Player player, String teamText, Game game){
        return changePlayerTeam(player, getTeam(teamText), game);
    }

    private static Team getTeam(String team) {
        switch (team) {
            case "RED":
                return Team.RED;
            case "BLUE":
                return Team.BLUE;
            default:
                return Team.LACK;
        }
    }

    private static boolean canChangeTeam(Team team, Game game) {
        if(team == Team.LACK){
            return true;
        }
        int teamSize = game.getPlayers().getPlayers(team).size();
        return teamSize < game.getSettings().getMaxTeamSize();
    }

    public static Team getSmallerTeam(Game game){
        int blueTeamSize = game.getPlayers().getTeamSize(Team.BLUE);
        int redTeamSize = game.getPlayers().getTeamSize(Team.RED);
        return blueTeamSize<redTeamSize? Team.BLUE: Team.RED;
    }

}
