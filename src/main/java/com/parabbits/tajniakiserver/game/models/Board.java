package com.parabbits.tajniakiserver.game.models;

import com.parabbits.tajniakiserver.game.FlagsManager;
import com.parabbits.tajniakiserver.game.AnswerManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Board {

    private Map<String, Card> cards;
    private final AnswerManager answerManager = new AnswerManager();
    private final FlagsManager flagsManager = new FlagsManager();

    public AnswerManager getAnswerManager(){
        return answerManager;
    }

    public FlagsManager getFlagsManager() {
        return flagsManager;
    }

    public void init(Team firstTeam) throws IOException {
        List<Card> cardsList = BoardCreator.createCards(firstTeam);
        cards = new HashMap<>();
        for(Card card: cardsList){
            cards.put(card.getWord(), card);
        }
    }

    public Card getCard(String word){
        if(cards.containsKey(word)){
            return cards.get(word);
        }
        return null;
    }

    public List<Card> getCards(Role role){
        // TODO: refaktoryzacja
        if(role == Role.BOSS){
            return new ArrayList<>(cards.values());
        } else if (role == Role.PLAYER){
            List<Card> cardsList = new ArrayList<>();
            for(Card card: cards.values()){
                Card newCard = new Card(card.getIndex(), card.getWord(), WordColor.LACK, card.isChecked());
                cardsList.add(newCard);
            }
            return cardsList;
        }
        return null;
    }

    public void addFlagToCard(String string, Player player){
        Card card = getCard(string);

    }

}
