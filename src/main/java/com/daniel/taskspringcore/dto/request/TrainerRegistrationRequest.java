package com.daniel.taskspringcore.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Trainer registration request")
public record TrainerRegistrationRequest(
        @Schema(description = "First name", example = "Anna")
        @NotBlank String firstName,

        @Schema(description = "Last name", example = "Jones")
        @NotBlank String lastName,

        @Schema(description = "Specialization (training type name)", example = "Yoga")
        @NotBlank String specialization) {
}
