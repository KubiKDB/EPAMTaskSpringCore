package com.daniel.taskspringcore.dto.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

@Schema(description = "Replace a trainee's trainer list")
public record UpdateTraineeTrainersRequest(
        @Schema(description = "Trainer usernames the trainee should be assigned to")
        @NotEmpty List<@NotBlank String> trainerUsernames) {
}
