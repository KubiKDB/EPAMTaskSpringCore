package com.daniel.taskspringcore.dao;

import java.util.Collection;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.daniel.taskspringcore.model.Training;

@Repository
public class TrainingDAO {

    private Map<String, Training> trainingStorage;

    @Autowired
    public void setTrainingStorage(@Qualifier("trainingStorage") Map<String, Training> trainingStorage) {
        this.trainingStorage = trainingStorage;
    }

    public Training save(Training training) {
        return trainingStorage.put(training.getTrainingId(), training);
    }

    public Training findById(String trainingId) {
        return trainingStorage.get(trainingId);
    }

    public Collection<Training> findAll() {
        return trainingStorage.values();
    }
}
