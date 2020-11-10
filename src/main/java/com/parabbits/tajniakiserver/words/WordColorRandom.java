package com.parabbits.tajniakiserver.words;

import com.parabbits.tajniakiserver.game.models.Team;
import com.parabbits.tajniakiserver.game.models.CardColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class WordColorRandom {

    public static List<CardColor> randomTeamsForWords(int numberOfWords, int wordsInFirstTeam, Team firstTeam){
        List<Integer> indices = new ArrayList<>();
        for (int i=0; i < numberOfWords; i++){
            indices.add(i);
        }
        Collections.shuffle(indices);

        List<List<Integer>> colorIndices = ListUtils.splitListBySize(indices, new int[]{wordsInFirstTeam, wordsInFirstTeam-1, 1});

        CardColor[] colors = {getFirstColor(firstTeam), getSecondColor(firstTeam), CardColor.KILLER, CardColor.NEUTRAL};
        return getTeamsForWords(numberOfWords, colorIndices, colors);
    }

    private static CardColor getFirstColor(Team team){
        return team==Team.BLUE? CardColor.BLUE : CardColor.RED;
    }

    private static CardColor getSecondColor(Team firstTeam){
        return firstTeam==Team.BLUE? CardColor.RED: CardColor.BLUE;
    }

    private static List<CardColor> getTeamsForWords(int numberOfWords, List<List<Integer>> indices, CardColor[] wordColors){
        CardColor[] resultColors = new CardColor[numberOfWords];
        for(int i=0; i<indices.size(); i++){
            List<Integer> colorIndices = indices.get(i);
            CardColor color = wordColors[i];
            for(Integer index: colorIndices){
                resultColors[index] = color;
            }
        }
        return Arrays.asList(resultColors);
    }
}