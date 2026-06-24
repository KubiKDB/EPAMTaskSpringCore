package com.daniel.taskspringcore.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.daniel.taskspringcore.model.Trainer;
import com.daniel.taskspringcore.model.TrainingType;

class TrainerDAOTest {

    private TrainerDAO dao;

    @BeforeEach
    void setUp() {
        dao = new TrainerDAO();
        dao.setTrainerStorage(new HashMap<>());
    }

    private Trainer trainer(String id, String first, String last, TrainingType spec) {
        Trainer t = new Trainer();
        t.setUserId(id);
        t.setFirstName(first);
        t.setLastName(last);
        t.setSpecialization(spec);
        return t;
    }

    @Test
    void savesAndFindsById() {
        dao.save(trainer("1", "Anna", "Jones", TrainingType.Yoga));
        assertThat(dao.findById("1")).isNotNull()
                .extracting(Trainer::getSpecialization).isEqualTo(TrainingType.Yoga);
    }

    @Test
    void updateReplacesExisting() {
        dao.save(trainer("1", "Anna", "Jones", TrainingType.Yoga));
        dao.update(trainer("1", "Anna", "Jones", TrainingType.Strength));
        assertThat(dao.findById("1").getSpecialization()).isEqualTo(TrainingType.Strength);
        assertThat(dao.findAll()).hasSize(1);
    }

    @Test
    void findAllReturnsAll() {
        dao.save(trainer("1", "Anna", "Jones", TrainingType.Yoga));
        dao.save(trainer("2", "Mike", "Brown", TrainingType.Strength));
        assertThat(dao.findAll()).hasSize(2);
    }
}
