package com.daniel.taskspringcore.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.daniel.taskspringcore.model.TrainingType;
import com.daniel.taskspringcore.repository.TrainingTypeRepository;

@Component
public class TrainingTypeDAO {

    private final TrainingTypeRepository trainingTypeRepository;

    public TrainingTypeDAO(TrainingTypeRepository trainingTypeRepository) {
        this.trainingTypeRepository = trainingTypeRepository;
    }

    public Optional<TrainingType> findByName(String trainingTypeName) {
        return trainingTypeRepository.findByTrainingTypeName(trainingTypeName);
    }

    public List<TrainingType> findAll() {
        return trainingTypeRepository.findAll();
    }

    public long count() {
        return trainingTypeRepository.count();
    }
}
