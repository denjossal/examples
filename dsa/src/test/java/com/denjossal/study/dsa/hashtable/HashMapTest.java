package com.denjossal.study.dsa.hashtable;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class HashMapTest {

    @Test
    void shouldStartEmpty() {
        var map = new HashMap<String, Integer>();
        assertThat(map.isEmpty()).isTrue();
        assertThat(map.size()).isZero();
    }

    @Test
    void shouldPutAndGet() {
        var map = new HashMap<String, Integer>();
        map.put("one", 1);
        map.put("two", 2);
        map.put("three", 3);

        assertThat(map.get("one")).isEqualTo(1);
        assertThat(map.get("two")).isEqualTo(2);
        assertThat(map.get("three")).isEqualTo(3);
        assertThat(map.size()).isEqualTo(3);
    }

    @Test
    void shouldReturnNullForMissingKey() {
        var map = new HashMap<String, Integer>();
        map.put("exists", 42);

        assertThat(map.get("missing")).isNull();
    }

    @Test
    void shouldOverwriteExistingKey() {
        var map = new HashMap<String, String>();
        map.put("key", "old");
        map.put("key", "new");

        assertThat(map.get("key")).isEqualTo("new");
        assertThat(map.size()).isEqualTo(1);
    }

    @Test
    void shouldRemoveKey() {
        var map = new HashMap<String, Integer>();
        map.put("a", 1);
        map.put("b", 2);
        map.put("c", 3);

        Integer removed = map.remove("b");

        assertThat(removed).isEqualTo(2);
        assertThat(map.size()).isEqualTo(2);
        assertThat(map.get("b")).isNull();
        assertThat(map.get("a")).isEqualTo(1);
        assertThat(map.get("c")).isEqualTo(3);
    }

    @Test
    void shouldReturnNullOnRemoveMissing() {
        var map = new HashMap<String, Integer>();
        assertThat(map.remove("nope")).isNull();
    }

    @Test
    void shouldContainsKey() {
        var map = new HashMap<String, Integer>();
        map.put("present", 1);

        assertThat(map.containsKey("present")).isTrue();
        assertThat(map.containsKey("absent")).isFalse();
    }

    @Test
    void shouldHandleNullKey() {
        var map = new HashMap<String, String>();
        map.put(null, "null-value");

        assertThat(map.get(null)).isEqualTo("null-value");
        assertThat(map.containsKey(null)).isTrue();
    }

    @Test
    void shouldHandleNullValue() {
        var map = new HashMap<String, String>();
        map.put("key", null);

        assertThat(map.get("key")).isNull();
        assertThat(map.containsKey("key")).isTrue();
    }

    @Test
    void shouldResizeOnHighLoad() {
        var map = new HashMap<Integer, Integer>();
        for (int i = 0; i < 100; i++) {
            map.put(i, i * 10);
        }

        assertThat(map.size()).isEqualTo(100);
        for (int i = 0; i < 100; i++) {
            assertThat(map.get(i)).isEqualTo(i * 10);
        }
    }

    @Test
    void shouldHandleCollisions() {
        // Keys that hash to the same bucket (same hashCode % capacity)
        var map = new HashMap<CollisionKey, String>();
        var k1 = new CollisionKey("a", 1);
        var k2 = new CollisionKey("b", 1);
        var k3 = new CollisionKey("c", 1);

        map.put(k1, "value-a");
        map.put(k2, "value-b");
        map.put(k3, "value-c");

        assertThat(map.get(k1)).isEqualTo("value-a");
        assertThat(map.get(k2)).isEqualTo("value-b");
        assertThat(map.get(k3)).isEqualTo("value-c");
        assertThat(map.size()).isEqualTo(3);
    }

    @Test
    void shouldRemoveFromCollisionChain() {
        var map = new HashMap<CollisionKey, String>();
        var k1 = new CollisionKey("a", 1);
        var k2 = new CollisionKey("b", 1);
        var k3 = new CollisionKey("c", 1);

        map.put(k1, "1");
        map.put(k2, "2");
        map.put(k3, "3");

        map.remove(k2);

        assertThat(map.get(k1)).isEqualTo("1");
        assertThat(map.get(k2)).isNull();
        assertThat(map.get(k3)).isEqualTo("3");
        assertThat(map.size()).isEqualTo(2);
    }

    /**
     * Key with controllable hashCode to force collisions in tests.
     */
    private record CollisionKey(String id, int hash) {
        @Override
        public int hashCode() {
            return hash;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof CollisionKey other)) return false;
            return id.equals(other.id);
        }
    }
}
