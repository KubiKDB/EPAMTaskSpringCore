package com.daniel.taskspringcore.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.daniel.taskspringcore.dto.request.ChangeLoginRequest;
import com.daniel.taskspringcore.facade.GymFacade;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;

@Tag(name = "Authentication", description = "Login and change-login")
@Validated
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final GymFacade facade;

    public AuthController(GymFacade facade) {
        this.facade = facade;
    }

    @Operation(summary = "Login: verify username and password")
    @GetMapping("/login")
    public ResponseEntity<Void> login(
            @RequestParam @NotBlank String username,
            @RequestParam @NotBlank String password) {
        facade.login(username, password);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Change login (password)")
    @PutMapping("/login")
    public ResponseEntity<Void> changeLogin(@Valid @RequestBody ChangeLoginRequest request) {
        facade.changePassword(request.username(), request.oldPassword(), request.newPassword());
        return ResponseEntity.ok().build();
    }
}
