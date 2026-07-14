package com.daniel.taskspringcore.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.daniel.taskspringcore.model.Trainee;
import com.daniel.taskspringcore.repository.TraineeRepository;

@Component
public class TraineeDAO {

    private final TraineeRepository traineeRepository;

    public TraineeDAO(TraineeRepository traineeRepository) {
        this.traineeRepository = traineeRepository;
    }

    public Trainee save(Trainee trainee) {
        return traineeRepository.save(trainee);
    }

    public Trainee update(Trainee trainee) {
        return traineeRepository.update(trainee);
    }

    public Optional<Trainee> findByUsername(String username) {
        return traineeRepository.findByUsername(username);
    }

    public List<Trainee> findAll() {
        return traineeRepository.findAll();
    }

    public void deleteByUsername(String username) {
        traineeRepository.deleteByUsername(username);
    }
}
