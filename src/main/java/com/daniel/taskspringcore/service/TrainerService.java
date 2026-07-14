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
import com.daniel.taskspringcore.dao.TrainingTypeDAO;
import com.daniel.taskspringcore.dao.UserDAO;
import com.daniel.taskspringcore.dto.CreateTrainerDTO;
import com.daniel.taskspringcore.dto.DtoMapper;
import com.daniel.taskspringcore.dto.TrainerDTO;
import com.daniel.taskspringcore.dto.TrainingDTO;
import com.daniel.taskspringcore.dto.UserCredentialsDTO;
import com.daniel.taskspringcore.exception.EntityNotFoundException;
import com.daniel.taskspringcore.model.Trainer;
import com.daniel.taskspringcore.model.TrainingType;
import com.daniel.taskspringcore.service.util.AuthenticationService;
import com.daniel.taskspringcore.service.util.UserCredentialGenerator;
import com.daniel.taskspringcore.service.util.ValidationUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TrainerService {

    private TrainerDAO trainerDAO;
    private TrainingDAO trainingDAO;
    private TrainingTypeDAO trainingTypeDAO;
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
    public void setTrainingTypeDAO(TrainingTypeDAO trainingTypeDAO) {
        this.trainingTypeDAO = trainingTypeDAO;
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
    public UserCredentialsDTO create(CreateTrainerDTO dto) {
        ValidationUtils.requireNonBlank(dto.firstName(), "firstName");
        ValidationUtils.requireNonBlank(dto.lastName(), "lastName");
        ValidationUtils.requireNonBlank(dto.specialization(), "specialization");
        TrainingType specialization = findRequiredType(dto.specialization());
        Set<String> existingUsernames = new HashSet<>(userDAO.findAllUsernames());
        String username = credentialGenerator.generateUsername(
                dto.firstName(), dto.lastName(), existingUsernames);
        String password = credentialGenerator.generatePassword();
        Trainer trainer = new Trainer(dto.firstName(), dto.lastName(), username, password,
                true, specialization);
        trainerDAO.save(trainer);
        log.info("Created trainer '{}'", username);
        return new UserCredentialsDTO(username, password);
    }

    @Transactional(readOnly = true)
    public void authenticate(String username, String password) {
        authenticationService.authenticate(username, password);
    }

    @Transactional(readOnly = true)
    public TrainerDTO selectByUsername(String authUsername, String authPassword, String username) {
        authenticationService.authenticate(authUsername, authPassword);
        log.debug("Selecting trainer '{}'", username);
        return DtoMapper.toDto(findRequired(username));
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
    public TrainerDTO update(String authUsername, String authPassword, TrainerDTO updated) {
        authenticationService.authenticate(authUsername, authPassword);
        ValidationUtils.requireNonBlank(updated.username(), "username");
        ValidationUtils.requireNonBlank(updated.firstName(), "firstName");
        ValidationUtils.requireNonBlank(updated.lastName(), "lastName");
        ValidationUtils.requireNonBlank(updated.specialization(), "specialization");
        Trainer trainer = findRequired(updated.username());
        trainer.setFirstName(updated.firstName());
        trainer.setLastName(updated.lastName());
        trainer.setSpecialization(findRequiredType(updated.specialization()));
        trainerDAO.update(trainer);
        log.info("Updated trainer '{}'", trainer.getUsername());
        return DtoMapper.toDto(trainer);
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
    public List<TrainingDTO> getTrainings(String authUsername, String authPassword, String username,
                                          LocalDate fromDate, LocalDate toDate, String traineeName) {
        authenticationService.authenticate(authUsername, authPassword);
        log.debug("Fetching trainings of trainer '{}' with criteria", username);
        return DtoMapper.toTrainingDtos(
                trainingDAO.findTrainerTrainings(username, fromDate, toDate, traineeName));
    }

    @Transactional(readOnly = true)
    public List<TrainerDTO> getUnassignedTrainers(String authUsername, String authPassword, String traineeUsername) {
        authenticationService.authenticate(authUsername, authPassword);
        log.debug("Fetching trainers not assigned to trainee '{}'", traineeUsername);
        return DtoMapper.toTrainerDtos(trainerDAO.findNotAssignedToTrainee(traineeUsername));
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

    private TrainingType findRequiredType(String trainingTypeName) {
        return trainingTypeDAO.findByName(trainingTypeName)
                .orElseThrow(() -> new EntityNotFoundException("Training type not found: " + trainingTypeName));
    }
}
