package com.parabbits.tajniakiserver.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapList<K, V> {

    private final Map<K, List<V>> map = new HashMap<>();

    public void add(K key, V value) {
        if (!map.containsKey(key)) {
            map.put(key, new ArrayList<>());
        }
        map.get(key).add(value);
    }

    public void remove(K key, V value) {
        if (map.containsKey(key)) {
            map.get(key).remove(value);
        }
    }

    public List<V> get(K key) {
        if (map.containsKey(key)) {
            return map.get(key);
        }
        return new ArrayList<>();
    }

    public boolean isValueExists(K key, V value) {
        if (map.containsKey(key)) {
            for (V v : map.get(key)) {
                if (v.equals(value)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void clear(K key) {
        if (map.containsKey(key)) {
            map.get(key).clear();
        }
    }
}
