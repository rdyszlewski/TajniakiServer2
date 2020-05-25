package com.parabbits.tajniakiserver.game.models;

import com.parabbits.tajniakiserver.shared.GameSettings;
import com.parabbits.words_utils.WordColorRandom;
import com.parabbits.words_utils.WordsHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BoardCreator {

    // TODO: przenieść to do jakieś konfiguracji gry
    private final static String WORDS_PATH = "resources/words.txt";

    public static List<Card> createCards(Team firstTeam, GameSettings settings) throws IOException {
        List<String> words = getRandomWords();
        List<WordColor> colors = WordColorRandom.randomTeamsForWords(settings.getNumbersOfCards(), settings.getFirstTeamWords(), firstTeam);

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
//        cards.add(createPassCard());
        return cards;
    }

    private static Card createPassCard(){
        Card passCard = new Card(-1, "--PASS--", WordColor.LACK, false);
        return passCard;
    }

    private static List<String> getRandomWords() throws IOException {
        List<String> allWords = WordsHelper.readWords(WORDS_PATH);
        List<String> randomWords = WordsHelper.randomWords(allWords, 25);
        return randomWords;
    }
}
