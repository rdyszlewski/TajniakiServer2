package com.parabbits.tajniakiserver.utils;

import java.util.HashMap;
import java.util.Map;

public class GroupCounter<T> {

    private final Map<T, Integer> counter = new HashMap<>();
    private T bestElement;
    private int bestValue;

    public void add(T element){
        if(counter.containsKey(element)){
            counter.put(element, counter.get(element) + 1);
        } else{
            counter.put(element, 1);
        }
        if(counter.get(element) > bestValue){
            bestElement = element;
            bestValue = counter.get(element);
        }
    }

    public int getCounter(T element){
        if(counter.containsKey(element)){
            return counter.get(element);
        }
        return 0;
    }

    public void remove(T element){
        counter.remove(element);
    }

    public T getBestElement(){
        return bestElement;
    }

    public int getBestValue(){
        return bestValue;
    }

    public void reset(){
        counter.clear();
    }
}
