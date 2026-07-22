package com.daniel.taskspringcore.facade;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Component;

import com.daniel.taskspringcore.dto.CreateTraineeDTO;
import com.daniel.taskspringcore.dto.CreateTrainerDTO;
import com.daniel.taskspringcore.dto.TraineeDTO;
import com.daniel.taskspringcore.dto.TrainerDTO;
import com.daniel.taskspringcore.dto.TrainerProfileDTO;
import com.daniel.taskspringcore.dto.TrainingDTO;
import com.daniel.taskspringcore.dto.TrainingTypeDTO;
import com.daniel.taskspringcore.dto.UserCredentialsDTO;
import com.daniel.taskspringcore.service.TraineeService;
import com.daniel.taskspringcore.service.TrainerService;
import com.daniel.taskspringcore.service.TrainingService;
import com.daniel.taskspringcore.service.TrainingTypeService;
import com.daniel.taskspringcore.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class GymFacade {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;
    private final TrainingTypeService trainingTypeService;
    private final UserService userService;

    public GymFacade(TraineeService traineeService,
                     TrainerService trainerService,
                     TrainingService trainingService,
                     TrainingTypeService trainingTypeService,
                     UserService userService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
        this.trainingTypeService = trainingTypeService;
        this.userService = userService;
    }

    // 1-2. Create profiles (no authentication required)

    public UserCredentialsDTO createTrainee(CreateTraineeDTO trainee) {
        log.debug("Facade: create trainee");
        return traineeService.create(trainee);
    }

    public UserCredentialsDTO createTrainer(CreateTrainerDTO trainer) {
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

    // Login
    public void login(String username, String password) {
        log.debug("Facade: login {}", username);
        userService.authenticate(username, password);
    }

    // 5-6. Select profile by username

    public TrainerProfileDTO getTrainerByUsername(String authUsername, String authPassword, String username) {
        log.debug("Facade: get trainer {}", username);
        return trainerService.selectByUsername(authUsername, authPassword, username);
    }

    public TraineeDTO getTraineeByUsername(String authUsername, String authPassword, String username) {
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

    // Change login for any user
    public void changePassword(String username, String oldPassword, String newPassword) {
        log.debug("Facade: change login of user {}", username);
        userService.changePassword(username, oldPassword, newPassword);
    }

    // Activate/de-activate any user, whether trainee or trainer (not idempotent)
    public void setUserActive(String authUsername, String authPassword, String username, boolean active) {
        log.debug("Facade: set active={} for user {}", active, username);
        userService.setActive(authUsername, authPassword, username, active);
    }

    // 9-10. Update profiles

    public TrainerProfileDTO updateTrainer(String authUsername, String authPassword, TrainerDTO trainer) {
        log.debug("Facade: update trainer {}", trainer.username());
        return trainerService.update(authUsername, authPassword, trainer);
    }

    public TraineeDTO updateTrainee(String authUsername, String authPassword, TraineeDTO trainee) {
        log.debug("Facade: update trainee {}", trainee.username());
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

    public List<TrainingDTO> getTraineeTrainings(String authUsername, String authPassword, String username,
                                                 LocalDate fromDate, LocalDate toDate,
                                                 String trainerName, String trainingTypeName) {
        log.debug("Facade: get trainings of trainee {}", username);
        return traineeService.getTrainings(authUsername, authPassword, username,
                fromDate, toDate, trainerName, trainingTypeName);
    }

    public List<TrainingDTO> getTrainerTrainings(String authUsername, String authPassword, String username,
                                                 LocalDate fromDate, LocalDate toDate, String traineeName) {
        log.debug("Facade: get trainings of trainer {}", username);
        return trainerService.getTrainings(authUsername, authPassword, username, fromDate, toDate, traineeName);
    }

    // 16. Add training

    public TrainingDTO addTraining(String authUsername, String authPassword,
                                   String traineeUsername, String trainerUsername, String trainingName,
                                   String trainingTypeName, LocalDate trainingDate, Integer trainingDurationMinutes) {
        log.debug("Facade: add training '{}'", trainingName);
        return trainingService.create(authUsername, authPassword, traineeUsername, trainerUsername,
                trainingName, trainingTypeName, trainingDate, trainingDurationMinutes);
    }

    public TrainingDTO addTraining(String authUsername, String authPassword,
                                   String traineeUsername, String trainerUsername, String trainingName,
                                   LocalDate trainingDate, Integer trainingDurationMinutes) {
        return addTraining(authUsername, authPassword, traineeUsername, trainerUsername,
                trainingName, null, trainingDate, trainingDurationMinutes);
    }

    // 17. Trainers not assigned to a trainee

    public List<TrainerDTO> getUnassignedTrainers(String authUsername, String authPassword, String traineeUsername) {
        log.debug("Facade: get trainers not assigned to trainee {}", traineeUsername);
        return trainerService.getUnassignedTrainers(authUsername, authPassword, traineeUsername);
    }

    // 18. Update trainee's trainers list

    public TraineeDTO updateTraineeTrainers(String authUsername, String authPassword,
                                            String traineeUsername, List<String> trainerUsernames) {
        log.debug("Facade: update trainers list of trainee {}", traineeUsername);
        return traineeService.updateTrainersList(authUsername, authPassword, traineeUsername, trainerUsernames);
    }

    // 19. Training types reference list (no authentication)

    public List<TrainingTypeDTO> getTrainingTypes() {
        log.debug("Facade: get training types");
        return trainingTypeService.getAll();
    }
}
