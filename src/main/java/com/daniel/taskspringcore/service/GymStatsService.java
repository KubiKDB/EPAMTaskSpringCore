package com.daniel.taskspringcore.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.daniel.taskspringcore.dao.TraineeDAO;
import com.daniel.taskspringcore.dao.TrainerDAO;
import com.daniel.taskspringcore.dao.TrainingTypeDAO;
import com.daniel.taskspringcore.dao.UserDAO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class GymStatsService {

    private final TraineeDAO traineeDAO;
    private final TrainerDAO trainerDAO;
    private final TrainingTypeDAO trainingTypeDAO;
    private final UserDAO userDAO;

    public GymStatsService(TraineeDAO traineeDAO, TrainerDAO trainerDAO,
                           TrainingTypeDAO trainingTypeDAO, UserDAO userDAO) {
        this.traineeDAO = traineeDAO;
        this.trainerDAO = trainerDAO;
        this.trainingTypeDAO = trainingTypeDAO;
        this.userDAO = userDAO;
    }

    @Transactional(readOnly = true)
    public long countActiveTrainees() {
        long count = traineeDAO.countActive();
        log.trace("Active trainee count: {}", count);
        return count;
    }

    @Transactional(readOnly = true)
    public long countActiveTrainers() {
        long count = trainerDAO.countActive();
        log.trace("Active trainer count: {}", count);
        return count;
    }

    @Transactional(readOnly = true)
    public long countTrainingTypes() {
        long count = trainingTypeDAO.count();
        log.trace("Training type count: {}", count);
        return count;
    }

    @Transactional(readOnly = true)
    public long countUsers() {
        long count = userDAO.count();
        log.trace("User count: {}", count);
        return count;
    }
}