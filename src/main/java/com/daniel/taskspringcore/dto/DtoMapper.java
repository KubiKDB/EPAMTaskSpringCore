package com.daniel.taskspringcore.dto;

import java.util.List;

import com.daniel.taskspringcore.model.Trainee;
import com.daniel.taskspringcore.model.Trainer;
import com.daniel.taskspringcore.model.Training;
import com.daniel.taskspringcore.model.TrainingType;

public final class DtoMapper {

    private DtoMapper() {
    }

    public static TraineeDTO toDto(Trainee trainee) {
        List<TrainerDTO> trainers = trainee.getTrainers().stream()
                .map(DtoMapper::toDto)
                .toList();
        return new TraineeDTO(trainee.getUsername(), trainee.getFirstName(), trainee.getLastName(),
                trainee.getDateOfBirth(), trainee.getAddress(), trainee.isActive(), trainers);
    }

    public static TrainerDTO toDto(Trainer trainer) {
        String specialization = trainer.getSpecialization() != null
                ? trainer.getSpecialization().getTrainingTypeName()
                : null;
        return new TrainerDTO(trainer.getUsername(), trainer.getFirstName(), trainer.getLastName(),
                trainer.isActive(), specialization);
    }

    public static TrainerProfileDTO toProfileDto(Trainer trainer) {
        String specialization = trainer.getSpecialization() != null
                ? trainer.getSpecialization().getTrainingTypeName()
                : null;
        List<TraineeSummaryDTO> trainees = trainer.getTrainees().stream()
                .map(t -> new TraineeSummaryDTO(t.getUsername(), t.getFirstName(), t.getLastName()))
                .toList();
        return new TrainerProfileDTO(trainer.getUsername(), trainer.getFirstName(), trainer.getLastName(),
                trainer.isActive(), specialization, trainees);
    }

    public static TrainingTypeDTO toDto(TrainingType trainingType) {
        return new TrainingTypeDTO(trainingType.getId(), trainingType.getTrainingTypeName());
    }

    public static TrainingDTO toDto(Training training) {
        return new TrainingDTO(training.getId(),
                training.getTrainee().getUsername(), training.getTrainer().getUsername(),
                training.getTrainingName(), training.getTrainingType().getTrainingTypeName(),
                training.getTrainingDate(), training.getTrainingDuration());
    }

    public static List<TrainingDTO> toTrainingDtos(List<Training> trainings) {
        return trainings.stream().map(DtoMapper::toDto).toList();
    }

    public static List<TrainerDTO> toTrainerDtos(List<Trainer> trainers) {
        return trainers.stream().map(DtoMapper::toDto).toList();
    }
}
