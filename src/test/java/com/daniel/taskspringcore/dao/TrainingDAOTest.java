package com.daniel.taskspringcore.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.daniel.taskspringcore.model.Training;
import com.daniel.taskspringcore.model.TrainingType;

class TrainingDAOTest {

    private TrainingDAO dao;

    @BeforeEach
    void setUp() {
        dao = new TrainingDAO();
        dao.setTrainingStorage(new HashMap<>());
    }

    private Training training(String id, String name) {
        return new Training(id, "1", "1", name, TrainingType.Yoga, new Date(), Duration.ofMinutes(60));
    }

    @Test
    void savesAndFindsById() {
        dao.save(training("1", "Morning Yoga"));
        assertThat(dao.findById("1")).isNotNull()
                .extracting(Training::getTrainingName).isEqualTo("Morning Yoga");
    }

    @Test
    void findAllReturnsAll() {
        dao.save(training("1", "Morning Yoga"));
        dao.save(training("2", "Evening Strength"));
        assertThat(dao.findAll()).hasSize(2);
    }
}
