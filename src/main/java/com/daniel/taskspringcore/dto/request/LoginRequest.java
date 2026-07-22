package com.daniel.taskspringcore.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Login request. Credentials travel in the body so they never appear in URLs or logs.")
public record LoginRequest(
        @Schema(description = "Username", example = "John.Smith")
        @NotBlank String username,

        @Schema(description = "Password")
        @NotBlank String password) {
}
