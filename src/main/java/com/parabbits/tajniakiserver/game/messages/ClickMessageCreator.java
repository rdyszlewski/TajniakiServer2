package com.parabbits.tajniakiserver.game.messages;

import com.parabbits.tajniakiserver.game.models.Card;
import com.parabbits.tajniakiserver.game.models.ClientCard;
import com.parabbits.tajniakiserver.game.models.ClientCardCreator;
import com.parabbits.tajniakiserver.game.models.Player;
import com.parabbits.tajniakiserver.shared.game.Game;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ClickMessageCreator extends CardMessageCreator {

    public static ClickMessage create(Player player, Game game) {
        List<Card> editedCards = game.getBoard().getAnswerManager().popCardsToUpdate(player);
        List<ClientCard> clientCards = prepareClientCards(editedCards, player, game);
        ClickMessage message = new ClickMessage(clientCards);
        List<Card> passCard = editedCards.stream().filter(x->x.getId() < 0).collect(Collectors.toList());
        if(!passCard.isEmpty()){
            ClientCard passClientCard = ClientCardCreator.createCard(passCard.get(0), game, player.getRole(), player.getTeam());
            message.setPass(passClientCard.getAnswers().size());
        }
        // TODO: dodac liczbę pominiętych
        return message;
    }

}
