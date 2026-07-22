package com.daniel.taskspringcore.dto.response;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "A training entry from a trainer's perspective")
public record TrainerTrainingResponse(String trainingName, LocalDate trainingDate,
                                      String trainingType, Integer trainingDuration,
                                      String traineeName) {
}
