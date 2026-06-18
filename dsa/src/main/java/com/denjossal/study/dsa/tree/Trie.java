package com.denjossal.study.dsa.tree;

/**
 * Prefix tree (Trie) for efficient string prefix operations.
 *
 * Complexities (where m = word length):
 *   insert:         O(m)
 *   search:         O(m)
 *   startsWith:     O(m)
 *   delete:         O(m)
 *
 * Space: O(alphabet_size * total_characters) worst case
 */
public class Trie {

    private static final int ALPHABET_SIZE = 26;

    private final TrieNode root;

    public Trie() {
        root = new TrieNode();
    }

    public void insert(String word) {
        TrieNode current = root;
        for (char c : word.toCharArray()) {
            int idx = c - 'a';
            if (current.children[idx] == null) {
                current.children[idx] = new TrieNode();
            }
            current = current.children[idx];
        }
        current.isEndOfWord = true;
    }

    public boolean search(String word) {
        TrieNode node = findNode(word);
        return node != null && node.isEndOfWord;
    }

    public boolean startsWith(String prefix) {
        return findNode(prefix) != null;
    }

    public boolean delete(String word) {
        return deleteRec(root, word, 0);
    }

    public int countWordsWithPrefix(String prefix) {
        TrieNode node = findNode(prefix);
        if (node == null) return 0;
        return countWords(node);
    }

    private TrieNode findNode(String s) {
        TrieNode current = root;
        for (char c : s.toCharArray()) {
            int idx = c - 'a';
            if (current.children[idx] == null) return null;
            current = current.children[idx];
        }
        return current;
    }

    private boolean deleteRec(TrieNode node, String word, int depth) {
        if (node == null) return false;

        if (depth == word.length()) {
            if (!node.isEndOfWord) return false;
            node.isEndOfWord = false;
            return !hasChildren(node);
        }

        int idx = word.charAt(depth) - 'a';
        boolean shouldDeleteChild = deleteRec(node.children[idx], word, depth + 1);

        if (shouldDeleteChild) {
            node.children[idx] = null;
            return !node.isEndOfWord && !hasChildren(node);
        }
        return false;
    }

    private boolean hasChildren(TrieNode node) {
        for (TrieNode child : node.children) {
            if (child != null) return true;
        }
        return false;
    }

    private int countWords(TrieNode node) {
        int count = node.isEndOfWord ? 1 : 0;
        for (TrieNode child : node.children) {
            if (child != null) count += countWords(child);
        }
        return count;
    }

    private static class TrieNode {
        TrieNode[] children = new TrieNode[ALPHABET_SIZE];
        boolean isEndOfWord;
    }
}
