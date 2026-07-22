package com.daniel.taskspringcore.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Update trainer profile request. Specialization is read-only and cannot be changed.")
public record TrainerUpdateRequest(
        @Schema(description = "First name", example = "Anna")
        @NotBlank String firstName,

        @Schema(description = "Last name", example = "Jones")
        @NotBlank String lastName,

        @Schema(description = "Active flag", example = "true")
        @NotNull Boolean isActive) {
}
