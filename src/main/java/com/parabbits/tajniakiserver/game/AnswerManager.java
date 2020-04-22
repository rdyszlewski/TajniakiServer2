package com.parabbits.tajniakiserver.game;

import com.parabbits.tajniakiserver.game.models.Card;
import com.parabbits.tajniakiserver.game.models.Player;
import com.parabbits.tajniakiserver.utils.GroupCounter;
import com.parabbits.tajniakiserver.utils.MapList;

import java.util.*;

public class AnswerManager {

    private final Map<Player, Card> answers = new HashMap<>();
    private final GroupCounter<Card> counter = new GroupCounter<>();
    private final MapList<Player, Card> editedCards = new MapList<>();

    public void setAnswer(Card card, Player player){
        // TODO: refaktoryzacja
        Card previousCard=null;
        if(answers.containsKey(player)){
            previousCard = answers.get(player);
            counter.remove(previousCard);
            editedCards.add(player,previousCard);
            answers.remove(player);
        } if (!card.equals(previousCard)){
            answers.put(player, card);
            counter.add(card);
            editedCards.add(player, card);
        }
    }

    public void removeAnswer(Player player){
        counter.remove(answers.get(player));
        answers.remove(player);
        editedCards.add(player, answers.get(player));
    }

    public int getCounter(Card card){
        return counter.getCounter(card);
    }

    public boolean isValidAnswer(int playersInTeam){
        return counter.getBestValue() == playersInTeam;
    }

    public Card getValidAnswer(int teamSize){
        if(counter.getBestValue() == teamSize){
            return counter.getBestElement();
        }
        return null;
    }

    // TODO: sprawdzić, czy to się sprawdzi
    public List<Card> popCardsToUpdate(Player player){
        List<Card> cards = new ArrayList<>(editedCards.get(player));
        editedCards.clear(player);
        return cards;
    }

    public Set<Player> getPlayers(Card card){
        Set<Player> players = new HashSet<>();
        for(Map.Entry<Player, Card> entry: answers.entrySet()){
            if(entry.getValue().equals(card)){
                players.add(entry.getKey());
            }
        }
        return players;
    }
}
