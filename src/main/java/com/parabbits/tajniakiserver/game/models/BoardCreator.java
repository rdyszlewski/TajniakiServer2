package com.parabbits.tajniakiserver.game.models;

import com.parabbits.words_utils.WordColorRandom;
import com.parabbits.words_utils.WordsHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BoardCreator {

    // TODO: przenieść to do jakieś konfiguracji gry
    private final static int NUMBER_OF_CARDS = 25;
    public final static int WORDS_IN_FIRST_TEAM = 9;
    private final static String WORDS_PATH = "resources/words.txt";

    public static List<Card> createCards(Team firstTeam) throws IOException {
        List<String> words = getRandomWords();
        List<WordColor> colors = WordColorRandom.randomTeamsForWords(NUMBER_OF_CARDS, WORDS_IN_FIRST_TEAM, firstTeam);

        return buildWordCards(words, colors);
    }

    private static List<Card> buildWordCards(List<String> words, List<WordColor> colors) {
        List<Card> cards = new ArrayList<>();
        for(int i=0; i<words.size(); i++){
            String word = words.get(i);
            WordColor color = colors.get(i);
            Card card = new Card(i, word, color, false);
            cards.add(card);
        }
        return cards;
    }

    private static List<String> getRandomWords() throws IOException {
        List<String> allWords = WordsHelper.readWords(WORDS_PATH);
        List<String> randomWords = WordsHelper.randomWords(allWords, 25);
        return randomWords;
    }
}