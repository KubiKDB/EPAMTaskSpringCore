package com.daniel.taskspringcore.web;

// Shared constants for the REST layer
public final class ApiConstants {

    public static final String AUTH_USERNAME_HEADER = "X-Auth-Username";
    public static final String AUTH_PASSWORD_HEADER = "X-Auth-Password";
    public static final String TRANSACTION_ID_HEADER = "X-Transaction-Id";
    public static final String TRANSACTION_ID_MDC_KEY = "transactionId";

    private ApiConstants() {
    }
}
