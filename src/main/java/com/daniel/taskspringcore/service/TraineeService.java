package com.daniel.taskspringcore.service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.daniel.taskspringcore.dao.TraineeDAO;
import com.daniel.taskspringcore.dao.TrainerDAO;
import com.daniel.taskspringcore.dao.TrainingDAO;
import com.daniel.taskspringcore.dao.UserDAO;
import com.daniel.taskspringcore.exception.EntityNotFoundException;
import com.daniel.taskspringcore.model.Trainee;
import com.daniel.taskspringcore.model.Trainer;
import com.daniel.taskspringcore.model.Training;
import com.daniel.taskspringcore.service.util.AuthenticationService;
import com.daniel.taskspringcore.service.util.UserCredentialGenerator;
import com.daniel.taskspringcore.service.util.ValidationUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TraineeService {

    private TraineeDAO traineeDAO;
    private TrainerDAO trainerDAO;
    private TrainingDAO trainingDAO;
    private UserDAO userDAO;
    private UserCredentialGenerator credentialGenerator;
    private AuthenticationService authenticationService;

    @Autowired
    public void setTraineeDAO(TraineeDAO traineeDAO) {
        this.traineeDAO = traineeDAO;
    }

    @Autowired
    public void setTrainerDAO(TrainerDAO trainerDAO) {
        this.trainerDAO = trainerDAO;
    }

    @Autowired
    public void setTrainingDAO(TrainingDAO trainingDAO) {
        this.trainingDAO = trainingDAO;
    }

    @Autowired
    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Autowired
    public void setCredentialGenerator(UserCredentialGenerator credentialGenerator) {
        this.credentialGenerator = credentialGenerator;
    }

    @Autowired
    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Transactional
    public Trainee create(Trainee trainee) {
        ValidationUtils.requireNonBlank(trainee.getFirstName(), "firstName");
        ValidationUtils.requireNonBlank(trainee.getLastName(), "lastName");
        Set<String> existingUsernames = new HashSet<>(userDAO.findAllUsernames());
        trainee.setUsername(credentialGenerator.generateUsername(
                trainee.getFirstName(), trainee.getLastName(), existingUsernames));
        trainee.setPassword(credentialGenerator.generatePassword());
        trainee.setActive(true);
        traineeDAO.save(trainee);
        log.info("Created trainee '{}'", trainee.getUsername());
        return trainee;
    }

    @Transactional(readOnly = true)
    public void authenticate(String username, String password) {
        authenticationService.authenticate(username, password);
    }

    @Transactional(readOnly = true)
    public Trainee selectByUsername(String authUsername, String authPassword, String username) {
        authenticationService.authenticate(authUsername, authPassword);
        log.debug("Selecting trainee '{}'", username);
        return findRequired(username);
    }

    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {
        authenticationService.authenticate(username, oldPassword);
        ValidationUtils.requireNonBlank(newPassword, "newPassword");
        Trainee trainee = findRequired(username);
        trainee.setPassword(newPassword);
        traineeDAO.update(trainee);
        log.info("Changed password for trainee '{}'", username);
    }

    @Transactional
    public Trainee update(String authUsername, String authPassword, Trainee updated) {
        authenticationService.authenticate(authUsername, authPassword);
        ValidationUtils.requireNonBlank(updated.getUsername(), "username");
        ValidationUtils.requireNonBlank(updated.getFirstName(), "firstName");
        ValidationUtils.requireNonBlank(updated.getLastName(), "lastName");
        Trainee trainee = findRequired(updated.getUsername());
        trainee.setFirstName(updated.getFirstName());
        trainee.setLastName(updated.getLastName());
        trainee.setDateOfBirth(updated.getDateOfBirth());
        trainee.setAddress(updated.getAddress());
        traineeDAO.update(trainee);
        log.info("Updated trainee '{}'", trainee.getUsername());
        return trainee;
    }

    @Transactional
    public void activate(String authUsername, String authPassword, String username) {
        setActiveState(authUsername, authPassword, username, true);
    }

    @Transactional
    public void deactivate(String authUsername, String authPassword, String username) {
        setActiveState(authUsername, authPassword, username, false);
    }

    @Transactional
    public void deleteByUsername(String authUsername, String authPassword, String username) {
        authenticationService.authenticate(authUsername, authPassword);
        findRequired(username);
        traineeDAO.deleteByUsername(username);
        log.info("Deleted trainee '{}' and cascaded their trainings", username);
    }

    @Transactional(readOnly = true)
    public List<Training> getTrainings(String authUsername, String authPassword, String username,
                                       LocalDate fromDate, LocalDate toDate,
                                       String trainerName, String trainingTypeName) {
        authenticationService.authenticate(authUsername, authPassword);
        log.debug("Fetching trainings of trainee '{}' with criteria", username);
        return trainingDAO.findTraineeTrainings(username, fromDate, toDate, trainerName, trainingTypeName);
    }

    @Transactional
    public Trainee updateTrainersList(String authUsername, String authPassword,
                                      String traineeUsername, List<String> trainerUsernames) {
        authenticationService.authenticate(authUsername, authPassword);
        Trainee trainee = findRequired(traineeUsername);
        Set<Trainer> trainers = new HashSet<>();
        for (String trainerUsername : trainerUsernames) {
            trainers.add(trainerDAO.findByUsername(trainerUsername)
                    .orElseThrow(() -> new EntityNotFoundException("Trainer not found: " + trainerUsername)));
        }
        trainee.getTrainers().clear();
        trainee.getTrainers().addAll(trainers);
        traineeDAO.update(trainee);
        log.info("Updated trainers list of trainee '{}' ({} trainers)", traineeUsername, trainers.size());
        return trainee;
    }

    // Activate/de-activate is not idempotent: repeating the same action is an error
    private void setActiveState(String authUsername, String authPassword, String username, boolean active) {
        authenticationService.authenticate(authUsername, authPassword);
        Trainee trainee = findRequired(username);
        if (trainee.isActive() == active) {
            throw new IllegalStateException(
                    "Trainee " + username + " is already " + (active ? "active" : "inactive"));
        }
        trainee.setActive(active);
        traineeDAO.update(trainee);
        log.info("{} trainee '{}'", active ? "Activated" : "Deactivated", username);
    }

    private Trainee findRequired(String username) {
        return traineeDAO.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Trainee not found: " + username));
    }
}
