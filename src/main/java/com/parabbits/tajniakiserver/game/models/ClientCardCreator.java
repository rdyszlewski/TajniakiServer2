package com.parabbits.tajniakiserver.game.models;

import com.parabbits.tajniakiserver.shared.game.Game;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ClientCardCreator {

    public static ClientCard createCard(Card card, Game game, Role role, Team team) {
        switch (role) {
            case SPYMASTER:
                return createCardForSpymaster(card, game, team);
            case ORDINARY_PLAYER:
                return createCardForOrdinaryPlayer(card, game, team);
        }
        return null;
    }

    public static List<ClientCard> createCards(List<Card> cards, Game game, Role role, Team team) {
        return cards.parallelStream()
                .map(x -> createCard(x, game, role, team))
                .collect(Collectors.toList());
    }

    private static ClientCard createCardForSpymaster(Card card, Game game, Team team) {
        return new ClientCard(card.getId(), card.getWord(), card.getColor(), card.isChecked());
    }

    private static ClientCard createCardForOrdinaryPlayer(Card card, Game game, Team team) {
        CardColor color = getPlayerCardColor(card);
        ClientCard clientCard = new ClientCard(card.getId(), card.getWord(), color, card.isChecked());
        clientCard.setAnswers(getPlayerFromAnswer(game, card));
        clientCard.setFlags(getPlayersIdsFromFlags(game, card, team));

        return clientCard;
    }

    private static CardColor getPlayerCardColor(Card card) {
        return card.isChecked() ? card.getColor() : CardColor.LACK;
    }

    private static Set<Long> getPlayerFromAnswer(Game game, Card card) {
        Set<Player> players = game.getPlayersWhoClicked(card);
        return getPlayersIds(players);
    }

    private static Set<Long> getPlayersIds(Set<Player> players) {
        return players.stream().map(Player::getId).collect(Collectors.toSet());
    }

    private static Set<Long> getPlayersIdsFromFlags(Game game, Card card, Team team) {
        Set<Player> players = game.getFlagsOwner(card, team);
        return getPlayersIds(players);
    }
}
