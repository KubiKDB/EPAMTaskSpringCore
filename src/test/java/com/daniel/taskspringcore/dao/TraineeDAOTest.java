package com.daniel.taskspringcore.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.daniel.taskspringcore.model.Trainee;
import com.daniel.taskspringcore.model.Trainer;
import com.daniel.taskspringcore.model.Training;
import com.daniel.taskspringcore.model.TrainingType;

import jakarta.persistence.EntityManager;

@SpringBootTest
@Transactional
class TraineeDAOTest {

    @Autowired
    private TraineeDAO traineeDAO;
    @Autowired
    private TrainerDAO trainerDAO;
    @Autowired
    private TrainingDAO trainingDAO;
    @Autowired
    private TrainingTypeDAO trainingTypeDAO;
    @Autowired
    private EntityManager em;

    private Trainee trainee(String first, String last, String username) {
        return new Trainee(first, last, username, "pw12345678", true, LocalDate.of(1990, 1, 1), "Main St");
    }

    @Test
    void savesAndFindsByUsername() {
        traineeDAO.save(trainee("John", "Smith", "john.smith"));

        Optional<Trainee> found = traineeDAO.findByUsername("john.smith");

        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("John");
    }

    @Test
    void findByUsernameReturnsEmptyWhenMissing() {
        assertThat(traineeDAO.findByUsername("nobody")).isEmpty();
    }

    @Test
    void updatePersistsChanges() {
        Trainee t = trainee("John", "Smith", "john.smith2");
        traineeDAO.save(t);
        em.flush();
        em.clear();

        Trainee toUpdate = traineeDAO.findByUsername("john.smith2").orElseThrow();
        toUpdate.setAddress("New Address");
        traineeDAO.update(toUpdate);
        em.flush();
        em.clear();

        assertThat(traineeDAO.findByUsername("john.smith2").orElseThrow().getAddress())
                .isEqualTo("New Address");
    }

    @Test
    void findAllReturnsAllTrainees() {
        traineeDAO.save(trainee("John", "Smith", "john.smith3"));
        traineeDAO.save(trainee("Jane", "Doe", "jane.doe3"));

        assertThat(traineeDAO.findAll()).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void deleteByUsernameCascadesTrainingsAndJoinRows() {
        Trainee t = trainee("John", "Smith", "john.smith4");
        Trainer trainer = new Trainer("Anna", "Jones", "anna.jones4", "pw12345678", true,
                trainingTypeDAO.findAll().get(0));
        trainerDAO.save(trainer);
        t.getTrainers().add(trainer);
        traineeDAO.save(t);

        TrainingType type = trainingTypeDAO.findAll().get(0);
        Training training = new Training(t, trainer, "Morning Session", type, LocalDate.now(), 60);
        trainingDAO.save(training);
        em.flush();
        em.clear();

        traineeDAO.deleteByUsername("john.smith4");
        em.flush();
        em.clear();

        assertThat(traineeDAO.findByUsername("john.smith4")).isEmpty();
        List<Training> remaining = em.createQuery(
                        "SELECT tr FROM Training tr WHERE tr.trainee.id = :id", Training.class)
                .setParameter("id", t.getId())
                .getResultList();
        assertThat(remaining).isEmpty();
    }
}
