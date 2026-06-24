package com.daniel.taskspringcore.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.daniel.taskspringcore.model.Trainee;
import com.daniel.taskspringcore.model.Trainer;
import com.daniel.taskspringcore.model.Training;
import com.daniel.taskspringcore.service.TraineeService;
import com.daniel.taskspringcore.service.TrainerService;
import com.daniel.taskspringcore.service.TrainingService;

class GymFacadeTest {

    @Mock
    private TraineeService traineeService;
    @Mock
    private TrainerService trainerService;
    @Mock
    private TrainingService trainingService;
    @InjectMocks
    private GymFacade facade;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void traineeOperationsDelegateToService() {
        Trainee t = new Trainee();
        when(traineeService.create(t)).thenReturn(t);
        when(traineeService.update(t)).thenReturn(t);
        when(traineeService.select("1")).thenReturn(t);

        assertThat(facade.createTrainee(t)).isSameAs(t);
        assertThat(facade.updateTrainee(t)).isSameAs(t);
        assertThat(facade.getTrainee("1")).isSameAs(t);
        facade.deleteTrainee("1");

        verify(traineeService).create(t);
        verify(traineeService).update(t);
        verify(traineeService).select("1");
        verify(traineeService).delete("1");
    }

    @Test
    void trainerOperationsDelegateToService() {
        Trainer t = new Trainer();
        when(trainerService.create(t)).thenReturn(t);
        when(trainerService.update(t)).thenReturn(t);
        when(trainerService.select("1")).thenReturn(t);

        assertThat(facade.createTrainer(t)).isSameAs(t);
        assertThat(facade.updateTrainer(t)).isSameAs(t);
        assertThat(facade.getTrainer("1")).isSameAs(t);

        verify(trainerService).create(t);
        verify(trainerService).update(t);
        verify(trainerService).select("1");
    }

    @Test
    void trainingOperationsDelegateToService() {
        Training t = new Training();
        when(trainingService.create(t)).thenReturn(t);
        when(trainingService.select("1")).thenReturn(t);

        assertThat(facade.createTraining(t)).isSameAs(t);
        assertThat(facade.getTraining("1")).isSameAs(t);

        verify(trainingService).create(t);
        verify(trainingService).select("1");
    }
}
