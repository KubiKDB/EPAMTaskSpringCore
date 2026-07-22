package com.daniel.taskspringcore.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.daniel.taskspringcore.dto.request.ActiveStatusRequest;
import com.daniel.taskspringcore.facade.GymFacade;
import com.daniel.taskspringcore.web.ApiConstants;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "User", description = "Operations that apply to any user, trainee or trainer")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final GymFacade facade;

    public UserController(GymFacade facade) {
        this.facade = facade;
    }

    @Operation(summary = "Activate/de-activate any user, trainee or trainer (not idempotent)")
    @PatchMapping("/{username}/status")
    public ResponseEntity<Void> setStatus(
            @RequestHeader(ApiConstants.AUTH_USERNAME_HEADER) String authUsername,
            @RequestHeader(ApiConstants.AUTH_PASSWORD_HEADER) String authPassword,
            @PathVariable String username,
            @Valid @RequestBody ActiveStatusRequest request) {
        facade.setUserActive(authUsername, authPassword, username, request.isActive());
        return ResponseEntity.ok().build();
    }
}
