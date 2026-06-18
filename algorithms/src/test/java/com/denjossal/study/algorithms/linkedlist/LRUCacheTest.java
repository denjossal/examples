package com.denjossal.study.algorithms.linkedlist;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class LRUCacheTest {

    @Test
    void shouldEvictLRU() {
        var cache = new LRUCache(2);
        cache.put(1, 1);
        cache.put(2, 2);
        assertThat(cache.get(1)).isEqualTo(1);

        cache.put(3, 3); // evicts key 2
        assertThat(cache.get(2)).isEqualTo(-1);

        cache.put(4, 4); // evicts key 1
        assertThat(cache.get(1)).isEqualTo(-1);
        assertThat(cache.get(3)).isEqualTo(3);
        assertThat(cache.get(4)).isEqualTo(4);
    }

    @Test
    void shouldUpdateExistingKey() {
        var cache = new LRUCache(2);
        cache.put(1, 1);
        cache.put(1, 10);
        assertThat(cache.get(1)).isEqualTo(10);
    }

    @Test
    void shouldReturnMinusOneForMissing() {
        var cache = new LRUCache(1);
        assertThat(cache.get(99)).isEqualTo(-1);
    }
}
