package com.daniel.taskspringcore.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.daniel.taskspringcore.dao.UserDAO;
import com.daniel.taskspringcore.exception.EntityNotFoundException;
import com.daniel.taskspringcore.model.Trainee;
import com.daniel.taskspringcore.model.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService {

    private final UserDAO userDAO;
    private final TraineeService traineeService;
    private final TrainerService trainerService;

    public UserService(UserDAO userDAO, TraineeService traineeService, TrainerService trainerService) {
        this.userDAO = userDAO;
        this.traineeService = traineeService;
        this.trainerService = trainerService;
    }

    @Transactional(readOnly = true)
    public void authenticate(String username, String password) {
        traineeService.authenticate(username, password);
    }

    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {
        if (isTrainee(username)) {
            traineeService.changePassword(username, oldPassword, newPassword);
        } else {
            trainerService.changePassword(username, oldPassword, newPassword);
        }
    }

    // Activate/de-activate any user. Not idempotent: repeating the same state is an error.
    @Transactional
    public void setActive(String authUsername, String authPassword, String username, boolean active) {
        if (isTrainee(username)) {
            if (active) {
                traineeService.activate(authUsername, authPassword, username);
            } else {
                traineeService.deactivate(authUsername, authPassword, username);
            }
        } else {
            if (active) {
                trainerService.activate(authUsername, authPassword, username);
            } else {
                trainerService.deactivate(authUsername, authPassword, username);
            }
        }
    }

    private boolean isTrainee(String username) {
        User user = userDAO.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + username));
        return user instanceof Trainee;
    }
}