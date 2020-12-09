package com.parabbits.tajniakiserver.utils;

import java.util.concurrent.ConcurrentHashMap;

public class GroupCounter<T> {

    private final ConcurrentHashMap<T, Integer> counter = new ConcurrentHashMap<>();
    private volatile T bestElement;
    private volatile int bestValue;

    public void add(T element) {
        counter.compute(element, (k, v) -> v == null ? 1 : v + 1);
        synchronized (this) {
            if (counter.get(element) > bestValue) {
                bestElement = element;
                bestValue = counter.get(element);
            }
        }
    }

    public int getCounter(T element) {
        return counter.getOrDefault(element, 0);
    }

    public void remove(T element) {
        counter.remove(element);
    }

    public T getBestElement() {
        return bestElement;
    }

    public int getBestValue() {
        return bestValue;
    }

    public void reset() {
        counter.clear();
    }
}
