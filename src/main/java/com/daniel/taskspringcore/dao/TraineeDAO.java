package com.daniel.taskspringcore.dao;

import java.util.Collection;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.daniel.taskspringcore.model.Trainee;

@Repository
public class TraineeDAO {

    private Map<String, Trainee> traineeStorage;

    @Autowired
    public void setTraineeStorage(@Qualifier("traineeStorage") Map<String, Trainee> traineeStorage) {
        this.traineeStorage = traineeStorage;
    }

    public Trainee save(Trainee trainee) {
        return traineeStorage.put(trainee.getUserId(), trainee);
    }

    public Trainee update(Trainee trainee) {
        return traineeStorage.put(trainee.getUserId(), trainee);
    }

    public Trainee findById(String traineeId) {
        return traineeStorage.get(traineeId);
    }

    public Collection<Trainee> findAll() {
        return traineeStorage.values();
    }

    public void delete(String traineeId) {
        traineeStorage.remove(traineeId);
    }
}