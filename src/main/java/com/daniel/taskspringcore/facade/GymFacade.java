package com.daniel.taskspringcore.facade;

import java.time.LocalDate;
import java.util.List;

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

    // 1-2. Create profiles (no authentication required)

    public Trainee createTrainee(Trainee trainee) {
        log.debug("Facade: create trainee");
        return traineeService.create(trainee);
    }

    public Trainer createTrainer(Trainer trainer) {
        log.debug("Facade: create trainer");
        return trainerService.create(trainer);
    }

    // 3-4. Username and password matching

    public void authenticateTrainee(String username, String password) {
        log.debug("Facade: authenticate trainee {}", username);
        traineeService.authenticate(username, password);
    }

    public void authenticateTrainer(String username, String password) {
        log.debug("Facade: authenticate trainer {}", username);
        trainerService.authenticate(username, password);
    }

    // 5-6. Select profile by username

    public Trainer getTrainerByUsername(String authUsername, String authPassword, String username) {
        log.debug("Facade: get trainer {}", username);
        return trainerService.selectByUsername(authUsername, authPassword, username);
    }

    public Trainee getTraineeByUsername(String authUsername, String authPassword, String username) {
        log.debug("Facade: get trainee {}", username);
        return traineeService.selectByUsername(authUsername, authPassword, username);
    }

    // 7-8. Password change

    public void changeTraineePassword(String username, String oldPassword, String newPassword) {
        log.debug("Facade: change password of trainee {}", username);
        traineeService.changePassword(username, oldPassword, newPassword);
    }

    public void changeTrainerPassword(String username, String oldPassword, String newPassword) {
        log.debug("Facade: change password of trainer {}", username);
        trainerService.changePassword(username, oldPassword, newPassword);
    }

    // 9-10. Update profiles

    public Trainer updateTrainer(String authUsername, String authPassword, Trainer trainer) {
        log.debug("Facade: update trainer {}", trainer.getUsername());
        return trainerService.update(authUsername, authPassword, trainer);
    }

    public Trainee updateTrainee(String authUsername, String authPassword, Trainee trainee) {
        log.debug("Facade: update trainee {}", trainee.getUsername());
        return traineeService.update(authUsername, authPassword, trainee);
    }

    // 11-12. Activate/De-activate (not idempotent)

    public void activateTrainee(String authUsername, String authPassword, String username) {
        log.debug("Facade: activate trainee {}", username);
        traineeService.activate(authUsername, authPassword, username);
    }

    public void deactivateTrainee(String authUsername, String authPassword, String username) {
        log.debug("Facade: deactivate trainee {}", username);
        traineeService.deactivate(authUsername, authPassword, username);
    }

    public void activateTrainer(String authUsername, String authPassword, String username) {
        log.debug("Facade: activate trainer {}", username);
        trainerService.activate(authUsername, authPassword, username);
    }

    public void deactivateTrainer(String authUsername, String authPassword, String username) {
        log.debug("Facade: deactivate trainer {}", username);
        trainerService.deactivate(authUsername, authPassword, username);
    }

    // 13. Hard delete trainee, cascades to trainings

    public void deleteTraineeByUsername(String authUsername, String authPassword, String username) {
        log.debug("Facade: delete trainee {}", username);
        traineeService.deleteByUsername(authUsername, authPassword, username);
    }

    // 14-15. Trainings lists by criteria

    public List<Training> getTraineeTrainings(String authUsername, String authPassword, String username,
                                              LocalDate fromDate, LocalDate toDate,
                                              String trainerName, String trainingTypeName) {
        log.debug("Facade: get trainings of trainee {}", username);
        return traineeService.getTrainings(authUsername, authPassword, username,
                fromDate, toDate, trainerName, trainingTypeName);
    }

    public List<Training> getTrainerTrainings(String authUsername, String authPassword, String username,
                                              LocalDate fromDate, LocalDate toDate, String traineeName) {
        log.debug("Facade: get trainings of trainer {}", username);
        return trainerService.getTrainings(authUsername, authPassword, username, fromDate, toDate, traineeName);
    }

    // 16. Add training

    public Training addTraining(String authUsername, String authPassword,
                                String traineeUsername, String trainerUsername, String trainingName,
                                String trainingTypeName, LocalDate trainingDate, Integer trainingDurationMinutes) {
        log.debug("Facade: add training '{}'", trainingName);
        return trainingService.create(authUsername, authPassword, traineeUsername, trainerUsername,
                trainingName, trainingTypeName, trainingDate, trainingDurationMinutes);
    }

    // 17. Trainers not assigned to a trainee

    public List<Trainer> getUnassignedTrainers(String authUsername, String authPassword, String traineeUsername) {
        log.debug("Facade: get trainers not assigned to trainee {}", traineeUsername);
        return trainerService.getUnassignedTrainers(authUsername, authPassword, traineeUsername);
    }

    // 18. Update trainee's trainers list

    public Trainee updateTraineeTrainers(String authUsername, String authPassword,
                                         String traineeUsername, List<String> trainerUsernames) {
        log.debug("Facade: update trainers list of trainee {}", traineeUsername);
        return traineeService.updateTrainersList(authUsername, authPassword, traineeUsername, trainerUsernames);
    }
}
