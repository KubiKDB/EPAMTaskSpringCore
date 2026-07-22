package com.daniel.taskspringcore.web;

import java.util.List;

import com.daniel.taskspringcore.dto.TrainingDTO;
import com.daniel.taskspringcore.dto.response.TraineeTrainingResponse;
import com.daniel.taskspringcore.dto.response.TrainerTrainingResponse;

public final class WebMapper {

    private WebMapper() {
    }

    public static List<TraineeTrainingResponse> toTraineeTrainings(List<TrainingDTO> trainings) {
        return trainings.stream()
                .map(t -> new TraineeTrainingResponse(t.trainingName(), t.trainingDate(),
                        t.trainingTypeName(), t.trainingDuration(), t.trainerUsername()))
                .toList();
    }

    public static List<TrainerTrainingResponse> toTrainerTrainings(List<TrainingDTO> trainings) {
        return trainings.stream()
                .map(t -> new TrainerTrainingResponse(t.trainingName(), t.trainingDate(),
                        t.trainingTypeName(), t.trainingDuration(), t.traineeUsername()))
                .toList();
    }
}
