package com.parabbits.words_utils;

import com.parabbits.tajniakiserver.game.models.Team;
import com.parabbits.tajniakiserver.game.models.WordColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class WordColorRandom {

    public static List<WordColor> randomTeamsForWords(int numberOfWords, int wordsInFirstTeam, Team firstTeam){
        List<Integer> indices = new ArrayList<>();
        for (int i=0; i < numberOfWords; i++){
            indices.add(i);
        }
        Collections.shuffle(indices);

        List<List<Integer>> colorIndices = ListUtils.splitListBySize(indices, new int[]{wordsInFirstTeam, wordsInFirstTeam-1, 1});

        WordColor[] colors = {getFirstColor(firstTeam), getSecondColor(firstTeam), WordColor.KILLER, WordColor.NEUTRAL};
        return getTeamsForWords(numberOfWords, colorIndices, colors);
    }

    private static WordColor getFirstColor(Team team){
        return team==Team.BLUE? WordColor.BLUE : WordColor.RED;
    }

    private static WordColor getSecondColor(Team firstTeam){
        return firstTeam==Team.BLUE? WordColor.RED: WordColor.BLUE;
    }

    private static List<WordColor> getTeamsForWords(int numberOfWords, List<List<Integer>> indices, WordColor[] wordColors){
        WordColor[] resultColors = new WordColor[numberOfWords];
        for(int i=0; i<indices.size(); i++){
            List<Integer> colorIndices = indices.get(i);
            WordColor color = wordColors[i];
            for(Integer index: colorIndices){
                resultColors[index] = color;
            }
        }
        return Arrays.asList(resultColors);
    }
}