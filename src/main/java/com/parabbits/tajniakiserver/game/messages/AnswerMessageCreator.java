package com.parabbits.tajniakiserver.game.messages;

import com.parabbits.tajniakiserver.game.models.*;
import com.parabbits.tajniakiserver.shared.game.Game;

import java.util.List;

public class AnswerMessageCreator {

    public static AnswerMessage create(List<Card> cardsToUpdate, boolean correct, Player player, Role role, Game game) {
        List<ClientCard> cards = ClientCardCreator.createCards( cardsToUpdate, game, role, player.getTeam());
        return new AnswerMessage(cards, correct, game.getState());
    }
}
