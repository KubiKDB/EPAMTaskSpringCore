package com.daniel.taskspringcore.dto;

import java.time.LocalDate;
import java.util.List;

public record TraineeDTO(String username, String firstName, String lastName,
                         LocalDate dateOfBirth, String address, boolean active,
                         List<TrainerDTO> trainers) {
}
