package com.daniel.taskspringcore.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.daniel.taskspringcore.dao.TraineeDAO;
import com.daniel.taskspringcore.dao.TrainerDAO;
import com.daniel.taskspringcore.dao.TrainingDAO;
import com.daniel.taskspringcore.dao.TrainingTypeDAO;
import com.daniel.taskspringcore.dto.TrainingDTO;
import com.daniel.taskspringcore.exception.EntityNotFoundException;
import com.daniel.taskspringcore.model.Trainee;
import com.daniel.taskspringcore.model.Trainer;
import com.daniel.taskspringcore.model.Training;
import com.daniel.taskspringcore.model.TrainingType;
import com.daniel.taskspringcore.service.util.AuthenticationService;

class TrainingServiceTest {

    @Mock
    private TrainingDAO trainingDAO;
    @Mock
    private TraineeDAO traineeDAO;
    @Mock
    private TrainerDAO trainerDAO;
    @Mock
    private TrainingTypeDAO trainingTypeDAO;
    @Mock
    private AuthenticationService authenticationService;
    @InjectMocks
    private TrainingService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createSavesTrainingAndLinksTrainerToTrainee() {
        Trainee trainee = new Trainee();
        trainee.setUsername("john.smith");
        Trainer trainer = new Trainer();
        trainer.setUsername("anna.jones");
        TrainingType yoga = new TrainingType("Yoga");
        when(traineeDAO.findByUsername("john.smith")).thenReturn(Optional.of(trainee));
        when(trainerDAO.findByUsername("anna.jones")).thenReturn(Optional.of(trainer));
        when(trainingTypeDAO.findByName("Yoga")).thenReturn(Optional.of(yoga));

        TrainingDTO result = service.create("john.smith", "pw", "john.smith", "anna.jones",
                "Morning Yoga", "Yoga", LocalDate.now(), 60);

        assertThat(result.trainingName()).isEqualTo("Morning Yoga");
        assertThat(result.traineeUsername()).isEqualTo("john.smith");
        assertThat(result.trainerUsername()).isEqualTo("anna.jones");
        assertThat(trainee.getTrainers()).contains(trainer);
        verify(trainingDAO).save(any(Training.class));
        verify(traineeDAO).update(trainee);
    }

    @Test
    void createThrowsWhenTraineeMissing() {
        when(traineeDAO.findByUsername("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create("a", "pw", "missing", "anna.jones",
                "Morning Yoga", "Yoga", LocalDate.now(), 60))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void createThrowsWhenTrainingTypeMissing() {
        Trainee trainee = new Trainee();
        trainee.setUsername("john.smith");
        Trainer trainer = new Trainer();
        trainer.setUsername("anna.jones");
        when(traineeDAO.findByUsername("john.smith")).thenReturn(Optional.of(trainee));
        when(trainerDAO.findByUsername("anna.jones")).thenReturn(Optional.of(trainer));
        when(trainingTypeDAO.findByName("Unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create("a", "pw", "john.smith", "anna.jones",
                "Morning Yoga", "Unknown", LocalDate.now(), 60))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
