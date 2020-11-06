package com.parabbits.tajniakiserver.game.messages;

import com.parabbits.tajniakiserver.game.models.Card;
import com.parabbits.tajniakiserver.game.models.ClientCard;
import com.parabbits.tajniakiserver.game.models.ClientCardCreator;
import com.parabbits.tajniakiserver.game.models.Player;
import com.parabbits.tajniakiserver.shared.game.Game;

import java.util.ArrayList;
import java.util.List;

public abstract class CardMessageCreator {

    protected static List<ClientCard> prepareClientCards(List<Card> cards, Player player, Game game) {
        List<ClientCard> clientCards = new ArrayList<>();
        for (Card card : cards) {
            ClientCard clientCard = ClientCardCreator.createCard(card, game, player.getRole(), player.getTeam());
            clientCards.add(clientCard);
        }
        return clientCards;
    }
}
