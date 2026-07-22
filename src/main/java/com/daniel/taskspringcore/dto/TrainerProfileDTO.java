package com.daniel.taskspringcore.dto;

import java.util.List;

public record TrainerProfileDTO(String username, String firstName, String lastName,
                                boolean active, String specialization,
                                List<TraineeSummaryDTO> trainees) {
}
