package com.parabbits.tajniakiserver.game.models;

import com.parabbits.tajniakiserver.shared.game.GameSettings;
import com.parabbits.tajniakiserver.words.WordColorRandom;
import com.parabbits.tajniakiserver.words.WordsHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BoardCreator {

    // TODO: przenieść to do jakieś konfiguracji gry
    private final static String WORDS_PATH = "resources/words.txt";

    public static List<Card> createCards(Team firstTeam, GameSettings settings) throws IOException {
        List<String> words = getRandomWords();
        List<CardColor> colors = WordColorRandom.randomTeamsForWords(settings.getNumbersOfCards(), settings.getFirstTeamWords(), firstTeam);

        return buildWordCards(words, colors);
    }

    private static List<Card> buildWordCards(List<String> words, List<CardColor> colors) {
        List<Card> cards = new ArrayList<>();
        for(int i=0; i<words.size(); i++){
            String word = words.get(i);
            CardColor color = colors.get(i);
            Card card = new Card(i, word, color, false);
            cards.add(card);
        }
        cards.add(createPassCard());
        return cards;
    }

    private static Card createPassCard(){
        return new Card(-1, "--PASS--", CardColor.LACK, false);
    }

    private static List<String> getRandomWords() throws IOException {
        List<String> allWords = WordsHelper.readWords(WORDS_PATH);
        List<String> randomWords = WordsHelper.randomWords(allWords, 25);
        return randomWords;
    }
}
