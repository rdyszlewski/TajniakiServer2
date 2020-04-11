package com.parabbits.words_utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GroupsRandom {


    public static int[] createGamePattern(int numberOfWords, int wordsInFirstGroup, Group firstGroup){
        List<Integer> indices = new ArrayList<>();
        for (int i=0; i < numberOfWords; i++){
            indices.add(i);
        }

        Collections.shuffle(indices);
        
        List<List<Integer>> groupIndices = ListUtils.splitListBySize(indices, new int[]{wordsInFirstGroup, wordsInFirstGroup-1, 1});
        int[] resultPattern = createPatternArray(numberOfWords, groupIndices,
         new Group[]{firstGroup, getSecondGroup(firstGroup), Group.KILLER, Group.NEUTRAL});

        return resultPattern;
    }

    private static Group getSecondGroup(Group firstGroup){
        switch(firstGroup){
            case BLUE:
                return Group.RED;
            case RED:
                return Group.BLUE;
            default:
                throw new IllegalArgumentException();
        }
    }

    private static int[] createPatternArray(int numberOfWords, List<List<Integer>> indices, Group[] groups){
        int[] resultPattern = new int[numberOfWords];
        for(int i =0; i<indices.size(); i++){
            List<Integer> groupIndices = indices.get(i);
            Group group = groups[i];
            for(Integer index: groupIndices){
                resultPattern[index] = group.value;
            }
        }
        return resultPattern;
    }
}