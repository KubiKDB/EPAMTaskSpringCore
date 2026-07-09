package com.daniel.taskspringcore.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.daniel.taskspringcore.model.Trainee;
import com.daniel.taskspringcore.model.Trainer;
import com.daniel.taskspringcore.model.Training;
import com.daniel.taskspringcore.model.TrainingType;

@SpringBootTest
@Transactional
class TrainingDAOTest {

    @Autowired
    private TrainingDAO trainingDAO;
    @Autowired
    private TraineeDAO traineeDAO;
    @Autowired
    private TrainerDAO trainerDAO;
    @Autowired
    private TrainingTypeDAO trainingTypeDAO;

    private Trainee trainee;
    private Trainer trainer;
    private TrainingType yoga;
    private TrainingType strength;

    private void seed() {
        yoga = trainingTypeDAO.findByName("Yoga").orElseThrow();
        strength = trainingTypeDAO.findByName("Strength").orElseThrow();
        trainee = new Trainee("John", "Smith", "john.smith.training", "pw12345678", true,
                LocalDate.of(1990, 1, 1), "Main St");
        traineeDAO.save(trainee);
        trainer = new Trainer("Anna", "Jones", "anna.jones.training", "pw12345678", true, yoga);
        trainerDAO.save(trainer);
    }

    @Test
    void savesTraining() {
        seed();
        Training training = new Training(trainee, trainer, "Morning Yoga", yoga, LocalDate.now(), 60);

        trainingDAO.save(training);

        assertThat(training.getId()).isNotNull();
    }

    @Test
    void findTraineeTrainingsFiltersByDateRangeAndType() {
        seed();
        trainingDAO.save(new Training(trainee, trainer, "Yoga Session", yoga,
                LocalDate.of(2026, 1, 10), 60));
        trainingDAO.save(new Training(trainee, trainer, "Strength Session", strength,
                LocalDate.of(2026, 2, 10), 45));

        List<Training> yogaInJanuary = trainingDAO.findTraineeTrainings(
                "john.smith.training", LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31), null, "Yoga");

        assertThat(yogaInJanuary).extracting(Training::getTrainingName).containsExactly("Yoga Session");
    }

    @Test
    void findTraineeTrainingsFiltersByTrainerName() {
        seed();
        trainingDAO.save(new Training(trainee, trainer, "Yoga Session", yoga, LocalDate.now(), 60));

        List<Training> matching = trainingDAO.findTraineeTrainings(
                "john.smith.training", null, null, "Anna", null);
        List<Training> notMatching = trainingDAO.findTraineeTrainings(
                "john.smith.training", null, null, "Nobody", null);

        assertThat(matching).hasSize(1);
        assertThat(notMatching).isEmpty();
    }

    @Test
    void findTrainerTrainingsFiltersByTraineeName() {
        seed();
        trainingDAO.save(new Training(trainee, trainer, "Yoga Session", yoga, LocalDate.now(), 60));

        List<Training> matching = trainingDAO.findTrainerTrainings(
                "anna.jones.training", null, null, "John");
        List<Training> notMatching = trainingDAO.findTrainerTrainings(
                "anna.jones.training", null, null, "Nobody");

        assertThat(matching).hasSize(1);
        assertThat(notMatching).isEmpty();
    }
}
