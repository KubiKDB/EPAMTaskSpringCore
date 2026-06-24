package com.daniel.taskspringcore.service.util;

import java.security.SecureRandom;
import java.util.Set;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class UserCredentialGenerator {

    private static final int PASSWORD_LENGTH = 10;
    private static final String PASSWORD_CHARS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private final SecureRandom random = new SecureRandom();

    public String generateUsername(String firstName, String lastName, Set<String> existingUsernames) {
        String base = firstName + "." + lastName;
        String candidate = base;
        int suffix = 1;
        while (existingUsernames.contains(candidate)) {
            candidate = base + suffix;
            suffix++;
        }
        log.debug("Generated username '{}' from '{} {}'", candidate, firstName, lastName);
        return candidate;
    }

    public String generatePassword() {
        StringBuilder sb = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            sb.append(PASSWORD_CHARS.charAt(random.nextInt(PASSWORD_CHARS.length())));
        }
        log.debug("Generated random {}-character password", PASSWORD_LENGTH);
        return sb.toString();
    }
}