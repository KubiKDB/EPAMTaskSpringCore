package com.daniel.taskspringcore.service.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.Set;

import org.junit.jupiter.api.Test;

class UserCredentialGeneratorTest {

    private final UserCredentialGenerator generator = new UserCredentialGenerator();

    @Test
    void generatesBaseUsernameWhenNoCollision() {
        String username = generator.generateUsername("John", "Smith", Collections.emptySet());
        assertThat(username).isEqualTo("John.Smith");
    }

    @Test
    void appendsSuffixWhenBaseTaken() {
        Set<String> existing = Set.of("John.Smith");
        String username = generator.generateUsername("John", "Smith", existing);
        assertThat(username).isEqualTo("John.Smith1");
    }

    @Test
    void appendsSmallestFreeSuffixOnRepeatedCollision() {
        Set<String> existing = Set.of("John.Smith", "John.Smith1", "John.Smith2");
        String username = generator.generateUsername("John", "Smith", existing);
        assertThat(username).isEqualTo("John.Smith3");
    }

    @Test
    void passwordHasExpectedLength() {
        assertThat(generator.generatePassword()).hasSize(10);
    }

    @Test
    void passwordIsAlphanumeric() {
        assertThat(generator.generatePassword()).matches("[A-Za-z0-9]{10}");
    }

    @Test
    void passwordsAreNotConstant() {
        // Extremely unlikely to collide; guards against a hardcoded/empty password.
        assertThat(generator.generatePassword()).isNotEqualTo(generator.generatePassword());
    }
}
