package com.daniel.taskspringcore.service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.daniel.taskspringcore.dao.TrainerDAO;
import com.daniel.taskspringcore.dao.TrainingDAO;
import com.daniel.taskspringcore.dao.UserDAO;
import com.daniel.taskspringcore.exception.EntityNotFoundException;
import com.daniel.taskspringcore.model.Trainer;
import com.daniel.taskspringcore.model.Training;
import com.daniel.taskspringcore.service.util.AuthenticationService;
import com.daniel.taskspringcore.service.util.UserCredentialGenerator;
import com.daniel.taskspringcore.service.util.ValidationUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TrainerService {

    private TrainerDAO trainerDAO;
    private TrainingDAO trainingDAO;
    private UserDAO userDAO;
    private UserCredentialGenerator credentialGenerator;
    private AuthenticationService authenticationService;

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
    public Trainer create(Trainer trainer) {
        ValidationUtils.requireNonBlank(trainer.getFirstName(), "firstName");
        ValidationUtils.requireNonBlank(trainer.getLastName(), "lastName");
        ValidationUtils.requireNonNull(trainer.getSpecialization(), "specialization");
        Set<String> existingUsernames = new HashSet<>(userDAO.findAllUsernames());
        trainer.setUsername(credentialGenerator.generateUsername(
                trainer.getFirstName(), trainer.getLastName(), existingUsernames));
        trainer.setPassword(credentialGenerator.generatePassword());
        trainer.setActive(true);
        trainerDAO.save(trainer);
        log.info("Created trainer '{}'", trainer.getUsername());
        return trainer;
    }

    @Transactional(readOnly = true)
    public void authenticate(String username, String password) {
        authenticationService.authenticate(username, password);
    }

    @Transactional(readOnly = true)
    public Trainer selectByUsername(String authUsername, String authPassword, String username) {
        authenticationService.authenticate(authUsername, authPassword);
        log.debug("Selecting trainer '{}'", username);
        return findRequired(username);
    }

    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {
        authenticationService.authenticate(username, oldPassword);
        ValidationUtils.requireNonBlank(newPassword, "newPassword");
        Trainer trainer = findRequired(username);
        trainer.setPassword(newPassword);
        trainerDAO.update(trainer);
        log.info("Changed password for trainer '{}'", username);
    }

    @Transactional
    public Trainer update(String authUsername, String authPassword, Trainer updated) {
        authenticationService.authenticate(authUsername, authPassword);
        ValidationUtils.requireNonBlank(updated.getUsername(), "username");
        ValidationUtils.requireNonBlank(updated.getFirstName(), "firstName");
        ValidationUtils.requireNonBlank(updated.getLastName(), "lastName");
        ValidationUtils.requireNonNull(updated.getSpecialization(), "specialization");
        Trainer trainer = findRequired(updated.getUsername());
        trainer.setFirstName(updated.getFirstName());
        trainer.setLastName(updated.getLastName());
        trainer.setSpecialization(updated.getSpecialization());
        trainerDAO.update(trainer);
        log.info("Updated trainer '{}'", trainer.getUsername());
        return trainer;
    }

    @Transactional
    public void activate(String authUsername, String authPassword, String username) {
        setActiveState(authUsername, authPassword, username, true);
    }

    @Transactional
    public void deactivate(String authUsername, String authPassword, String username) {
        setActiveState(authUsername, authPassword, username, false);
    }

    @Transactional(readOnly = true)
    public List<Training> getTrainings(String authUsername, String authPassword, String username,
                                       LocalDate fromDate, LocalDate toDate, String traineeName) {
        authenticationService.authenticate(authUsername, authPassword);
        log.debug("Fetching trainings of trainer '{}' with criteria", username);
        return trainingDAO.findTrainerTrainings(username, fromDate, toDate, traineeName);
    }

    @Transactional(readOnly = true)
    public List<Trainer> getUnassignedTrainers(String authUsername, String authPassword, String traineeUsername) {
        authenticationService.authenticate(authUsername, authPassword);
        log.debug("Fetching trainers not assigned to trainee '{}'", traineeUsername);
        return trainerDAO.findNotAssignedToTrainee(traineeUsername);
    }

    // Activate/de-activate is not idempotent: repeating the same action is an error
    private void setActiveState(String authUsername, String authPassword, String username, boolean active) {
        authenticationService.authenticate(authUsername, authPassword);
        Trainer trainer = findRequired(username);
        if (trainer.isActive() == active) {
            throw new IllegalStateException(
                    "Trainer " + username + " is already " + (active ? "active" : "inactive"));
        }
        trainer.setActive(active);
        trainerDAO.update(trainer);
        log.info("{} trainer '{}'", active ? "Activated" : "Deactivated", username);
    }

    private Trainer findRequired(String username) {
        return trainerDAO.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Trainer not found: " + username));
    }
}
