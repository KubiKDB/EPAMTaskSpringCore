package com.daniel.taskspringcore.storage;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.daniel.taskspringcore.facade.GymFacade;
import com.daniel.taskspringcore.model.Trainee;
import com.daniel.taskspringcore.model.Trainer;
import com.daniel.taskspringcore.model.Training;
import com.daniel.taskspringcore.model.TrainingType;
import com.daniel.taskspringcore.service.TraineeService;
import com.daniel.taskspringcore.service.TrainerService;
import com.daniel.taskspringcore.service.TrainingService;

@SpringBootTest
class StorageInitializerTest {

    @Autowired
    private GymFacade facade;
    @Autowired
    private TraineeService traineeService;
    @Autowired
    private TrainerService trainerService;
    @Autowired
    private TrainingService trainingService;

    @Test
    void seedsAllNamespacesFromCsv() {
        assertThat(traineeService.selectAll()).hasSize(2);
        assertThat(trainerService.selectAll()).hasSize(2);
        assertThat(trainingService.selectAll()).hasSize(2);
    }

    @Test
    void traineeFieldsAreParsedFromCsv() {
        Trainee trainee = facade.getTrainee("1");
        assertThat(trainee).isNotNull();
        assertThat(trainee.getFirstName()).isEqualTo("John");
        assertThat(trainee.getLastName()).isEqualTo("Smith");
        assertThat(trainee.getUsername()).isEqualTo("John.Smith");
        assertThat(trainee.getPassword()).isEqualTo("p4Ssw0rdJS");
        assertThat(trainee.isActive()).isTrue();
        assertThat(trainee.getAddress()).isEqualTo("221B Baker St");
        assertThat(trainee.getDateOfBirth()).isNotNull();
    }

    @Test
    void trainerSpecializationIsParsedFromCsv() {
        Trainer trainer = facade.getTrainer("1");
        assertThat(trainer).isNotNull();
        assertThat(trainer.getUsername()).isEqualTo("Anna.Jones");
        assertThat(trainer.getSpecialization()).isEqualTo(TrainingType.Yoga);
    }

    @Test
    void trainingFieldsAreParsedFromCsv() {
        Training training = facade.getTraining("1");
        assertThat(training).isNotNull();
        assertThat(training.getTrainingName()).isEqualTo("Morning Yoga");
        assertThat(training.getTrainingType()).isEqualTo(TrainingType.Yoga);
        assertThat(training.getTraineeId()).isEqualTo("1");
        assertThat(training.getTrainerId()).isEqualTo("1");
        assertThat(training.getTrainingDate()).isNotNull();
        assertThat(training.getTrainingDuration().toMinutes()).isEqualTo(60);
    }
}
