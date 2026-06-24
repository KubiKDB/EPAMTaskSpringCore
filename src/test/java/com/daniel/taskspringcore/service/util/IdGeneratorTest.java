package com.daniel.taskspringcore.service.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

class IdGeneratorTest {

    @Test
    void returnsOneWhenNoExistingIds() {
        assertThat(IdGenerator.nextId(List.of())).isEqualTo("1");
    }

    @Test
    void returnsMaxPlusOne() {
        assertThat(IdGenerator.nextId(List.of("1", "2", "5"))).isEqualTo("6");
    }

    @Test
    void ignoresNonNumericAndNullIds() {
        assertThat(IdGenerator.nextId(java.util.Arrays.asList("3", "abc", null, "7")))
                .isEqualTo("8");
    }
}
