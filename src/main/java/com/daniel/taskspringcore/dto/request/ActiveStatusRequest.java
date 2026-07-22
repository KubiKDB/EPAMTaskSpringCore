package com.daniel.taskspringcore.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Activate/de-activate request (not idempotent)")
public record ActiveStatusRequest(
        @Schema(description = "Desired active state", example = "true")
        @NotNull Boolean isActive) {
}
