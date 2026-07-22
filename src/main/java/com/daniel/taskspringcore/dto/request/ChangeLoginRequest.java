package com.daniel.taskspringcore.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Change login (password) request")
public record ChangeLoginRequest(
        @Schema(description = "Username", example = "John.Smith")
        @NotBlank String username,

        @Schema(description = "Current password")
        @NotBlank String oldPassword,

        @Schema(description = "New password")
        @NotBlank String newPassword) {
}
