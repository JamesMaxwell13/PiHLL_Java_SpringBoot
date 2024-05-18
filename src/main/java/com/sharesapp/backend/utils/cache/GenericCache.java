package com.sharesapp.backend.utils.cache;

import java.util.Optional;

public interface GenericCache<K, V> {

  void put(K key, V value);

  Optional<V> get(K key);

  void remove(K key);

  void clear();
}
