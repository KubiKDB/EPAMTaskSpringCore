package com.daniel.taskspringcore.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.daniel.taskspringcore.model.Trainer;
import com.daniel.taskspringcore.repository.TrainerRepository;

@Component
public class TrainerDAO {

    private final TrainerRepository trainerRepository;

    public TrainerDAO(TrainerRepository trainerRepository) {
        this.trainerRepository = trainerRepository;
    }

    public Trainer save(Trainer trainer) {
        return trainerRepository.save(trainer);
    }

    public Trainer update(Trainer trainer) {
        return trainerRepository.update(trainer);
    }

    public Optional<Trainer> findByUsername(String username) {
        return trainerRepository.findByUsername(username);
    }

    public long countActive() {
        return trainerRepository.countActive();
    }

    public List<Trainer> findAll() {
        return trainerRepository.findAll();
    }

    public List<Trainer> findNotAssignedToTrainee(String traineeUsername) {
        return trainerRepository.findNotAssignedToTrainee(traineeUsername);
    }
}
