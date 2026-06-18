package com.denjossal.study.dsa.tree;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TrieTest {

    private Trie trie;

    @BeforeEach
    void setUp() {
        trie = new Trie();
        trie.insert("apple");
        trie.insert("app");
        trie.insert("application");
        trie.insert("banana");
    }

    @Test
    void shouldSearchExactWord() {
        assertThat(trie.search("apple")).isTrue();
        assertThat(trie.search("app")).isTrue();
        assertThat(trie.search("application")).isTrue();
        assertThat(trie.search("banana")).isTrue();
    }

    @Test
    void shouldNotFindPartialWord() {
        assertThat(trie.search("appl")).isFalse();
        assertThat(trie.search("ban")).isFalse();
    }

    @Test
    void shouldNotFindAbsentWord() {
        assertThat(trie.search("orange")).isFalse();
        assertThat(trie.search("applications")).isFalse();
    }

    @Test
    void shouldFindPrefix() {
        assertThat(trie.startsWith("app")).isTrue();
        assertThat(trie.startsWith("appl")).isTrue();
        assertThat(trie.startsWith("ban")).isTrue();
        assertThat(trie.startsWith("b")).isTrue();
    }

    @Test
    void shouldNotFindAbsentPrefix() {
        assertThat(trie.startsWith("cat")).isFalse();
        assertThat(trie.startsWith("x")).isFalse();
    }

    @Test
    void shouldCountWordsWithPrefix() {
        assertThat(trie.countWordsWithPrefix("app")).isEqualTo(3);
        assertThat(trie.countWordsWithPrefix("apple")).isEqualTo(1);
        assertThat(trie.countWordsWithPrefix("b")).isEqualTo(1);
        assertThat(trie.countWordsWithPrefix("z")).isEqualTo(0);
    }

    @Test
    void shouldDeleteWord() {
        trie.delete("app");

        assertThat(trie.search("app")).isFalse();
        assertThat(trie.search("apple")).isTrue();
        assertThat(trie.search("application")).isTrue();
    }

    @Test
    void shouldDeleteLeafWordAndCleanup() {
        trie.delete("application");

        assertThat(trie.search("application")).isFalse();
        assertThat(trie.search("apple")).isTrue();
        assertThat(trie.search("app")).isTrue();
    }

    @Test
    void shouldReturnFalseOnDeleteNonExistent() {
        assertThat(trie.delete("xyz")).isFalse();
        assertThat(trie.delete("apples")).isFalse();
    }

    @Test
    void shouldWorkOnEmptyTrie() {
        var empty = new Trie();
        assertThat(empty.search("any")).isFalse();
        assertThat(empty.startsWith("a")).isFalse();
        assertThat(empty.countWordsWithPrefix("")).isEqualTo(0);
    }
}
