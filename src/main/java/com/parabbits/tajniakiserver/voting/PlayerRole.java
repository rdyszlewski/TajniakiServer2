package com.parabbits.tajniakiserver.voting;

import com.parabbits.tajniakiserver.game.models.Player;
import com.parabbits.tajniakiserver.game.models.Role;
import com.parabbits.tajniakiserver.game.models.Team;
import com.parabbits.tajniakiserver.shared.game.Game;
import com.parabbits.tajniakiserver.voting.service.Voting;
import com.parabbits.tajniakiserver.voting.service.VotingPlayer;

public class PlayerRole {

    public static void setRole(Game game, Voting voting) {
        VotingPlayer redSpymaster = voting.getWinner(Team.RED);
        VotingPlayer blueSpymaster = voting.getWinner(Team.BLUE);
        for (Player player : game.getPlayers().getAllPlayers()) {
            Role role = isSpymaster(redSpymaster, blueSpymaster, player) ? Role.SPYMASTER : Role.ORDINARY_PLAYER;
            player.setRole(role);
        }
    }

    private static boolean isSpymaster(VotingPlayer redSpymaster, VotingPlayer blueSpymaster, Player player) {
        return player.getId() == blueSpymaster.getId() || player.getId() == redSpymaster.getId();
    }
}
