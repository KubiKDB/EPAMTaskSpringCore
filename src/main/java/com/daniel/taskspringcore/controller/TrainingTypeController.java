package com.daniel.taskspringcore.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.daniel.taskspringcore.dto.TrainingTypeDTO;
import com.daniel.taskspringcore.facade.GymFacade;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Training Type", description = "Training type reference data")
@RestController
@RequestMapping("/api/training-types")
public class TrainingTypeController {

    private final GymFacade facade;

    public TrainingTypeController(GymFacade facade) {
        this.facade = facade;
    }

    @Operation(summary = "Get all training types (no authentication)")
    @GetMapping
    public List<TrainingTypeDTO> getTrainingTypes() {
        return facade.getTrainingTypes();
    }
}
