package com.daniel.taskspringcore.dto.request;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "Add training request. Training type is derived from the trainer's specialization.")
public record AddTrainingRequest(
        @Schema(description = "Trainee username", example = "John.Smith")
        @NotBlank String traineeUsername,

        @Schema(description = "Trainer username", example = "Anna.Jones")
        @NotBlank String trainerUsername,

        @Schema(description = "Training name", example = "Morning Cardio")
        @NotBlank String trainingName,

        @Schema(description = "Training date", example = "2026-07-20")
        @NotNull LocalDate trainingDate,

        @Schema(description = "Training duration in minutes", example = "60")
        @NotNull @Positive Integer trainingDuration) {
}
