package com.daniel.taskspringcore.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.daniel.taskspringcore.model.Trainee;

class TraineeDAOTest {

    private TraineeDAO dao;

    @BeforeEach
    void setUp() {
        dao = new TraineeDAO();
        dao.setTraineeStorage(new HashMap<>());
    }

    private Trainee trainee(String id, String first, String last) {
        Trainee t = new Trainee();
        t.setUserId(id);
        t.setFirstName(first);
        t.setLastName(last);
        return t;
    }

    @Test
    void savesAndFindsById() {
        dao.save(trainee("1", "John", "Smith"));
        assertThat(dao.findById("1")).isNotNull()
                .extracting(Trainee::getFirstName).isEqualTo("John");
    }

    @Test
    void updateReplacesExisting() {
        dao.save(trainee("1", "John", "Smith"));
        dao.update(trainee("1", "Johnny", "Smith"));
        assertThat(dao.findById("1").getFirstName()).isEqualTo("Johnny");
        assertThat(dao.findAll()).hasSize(1);
    }

    @Test
    void findAllReturnsAll() {
        dao.save(trainee("1", "John", "Smith"));
        dao.save(trainee("2", "Jane", "Doe"));
        assertThat(dao.findAll()).hasSize(2);
    }

    @Test
    void deleteRemoves() {
        dao.save(trainee("1", "John", "Smith"));
        dao.delete("1");
        assertThat(dao.findById("1")).isNull();
        assertThat(dao.findAll()).isEmpty();
    }
}
