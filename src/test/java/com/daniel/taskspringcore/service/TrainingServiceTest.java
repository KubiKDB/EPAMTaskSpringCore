package com.daniel.taskspringcore.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.daniel.taskspringcore.dao.TrainingDAO;
import com.daniel.taskspringcore.model.Training;
import com.daniel.taskspringcore.model.TrainingType;

class TrainingServiceTest {

    @Mock
    private TrainingDAO trainingDAO;
    @InjectMocks
    private TrainingService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Training newTraining(String id) {
        Training t = new Training();
        t.setTrainingId(id);
        t.setTrainingName("Morning Yoga");
        t.setTrainingType(TrainingType.Yoga);
        t.setTrainingDate(new Date());
        t.setTrainingDuration(Duration.ofMinutes(60));
        return t;
    }

    @Test
    void createAssignsIdWhenMissing() {
        when(trainingDAO.findAll()).thenReturn(List.of());
        Training result = service.create(newTraining(null));
        assertThat(result.getTrainingId()).isEqualTo("1");
        verify(trainingDAO).save(result);
    }

    @Test
    void createKeepsProvidedId() {
        Training result = service.create(newTraining("99"));
        assertThat(result.getTrainingId()).isEqualTo("99");
        verify(trainingDAO).save(result);
    }

    @Test
    void selectDelegatesToDao() {
        Training t = newTraining("1");
        when(trainingDAO.findById("1")).thenReturn(t);
        assertThat(service.select("1")).isSameAs(t);
    }
}
