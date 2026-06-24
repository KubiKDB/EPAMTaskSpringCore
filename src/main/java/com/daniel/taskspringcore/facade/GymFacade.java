package com.daniel.taskspringcore.facade;

import org.springframework.stereotype.Component;

import com.daniel.taskspringcore.model.Trainee;
import com.daniel.taskspringcore.model.Trainer;
import com.daniel.taskspringcore.model.Training;
import com.daniel.taskspringcore.service.TraineeService;
import com.daniel.taskspringcore.service.TrainerService;
import com.daniel.taskspringcore.service.TrainingService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class GymFacade {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    public GymFacade(TraineeService traineeService,
                     TrainerService trainerService,
                     TrainingService trainingService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
    }

    public Trainee createTrainee(Trainee trainee) {
        log.debug("Facade: create trainee");
        return traineeService.create(trainee);
    }

    public Trainee updateTrainee(Trainee trainee) {
        log.debug("Facade: update trainee {}", trainee.getUserId());
        return traineeService.update(trainee);
    }

    public void deleteTrainee(String traineeId) {
        log.debug("Facade: delete trainee {}", traineeId);
        traineeService.delete(traineeId);
    }

    public Trainee getTrainee(String traineeId) {
        return traineeService.select(traineeId);
    }

    public Trainer createTrainer(Trainer trainer) {
        log.debug("Facade: create trainer");
        return trainerService.create(trainer);
    }

    public Trainer updateTrainer(Trainer trainer) {
        log.debug("Facade: update trainer {}", trainer.getUserId());
        return trainerService.update(trainer);
    }

    public Trainer getTrainer(String trainerId) {
        return trainerService.select(trainerId);
    }

    public Training createTraining(Training training) {
        log.debug("Facade: create training");
        return trainingService.create(training);
    }

    public Training getTraining(String trainingId) {
        return trainingService.select(trainingId);
    }
}