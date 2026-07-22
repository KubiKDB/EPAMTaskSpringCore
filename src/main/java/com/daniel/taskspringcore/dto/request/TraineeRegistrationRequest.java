package com.daniel.taskspringcore.dto.request;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Trainee registration request")
public record TraineeRegistrationRequest(
        @Schema(description = "First name", example = "John")
        @NotBlank String firstName,

        @Schema(description = "Last name", example = "Smith")
        @NotBlank String lastName,

        @Schema(description = "Date of birth (optional)", example = "1990-05-17")
        LocalDate dateOfBirth,

        @Schema(description = "Address (optional)", example = "123 Main St")
        String address) {
}
