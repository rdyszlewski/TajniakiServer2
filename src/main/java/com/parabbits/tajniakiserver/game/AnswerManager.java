package com.parabbits.tajniakiserver.game;

import com.parabbits.tajniakiserver.game.models.Card;
import com.parabbits.tajniakiserver.game.models.Player;
import com.parabbits.tajniakiserver.utils.GroupCounter;
import com.parabbits.tajniakiserver.utils.MapList;

import java.util.*;

public class AnswerManager {

    private final Map<Player, Card> answers = new HashMap<>();
    private final GroupCounter<Card> counter = new GroupCounter<>();

    public List<Card> setAnswer(Card card, Player player){
        Card previousCard=null;
        List<Card> editedCards = new ArrayList<>();
        if(answers.containsKey(player)){ // change answer
            previousCard = answers.get(player);
            counter.remove(previousCard);
            editedCards.add(previousCard);
            answers.remove(player);

        } if (!card.equals(previousCard)){ // add new
            answers.put(player, card);
            counter.add(card);
            editedCards.add(card);
        }
        return editedCards;
    }

    public void reset(){
        answers.clear();
    }

    public int getCounter(Card card){
        return counter.getCounter(card);
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