package com.daniel.taskspringcore.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.daniel.taskspringcore.dao.TrainingDAO;
import com.daniel.taskspringcore.model.Training;
import com.daniel.taskspringcore.service.util.IdGenerator;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TrainingService {

    private TrainingDAO trainingDAO;

    @Autowired
    public void setTrainingDAO(TrainingDAO trainingDAO) {
        this.trainingDAO = trainingDAO;
    }

    public Training create(Training training) {
        if (training.getTrainingId() == null) {
            training.setTrainingId(IdGenerator.nextId(
                    trainingDAO.findAll().stream().map(Training::getTrainingId).toList()));
        }
        trainingDAO.save(training);
        log.info("Created training '{}' with id {}", training.getTrainingName(), training.getTrainingId());
        return training;
    }

    public Training select(String trainingId) {
        log.debug("Selecting training with id {}", trainingId);
        return trainingDAO.findById(trainingId);
    }

    public Collection<Training> selectAll() {
        return trainingDAO.findAll();
    }
}
