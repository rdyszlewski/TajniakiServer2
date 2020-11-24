package com.parabbits.tajniakiserver.game;

import com.parabbits.tajniakiserver.game.models.Card;
import com.parabbits.tajniakiserver.game.models.Player;
import com.parabbits.tajniakiserver.utils.GroupCounter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class AnswerManager {

    private final Map<Player, Card> answers = new ConcurrentHashMap<>();
    private final GroupCounter<Card> counter = new GroupCounter<>();

    public List<Card> setAnswer(Card card, Player player) {
        Card previousCard = null;
        List<Card> editedCards = new ArrayList<>();
        if (answers.containsKey(player)) { // change answer
            previousCard = answers.get(player);
            counter.remove(previousCard);
            editedCards.add(previousCard);
            answers.remove(player);

        }
        if (!card.equals(previousCard)) { // add new
            answers.put(player, card);
            counter.add(card);
            editedCards.add(card);
        }
        return editedCards;
    }

    public void reset() {
        answers.clear();
    }

    public int getCounter(Card card) {
        return counter.getCounter(card);
    }

    public Set<Player> getPlayers(Card card) {
        return answers.entrySet().parallelStream()
                .filter(entry -> entry.getValue() == card)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }
}