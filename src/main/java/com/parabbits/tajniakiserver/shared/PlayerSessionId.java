package com.parabbits.tajniakiserver.shared;

import com.parabbits.tajniakiserver.game.models.Player;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerSessionId {

    public static List<String> getSessionsIds(Collection<? extends Player> players) {
        return players.stream().map(Player::getSessionId).collect(Collectors.toList());
    }


}
