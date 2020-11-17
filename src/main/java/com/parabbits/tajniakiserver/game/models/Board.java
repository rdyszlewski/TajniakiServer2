package com.parabbits.tajniakiserver.game.models;

import com.parabbits.tajniakiserver.game.FlagsManager;
import com.parabbits.tajniakiserver.game.AnswerManager;
import com.parabbits.tajniakiserver.shared.game.GameSettings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


public class Board {

    private Map<Integer, Card> cards;
    private final AnswerManager answerManager = new AnswerManager();
    private final FlagsManager flagsManager = new FlagsManager();

    public AnswerManager getAnswerManager(){
        return answerManager;
    }

    public FlagsManager getFlagsManager() {
        return flagsManager;
    }

    public void init(Team firstTeam, GameSettings settings) throws IOException {
        List<Card> cardsList = BoardCreator.createCards(firstTeam, settings);
        cards = cardsList.stream().collect(Collectors.toMap(Card::getId, Function.identity()));
    }

    public Card getCard(Integer cardId){
        if(cards.containsKey(cardId)){
            return cards.get(cardId);
        }
        return null; // pominięcie
    }

    public List<Card> getCards(){
        return new ArrayList<>(cards.values());
    }
}
