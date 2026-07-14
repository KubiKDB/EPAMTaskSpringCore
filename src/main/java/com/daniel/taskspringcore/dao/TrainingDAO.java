package com.daniel.taskspringcore.dao;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Component;

import com.daniel.taskspringcore.model.Training;
import com.daniel.taskspringcore.repository.TrainingRepository;

@Component
public class TrainingDAO {

    private final TrainingRepository trainingRepository;

    public TrainingDAO(TrainingRepository trainingRepository) {
        this.trainingRepository = trainingRepository;
    }

    public Training save(Training training) {
        return trainingRepository.save(training);
    }

    public List<Training> findTraineeTrainings(String traineeUsername, LocalDate fromDate, LocalDate toDate,
                                               String trainerName, String trainingTypeName) {
        return trainingRepository.findTraineeTrainings(traineeUsername, fromDate, toDate, trainerName, trainingTypeName);
    }

    public List<Training> findTrainerTrainings(String trainerUsername, LocalDate fromDate, LocalDate toDate,
                                               String traineeName) {
        return trainingRepository.findTrainerTrainings(trainerUsername, fromDate, toDate, traineeName);
    }
}
