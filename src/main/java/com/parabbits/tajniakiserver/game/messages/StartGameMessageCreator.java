package com.parabbits.tajniakiserver.game.messages;

import com.parabbits.tajniakiserver.game.models.ClientCard;
import com.parabbits.tajniakiserver.game.models.ClientCardCreator;
import com.parabbits.tajniakiserver.game.models.Player;
import com.parabbits.tajniakiserver.game.models.Role;
import com.parabbits.tajniakiserver.shared.game.Game;

import java.util.ArrayList;
import java.util.List;

public class StartGameMessageCreator {

    public static StartGameMessage create(Role role, Player player, Game game){
        StartGameMessage message = new StartGameMessage();
        message.setNickname(player.getNickname());
        message.setPlayerRole(role);
        message.setPlayerTeam(player.getTeam());
        message.setGameState(game.getState());
        List<ClientCard> cards = ClientCardCreator.createCards(game.getBoard().getCards(), game, role, player.getTeam());
        message.setCards(cards);
        message.setPlayers(new ArrayList<>(game.getPlayers().getAllPlayers()));

        return message;
    }
}
