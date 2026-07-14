package com.daniel.taskspringcore.dto;

import java.time.LocalDate;

public record TrainingDTO(Long id, String traineeUsername, String trainerUsername,
                          String trainingName, String trainingTypeName,
                          LocalDate trainingDate, Integer trainingDuration) {
}
