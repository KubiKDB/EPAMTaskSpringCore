package com.daniel.taskspringcore.dao;

import java.util.Collection;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.daniel.taskspringcore.model.Trainer;

@Repository
public class TrainerDAO {

    private Map<String, Trainer> trainerStorage;

    @Autowired
    public void setTrainerStorage(@Qualifier("trainerStorage") Map<String, Trainer> trainerStorage) {
        this.trainerStorage = trainerStorage;
    }

    public Trainer save(Trainer trainer) {
        return trainerStorage.put(trainer.getUserId(), trainer);
    }

    public Trainer update(Trainer trainer) {
        return trainerStorage.put(trainer.getUserId(), trainer);
    }

    public Trainer findById(String trainerId) {
        return trainerStorage.get(trainerId);
    }

    public Collection<Trainer> findAll() {
        return trainerStorage.values();
    }
}