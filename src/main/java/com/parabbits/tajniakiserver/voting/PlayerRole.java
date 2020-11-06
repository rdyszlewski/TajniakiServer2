package com.parabbits.tajniakiserver.voting;

import com.parabbits.tajniakiserver.game.models.Player;
import com.parabbits.tajniakiserver.game.models.Role;
import com.parabbits.tajniakiserver.game.models.Team;
import com.parabbits.tajniakiserver.shared.game.Game;
import com.parabbits.tajniakiserver.voting.service.Voting;
import com.parabbits.tajniakiserver.voting.service.VotingPlayer;

public class PlayerRole {

    public static void setRole(Game game, Voting voting){
        VotingPlayer redBoss = voting.getWinner(Team.RED);
        VotingPlayer blueBoss = voting.getWinner(Team.BLUE);
        for(Player player: game.getPlayers().getAllPlayers()){
            Role role = isBoss(redBoss, blueBoss, player)? Role.BOSS : Role.PLAYER;
            player.setRole(role);
        }
    }

    private static boolean isBoss(VotingPlayer redBoss, VotingPlayer blueBoss, Player player) {
        return player.getId() == blueBoss.getId() || player.getId() == redBoss.getId();
    }
}
