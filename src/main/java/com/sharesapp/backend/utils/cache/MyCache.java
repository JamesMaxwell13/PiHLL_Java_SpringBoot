package com.sharesapp.backend.utils.cache;

import java.util.HashMap;
import java.util.Optional;

public class MyCache<K, V> implements GenericCache<K, V> {
    private final HashMap<K, V> cache = new HashMap<>();

    private static final int MAX_SIZE = 100;

    public void put(K key, V value) {
        if (cache.size() == MAX_SIZE) {
            clear();
        }
        cache.put(key, value);
    }

    public Optional<V> get(K key) {
        return Optional.ofNullable(cache.get(key));
    }

    public void remove(K key) {
        cache.remove(key);
    }

    public void clear() {
        cache.clear();
    }
}
