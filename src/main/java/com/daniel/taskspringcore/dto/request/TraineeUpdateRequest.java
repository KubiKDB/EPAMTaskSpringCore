package com.daniel.taskspringcore.dto.request;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Update trainee profile request")
public record TraineeUpdateRequest(
        @Schema(description = "First name", example = "John")
        @NotBlank String firstName,

        @Schema(description = "Last name", example = "Smith")
        @NotBlank String lastName,

        @Schema(description = "Date of birth (optional)", example = "1990-05-17")
        LocalDate dateOfBirth,

        @Schema(description = "Address (optional)", example = "123 Main St")
        String address,

        @Schema(description = "Active flag", example = "true")
        @NotNull Boolean isActive) {
}
