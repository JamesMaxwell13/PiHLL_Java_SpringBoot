package com.sharesapp.backend.utils.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MyCacheTests {
  private static final int MAX_CACHE_SIZE = 100;
  private MyCache<Long, Object> cache;

  @BeforeEach
  public void setUp() {
    cache = new MyCache<>();
  }

  @Test
  void get() {
    Long key = 1L;
    Object value = "value";
    cache.put(key, value);
    Optional<Object> result = cache.get(key);
    assertTrue(result.isPresent());
    assertEquals(value, result.get());
  }

  @Test
  void putMaxCapacity() {
    long key;
    Object value;

    for (int i = 1; i <= MAX_CACHE_SIZE; i++) {
      key = (long) i;
      value = "Value " + i;
      cache.put(key, value);
    }

    Long newKey = (long) (MAX_CACHE_SIZE + 1);
    Object newValue = "New Value";
    cache.put(newKey, newValue);

    Optional<Object> result = cache.get(newKey);
    assertTrue(result.isPresent());
    assertEquals(newValue, result.get());

    Optional<Object> removedValue = cache.get(1L);
    assertFalse(removedValue.isPresent());
  }

  @Test
  void remove() {
    Long key = 1L;
    Object value = "value";

    cache.put(key, value);
    cache.remove(1L);
    Optional<Object> result = cache.get(key);

    assertEquals(Optional.empty(), result);
  }
}
