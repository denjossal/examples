package com.denjossal.study.algorithms.linkedlist;

/**
 * LeetCode 143 — Reorder List (Medium)
 * Pattern: Linked List (find middle + reverse second half + merge)
 * Time: O(n)
 * Space: O(1)
 */
public class ReorderList {

    public static class ListNode {
        public int val;
        public ListNode next;

        public ListNode(int val) {
            this.val = val;
        }
    }

    public void solve(ListNode head) {
        if (head == null || head.next == null) return;

        // Find middle (slow ends at first middle for even lists)
        ListNode slow = head, fast = head;
        while (fast.next != null && fast.next.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }

        // Reverse second half
        ListNode second = reverse(slow.next);
        slow.next = null;

        // Merge alternating
        ListNode first = head;
        while (second != null) {
            ListNode tmp1 = first.next;
            ListNode tmp2 = second.next;
            first.next = second;
            second.next = tmp1;
            first = tmp1;
            second = tmp2;
        }
    }

    private ListNode reverse(ListNode head) {
        ListNode prev = null;
        while (head != null) {
            ListNode next = head.next;
            head.next = prev;
            prev = head;
            head = next;
        }
        return prev;
    }
}
