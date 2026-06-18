package com.denjossal.study.algorithms.linkedlist;

import com.denjossal.study.algorithms.linkedlist.ReorderList.ListNode;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class ReorderListTest {

    private final ReorderList solution = new ReorderList();

    @Test
    void shouldReorderEvenLength() {
        ListNode head = buildList(1, 2, 3, 4);
        solution.solve(head);
        assertThat(toList(head)).containsExactly(1, 4, 2, 3);
    }

    @Test
    void shouldReorderOddLength() {
        ListNode head = buildList(1, 2, 3, 4, 5);
        solution.solve(head);
        assertThat(toList(head)).containsExactly(1, 5, 2, 4, 3);
    }

    @Test
    void shouldHandleSingleNode() {
        ListNode head = buildList(1);
        solution.solve(head);
        assertThat(toList(head)).containsExactly(1);
    }

    private ListNode buildList(int... values) {
        ListNode dummy = new ListNode(0);
        ListNode current = dummy;
        for (int v : values) {
            current.next = new ListNode(v);
            current = current.next;
        }
        return dummy.next;
    }

    private List<Integer> toList(ListNode head) {
        List<Integer> result = new ArrayList<>();
        while (head != null) {
            result.add(head.val);
            head = head.next;
        }
        return result;
    }
}
