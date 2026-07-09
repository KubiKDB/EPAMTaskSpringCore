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
import com.daniel.taskspringcore.model.TrainingType;

import jakarta.persistence.EntityManager;

@SpringBootTest
@Transactional
class TrainerDAOTest {

    @Autowired
    private TrainerDAO trainerDAO;
    @Autowired
    private TraineeDAO traineeDAO;
    @Autowired
    private TrainingTypeDAO trainingTypeDAO;
    @Autowired
    private EntityManager em;

    private Trainer trainer(String first, String last, String username, TrainingType spec) {
        return new Trainer(first, last, username, "pw12345678", true, spec);
    }

    @Test
    void savesAndFindsByUsername() {
        TrainingType yoga = trainingTypeDAO.findByName("Yoga").orElseThrow();
        trainerDAO.save(trainer("Anna", "Jones", "anna.jones", yoga));

        Optional<Trainer> found = trainerDAO.findByUsername("anna.jones");

        assertThat(found).isPresent();
        assertThat(found.get().getSpecialization().getTrainingTypeName()).isEqualTo("Yoga");
    }

    @Test
    void updatePersistsChanges() {
        TrainingType yoga = trainingTypeDAO.findByName("Yoga").orElseThrow();
        TrainingType strength = trainingTypeDAO.findByName("Strength").orElseThrow();
        Trainer t = trainer("Anna", "Jones", "anna.jones2", yoga);
        trainerDAO.save(t);
        em.flush();
        em.clear();

        Trainer toUpdate = trainerDAO.findByUsername("anna.jones2").orElseThrow();
        toUpdate.setSpecialization(strength);
        trainerDAO.update(toUpdate);
        em.flush();
        em.clear();

        assertThat(trainerDAO.findByUsername("anna.jones2").orElseThrow().getSpecialization().getTrainingTypeName())
                .isEqualTo("Strength");
    }

    @Test
    void findAllReturnsAllTrainers() {
        TrainingType yoga = trainingTypeDAO.findByName("Yoga").orElseThrow();
        trainerDAO.save(trainer("Anna", "Jones", "anna.jones3", yoga));
        trainerDAO.save(trainer("Mike", "Brown", "mike.brown3", yoga));

        assertThat(trainerDAO.findAll()).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void findNotAssignedToTraineeExcludesAssignedTrainers() {
        TrainingType yoga = trainingTypeDAO.findByName("Yoga").orElseThrow();
        Trainer assigned = trainer("Anna", "Jones", "anna.jones4", yoga);
        Trainer unassigned = trainer("Mike", "Brown", "mike.brown4", yoga);
        trainerDAO.save(assigned);
        trainerDAO.save(unassigned);

        Trainee trainee = new Trainee("John", "Smith", "john.smith5", "pw12345678", true,
                LocalDate.of(1990, 1, 1), "Main St");
        trainee.getTrainers().add(assigned);
        traineeDAO.save(trainee);
        em.flush();
        em.clear();

        List<Trainer> result = trainerDAO.findNotAssignedToTrainee("john.smith5");

        assertThat(result).extracting(Trainer::getUsername).contains("mike.brown4")
                .doesNotContain("anna.jones4");
    }
}
