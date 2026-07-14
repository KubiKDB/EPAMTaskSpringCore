package com.daniel.taskspringcore.dto;

import java.time.LocalDate;

public record CreateTraineeDTO(String firstName, String lastName,
                               LocalDate dateOfBirth, String address) {
}
