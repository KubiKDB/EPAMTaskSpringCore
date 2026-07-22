package com.daniel.taskspringcore.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.daniel.taskspringcore.dto.CreateTrainerDTO;
import com.daniel.taskspringcore.dto.TrainerDTO;
import com.daniel.taskspringcore.dto.TrainerProfileDTO;
import com.daniel.taskspringcore.dto.UserCredentialsDTO;
import com.daniel.taskspringcore.dto.request.ActiveStatusRequest;
import com.daniel.taskspringcore.dto.request.TrainerRegistrationRequest;
import com.daniel.taskspringcore.dto.request.TrainerUpdateRequest;
import com.daniel.taskspringcore.dto.response.TrainerTrainingResponse;
import com.daniel.taskspringcore.facade.GymFacade;
import com.daniel.taskspringcore.web.ApiConstants;
import com.daniel.taskspringcore.web.WebMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Trainer", description = "Trainer registration and profile management")
@RestController
@RequestMapping("/api/trainers")
public class TrainerController {

    private final GymFacade facade;

    public TrainerController(GymFacade facade) {
        this.facade = facade;
    }

    @Operation(summary = "Register a new trainer (no authentication)")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserCredentialsDTO register(@Valid @RequestBody TrainerRegistrationRequest request) {
        return facade.createTrainer(new CreateTrainerDTO(
                request.firstName(), request.lastName(), request.specialization()));
    }

    @Operation(summary = "Get trainer profile")
    @GetMapping("/{username}")
    public TrainerProfileDTO getProfile(
            @RequestHeader(ApiConstants.AUTH_USERNAME_HEADER) String authUsername,
            @RequestHeader(ApiConstants.AUTH_PASSWORD_HEADER) String authPassword,
            @PathVariable String username) {
        return facade.getTrainerByUsername(authUsername, authPassword, username);
    }

    @Operation(summary = "Update trainer profile (specialization is read-only)")
    @PutMapping("/{username}")
    public TrainerProfileDTO update(
            @RequestHeader(ApiConstants.AUTH_USERNAME_HEADER) String authUsername,
            @RequestHeader(ApiConstants.AUTH_PASSWORD_HEADER) String authPassword,
            @PathVariable String username,
            @Valid @RequestBody TrainerUpdateRequest request) {
        TrainerDTO updated = new TrainerDTO(username, request.firstName(), request.lastName(),
                request.isActive(), null);
        return facade.updateTrainer(authUsername, authPassword, updated);
    }

    @Operation(summary = "Get the trainer's trainings with optional filters")
    @GetMapping("/{username}/trainings")
    public List<TrainerTrainingResponse> getTrainings(
            @RequestHeader(ApiConstants.AUTH_USERNAME_HEADER) String authUsername,
            @RequestHeader(ApiConstants.AUTH_PASSWORD_HEADER) String authPassword,
            @PathVariable String username,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodTo,
            @RequestParam(required = false) String traineeName) {
        return WebMapper.toTrainerTrainings(facade.getTrainerTrainings(
                authUsername, authPassword, username, periodFrom, periodTo, traineeName));
    }

    @Operation(summary = "Activate/de-activate the trainer (not idempotent)")
    @PatchMapping("/{username}/status")
    public ResponseEntity<Void> setStatus(
            @RequestHeader(ApiConstants.AUTH_USERNAME_HEADER) String authUsername,
            @RequestHeader(ApiConstants.AUTH_PASSWORD_HEADER) String authPassword,
            @PathVariable String username,
            @Valid @RequestBody ActiveStatusRequest request) {
        if (request.isActive()) {
            facade.activateTrainer(authUsername, authPassword, username);
        } else {
            facade.deactivateTrainer(authUsername, authPassword, username);
        }
        return ResponseEntity.ok().build();
    }
}
