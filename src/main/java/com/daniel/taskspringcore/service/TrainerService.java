package com.daniel.taskspringcore.service;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.daniel.taskspringcore.dao.TrainerDAO;
import com.daniel.taskspringcore.model.Trainer;
import com.daniel.taskspringcore.service.util.UserCredentialGenerator;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TrainerService {

    private TrainerDAO trainerDAO;
    private UserCredentialGenerator credentialGenerator;

    @Autowired
    public void setTrainerDAO(TrainerDAO trainerDAO) {
        this.trainerDAO = trainerDAO;
    }

    @Autowired
    public void setCredentialGenerator(UserCredentialGenerator credentialGenerator) {
        this.credentialGenerator = credentialGenerator;
    }

    public Trainer create(Trainer trainer) {
        Set<String> existingUsernames = trainerDAO.findAll().stream()
                .map(Trainer::getUsername)
                .collect(Collectors.toSet());
        trainer.setUsername(credentialGenerator.generateUsername(
                trainer.getFirstName(), trainer.getLastName(), existingUsernames));
        trainer.setPassword(credentialGenerator.generatePassword());
        trainer.setActive(true);
        if (trainer.getUserId() == null) {
            trainer.setUserId(nextId());
        }
        trainerDAO.save(trainer);
        log.info("Created trainer '{}' with id {}", trainer.getUsername(), trainer.getUserId());
        return trainer;
    }

    public Trainer update(Trainer trainer) {
        trainerDAO.update(trainer);
        log.info("Updated trainer with id {}", trainer.getUserId());
        return trainer;
    }

    public Trainer select(String trainerId) {
        log.debug("Selecting trainer with id {}", trainerId);
        return trainerDAO.findById(trainerId);
    }

    public Collection<Trainer> selectAll() {
        return trainerDAO.findAll();
    }

    private String nextId() {
        long max = trainerDAO.findAll().stream()
                .map(Trainer::getUserId)
                .filter(id -> id != null && id.matches("\\d+"))
                .mapToLong(Long::parseLong)
                .max()
                .orElse(0L);
        return String.valueOf(max + 1);
    }
}
