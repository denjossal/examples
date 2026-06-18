package com.denjossal.study.algorithms.arrays;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class GroupAnagramsTest {

    private final GroupAnagrams solution = new GroupAnagrams();

    @Test
    void shouldGroupAnagrams() {
        String[] input = {"eat", "tea", "tan", "ate", "nat", "bat"};
        List<List<String>> result = solution.solve(input);

        assertThat(result).hasSize(3);
        assertThat(result).anySatisfy(group -> assertThat(group).containsExactlyInAnyOrder("eat", "tea", "ate"));
        assertThat(result).anySatisfy(group -> assertThat(group).containsExactlyInAnyOrder("tan", "nat"));
        assertThat(result).anySatisfy(group -> assertThat(group).containsExactlyInAnyOrder("bat"));
    }

    @Test
    void shouldHandleEmptyString() {
        String[] input = {""};
        List<List<String>> result = solution.solve(input);

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).containsExactly("");
    }

    @Test
    void shouldHandleSingleChar() {
        String[] input = {"a"};
        List<List<String>> result = solution.solve(input);

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).containsExactly("a");
    }
}
