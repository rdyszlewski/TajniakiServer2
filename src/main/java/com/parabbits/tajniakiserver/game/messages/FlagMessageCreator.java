package com.parabbits.tajniakiserver.game.messages;

import com.parabbits.tajniakiserver.game.models.Card;
import com.parabbits.tajniakiserver.game.models.ClientCard;
import com.parabbits.tajniakiserver.game.models.Player;
import com.parabbits.tajniakiserver.shared.game.Game;

import java.util.Collections;
import java.util.List;

public class FlagMessageCreator extends CardMessageCreator {

     public static ClickMessage create(Player player, Card card, Game game) {
        List<Card> editedCards = Collections.singletonList(card);
        List<ClientCard> clientCards = prepareClientCards(editedCards, player, game);
        return new ClickMessage(clientCards);
    }
}
