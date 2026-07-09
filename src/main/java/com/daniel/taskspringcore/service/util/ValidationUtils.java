package com.daniel.taskspringcore.service.util;

import com.daniel.taskspringcore.exception.ValidationException;

public final class ValidationUtils {

    private ValidationUtils() {
    }

    public static void requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new ValidationException("Required field is missing or blank: " + fieldName);
        }
    }

    public static void requireNonNull(Object value, String fieldName) {
        if (value == null) {
            throw new ValidationException("Required field is missing: " + fieldName);
        }
    }
}
