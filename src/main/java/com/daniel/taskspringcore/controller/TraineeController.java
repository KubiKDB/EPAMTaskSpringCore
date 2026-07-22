package com.daniel.taskspringcore.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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

import com.daniel.taskspringcore.dto.CreateTraineeDTO;
import com.daniel.taskspringcore.dto.TraineeDTO;
import com.daniel.taskspringcore.dto.TrainerDTO;
import com.daniel.taskspringcore.dto.UserCredentialsDTO;
import com.daniel.taskspringcore.dto.request.ActiveStatusRequest;
import com.daniel.taskspringcore.dto.request.TraineeRegistrationRequest;
import com.daniel.taskspringcore.dto.request.TraineeUpdateRequest;
import com.daniel.taskspringcore.dto.request.UpdateTraineeTrainersRequest;
import com.daniel.taskspringcore.dto.response.TraineeTrainingResponse;
import com.daniel.taskspringcore.facade.GymFacade;
import com.daniel.taskspringcore.web.ApiConstants;
import com.daniel.taskspringcore.web.WebMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Trainee", description = "Trainee registration and profile management")
@RestController
@RequestMapping("/api/trainees")
public class TraineeController {

    private final GymFacade facade;

    public TraineeController(GymFacade facade) {
        this.facade = facade;
    }

    @Operation(summary = "Register a new trainee (no authentication)")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserCredentialsDTO register(@Valid @RequestBody TraineeRegistrationRequest request) {
        return facade.createTrainee(new CreateTraineeDTO(
                request.firstName(), request.lastName(), request.dateOfBirth(), request.address()));
    }

    @Operation(summary = "Get trainee profile")
    @GetMapping("/{username}")
    public TraineeDTO getProfile(
            @RequestHeader(ApiConstants.AUTH_USERNAME_HEADER) String authUsername,
            @RequestHeader(ApiConstants.AUTH_PASSWORD_HEADER) String authPassword,
            @PathVariable String username) {
        return facade.getTraineeByUsername(authUsername, authPassword, username);
    }

    @Operation(summary = "Update trainee profile")
    @PutMapping("/{username}")
    public TraineeDTO update(
            @RequestHeader(ApiConstants.AUTH_USERNAME_HEADER) String authUsername,
            @RequestHeader(ApiConstants.AUTH_PASSWORD_HEADER) String authPassword,
            @PathVariable String username,
            @Valid @RequestBody TraineeUpdateRequest request) {
        TraineeDTO updated = new TraineeDTO(username, request.firstName(), request.lastName(),
                request.dateOfBirth(), request.address(), request.isActive(), null);
        return facade.updateTrainee(authUsername, authPassword, updated);
    }

    @Operation(summary = "Delete trainee profile (hard delete, cascades trainings)")
    @DeleteMapping("/{username}")
    public ResponseEntity<Void> delete(
            @RequestHeader(ApiConstants.AUTH_USERNAME_HEADER) String authUsername,
            @RequestHeader(ApiConstants.AUTH_PASSWORD_HEADER) String authPassword,
            @PathVariable String username) {
        facade.deleteTraineeByUsername(authUsername, authPassword, username);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get active trainers not assigned to the trainee")
    @GetMapping("/{username}/unassigned-trainers")
    public List<TrainerDTO> getUnassignedTrainers(
            @RequestHeader(ApiConstants.AUTH_USERNAME_HEADER) String authUsername,
            @RequestHeader(ApiConstants.AUTH_PASSWORD_HEADER) String authPassword,
            @PathVariable String username) {
        return facade.getUnassignedTrainers(authUsername, authPassword, username);
    }

    @Operation(summary = "Replace the trainee's trainer list")
    @PutMapping("/{username}/trainers")
    public TraineeDTO updateTrainers(
            @RequestHeader(ApiConstants.AUTH_USERNAME_HEADER) String authUsername,
            @RequestHeader(ApiConstants.AUTH_PASSWORD_HEADER) String authPassword,
            @PathVariable String username,
            @Valid @RequestBody UpdateTraineeTrainersRequest request) {
        return facade.updateTraineeTrainers(authUsername, authPassword, username, request.trainerUsernames());
    }

    @Operation(summary = "Get the trainee's trainings with optional filters")
    @GetMapping("/{username}/trainings")
    public List<TraineeTrainingResponse> getTrainings(
            @RequestHeader(ApiConstants.AUTH_USERNAME_HEADER) String authUsername,
            @RequestHeader(ApiConstants.AUTH_PASSWORD_HEADER) String authPassword,
            @PathVariable String username,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodTo,
            @RequestParam(required = false) String trainerName,
            @RequestParam(required = false) String trainingType) {
        return WebMapper.toTraineeTrainings(facade.getTraineeTrainings(
                authUsername, authPassword, username, periodFrom, periodTo, trainerName, trainingType));
    }

    @Operation(summary = "Activate/de-activate the trainee (not idempotent)")
    @PatchMapping("/{username}/status")
    public ResponseEntity<Void> setStatus(
            @RequestHeader(ApiConstants.AUTH_USERNAME_HEADER) String authUsername,
            @RequestHeader(ApiConstants.AUTH_PASSWORD_HEADER) String authPassword,
            @PathVariable String username,
            @Valid @RequestBody ActiveStatusRequest request) {
        if (request.isActive()) {
            facade.activateTrainee(authUsername, authPassword, username);
        } else {
            facade.deactivateTrainee(authUsername, authPassword, username);
        }
        return ResponseEntity.ok().build();
    }
}
