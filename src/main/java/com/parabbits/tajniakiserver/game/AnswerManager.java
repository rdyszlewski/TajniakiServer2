package com.parabbits.tajniakiserver.game;

import com.parabbits.tajniakiserver.game.models.Card;
import com.parabbits.tajniakiserver.game.models.Player;
import com.parabbits.tajniakiserver.utils.GroupCounter;

import java.util.*;

public class AnswerManager {

    private Map<Player, Card> answers = new HashMap<>();
    private final GroupCounter<Card> counter = new GroupCounter<>();

    // TODO: sprawdzić, czy to działa poprawnie
    private List<Card> cardsToUpdate = new ArrayList<>();

    public void setAnswer(Card card, Player player){
        if(answers.containsKey(player)){
            counter.remove(answers.get(player));
            cardsToUpdate.add(answers.get(player));
        }
        answers.put(player, card);
        counter.add(card);
        cardsToUpdate.add(card);
    }

    public void removeAnswer(Player player){
        counter.remove(answers.get(player));
        answers.remove(player);
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
    public List<Card> popCardsToUpdate(){
        List<Card> cards = new ArrayList<>(cardsToUpdate);
        cardsToUpdate.clear();
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
