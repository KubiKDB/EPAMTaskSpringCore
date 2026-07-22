package com.daniel.taskspringcore.dto.response;

import java.time.OffsetDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Standard error response")
public record ApiError(OffsetDateTime timestamp, int status, String error,
                       String message, String transactionId, String path) {
}
