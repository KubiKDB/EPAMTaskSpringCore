package com.daniel.taskspringcore.service;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.daniel.taskspringcore.dao.TraineeDAO;
import com.daniel.taskspringcore.model.Trainee;
import com.daniel.taskspringcore.service.util.IdGenerator;
import com.daniel.taskspringcore.service.util.UserCredentialGenerator;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TraineeService {

    private TraineeDAO traineeDAO;
    private UserCredentialGenerator credentialGenerator;

    @Autowired
    public void setTraineeDAO(TraineeDAO traineeDAO) {
        this.traineeDAO = traineeDAO;
    }

    @Autowired
    public void setCredentialGenerator(UserCredentialGenerator credentialGenerator) {
        this.credentialGenerator = credentialGenerator;
    }

    public Trainee create(Trainee trainee) {
        Set<String> existingUsernames = traineeDAO.findAll().stream()
                .map(Trainee::getUsername)
                .collect(Collectors.toSet());
        trainee.setUsername(credentialGenerator.generateUsername(
                trainee.getFirstName(), trainee.getLastName(), existingUsernames));
        trainee.setPassword(credentialGenerator.generatePassword());
        trainee.setActive(true);
        if (trainee.getUserId() == null) {
            trainee.setUserId(IdGenerator.nextId(
                    traineeDAO.findAll().stream().map(Trainee::getUserId).toList()));
        }
        traineeDAO.save(trainee);
        log.info("Created trainee '{}' with id {}", trainee.getUsername(), trainee.getUserId());
        return trainee;
    }

    public Trainee update(Trainee trainee) {
        traineeDAO.update(trainee);
        log.info("Updated trainee with id {}", trainee.getUserId());
        return trainee;
    }

    public void delete(String traineeId) {
        traineeDAO.delete(traineeId);
        log.info("Deleted trainee with id {}", traineeId);
    }

    public Trainee select(String traineeId) {
        log.debug("Selecting trainee with id {}", traineeId);
        return traineeDAO.findById(traineeId);
    }

    public Collection<Trainee> selectAll() {
        return traineeDAO.findAll();
    }
}
