package com.daniel.taskspringcore.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.daniel.taskspringcore.dao.TraineeDAO;
import com.daniel.taskspringcore.dao.TrainerDAO;
import com.daniel.taskspringcore.dao.TrainingDAO;
import com.daniel.taskspringcore.dao.TrainingTypeDAO;
import com.daniel.taskspringcore.dto.DtoMapper;
import com.daniel.taskspringcore.dto.TrainingDTO;
import com.daniel.taskspringcore.exception.EntityNotFoundException;
import com.daniel.taskspringcore.model.Trainee;
import com.daniel.taskspringcore.model.Trainer;
import com.daniel.taskspringcore.model.Training;
import com.daniel.taskspringcore.model.TrainingType;
import com.daniel.taskspringcore.service.util.AuthenticationService;
import com.daniel.taskspringcore.service.util.ValidationUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TrainingService {

    private TrainingDAO trainingDAO;
    private TraineeDAO traineeDAO;
    private TrainerDAO trainerDAO;
    private TrainingTypeDAO trainingTypeDAO;
    private AuthenticationService authenticationService;

    @Autowired
    public void setTrainingDAO(TrainingDAO trainingDAO) {
        this.trainingDAO = trainingDAO;
    }

    @Autowired
    public void setTraineeDAO(TraineeDAO traineeDAO) {
        this.traineeDAO = traineeDAO;
    }

    @Autowired
    public void setTrainerDAO(TrainerDAO trainerDAO) {
        this.trainerDAO = trainerDAO;
    }

    @Autowired
    public void setTrainingTypeDAO(TrainingTypeDAO trainingTypeDAO) {
        this.trainingTypeDAO = trainingTypeDAO;
    }

    @Autowired
    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Transactional
    public TrainingDTO create(String authUsername, String authPassword,
                              String traineeUsername, String trainerUsername, String trainingName,
                              String trainingTypeName, LocalDate trainingDate, Integer trainingDurationMinutes) {
        authenticationService.authenticate(authUsername, authPassword);
        ValidationUtils.requireNonBlank(trainingName, "trainingName");
        ValidationUtils.requireNonNull(trainingDate, "trainingDate");
        ValidationUtils.requireNonNull(trainingDurationMinutes, "trainingDuration");

        Trainee trainee = traineeDAO.findByUsername(traineeUsername)
                .orElseThrow(() -> new EntityNotFoundException("Trainee not found: " + traineeUsername));
        Trainer trainer = trainerDAO.findByUsername(trainerUsername)
                .orElseThrow(() -> new EntityNotFoundException("Trainer not found: " + trainerUsername));
        TrainingType trainingType = (trainingTypeName == null || trainingTypeName.isBlank())
                ? trainer.getSpecialization()
                : trainingTypeDAO.findByName(trainingTypeName)
                        .orElseThrow(() -> new EntityNotFoundException("Training type not found: " + trainingTypeName));

        Training training = new Training(trainee, trainer, trainingName, trainingType,
                trainingDate, trainingDurationMinutes);
        trainingDAO.save(training);

        // Adding a training links the trainer to the trainee's trainers list
        if (trainee.getTrainers().add(trainer)) {
            traineeDAO.update(trainee);
        }

        log.info("Created training '{}' for trainee '{}' with trainer '{}'",
                trainingName, traineeUsername, trainerUsername);
        return DtoMapper.toDto(training);
    }
}
