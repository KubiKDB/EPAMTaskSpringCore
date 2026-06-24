package com.daniel.taskspringcore.service.util;

import java.util.Collection;

public final class IdGenerator {

    private IdGenerator() {
    }

    public static String nextId(Collection<String> existingIds) {
        long max = existingIds.stream()
                .filter(id -> id != null && id.matches("\\d+"))
                .mapToLong(Long::parseLong)
                .max()
                .orElse(0L);
        return String.valueOf(max + 1);
    }
}