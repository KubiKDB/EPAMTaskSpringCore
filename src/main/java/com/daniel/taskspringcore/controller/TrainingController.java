package com.daniel.taskspringcore.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.daniel.taskspringcore.dto.request.AddTrainingRequest;
import com.daniel.taskspringcore.facade.GymFacade;
import com.daniel.taskspringcore.web.ApiConstants;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Training", description = "Add trainings")
@RestController
@RequestMapping("/api/trainings")
public class TrainingController {

    private final GymFacade facade;

    public TrainingController(GymFacade facade) {
        this.facade = facade;
    }

    @Operation(summary = "Add a training (training type is derived from the trainer's specialization)")
    @PostMapping
    public ResponseEntity<Void> addTraining(
            @RequestHeader(ApiConstants.AUTH_USERNAME_HEADER) String authUsername,
            @RequestHeader(ApiConstants.AUTH_PASSWORD_HEADER) String authPassword,
            @Valid @RequestBody AddTrainingRequest request) {
        facade.addTraining(authUsername, authPassword,
                request.traineeUsername(), request.trainerUsername(), request.trainingName(),
                request.trainingDate(), request.trainingDuration());
        return ResponseEntity.ok().build();
    }
}
