package com.daniel.taskspringcore.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

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
    void createOperationsDelegateToServices() {
        Trainee trainee = new Trainee();
        Trainer trainer = new Trainer();
        when(traineeService.create(trainee)).thenReturn(trainee);
        when(trainerService.create(trainer)).thenReturn(trainer);

        assertThat(facade.createTrainee(trainee)).isSameAs(trainee);
        assertThat(facade.createTrainer(trainer)).isSameAs(trainer);

        verify(traineeService).create(trainee);
        verify(trainerService).create(trainer);
    }

    @Test
    void authenticationOperationsDelegateToServices() {
        facade.authenticateTrainee("john.smith", "pw");
        facade.authenticateTrainer("anna.jones", "pw");

        verify(traineeService).authenticate("john.smith", "pw");
        verify(trainerService).authenticate("anna.jones", "pw");
    }

    @Test
    void selectByUsernameOperationsDelegateToServices() {
        Trainee trainee = new Trainee();
        Trainer trainer = new Trainer();
        when(traineeService.selectByUsername("a", "pw", "john.smith")).thenReturn(trainee);
        when(trainerService.selectByUsername("a", "pw", "anna.jones")).thenReturn(trainer);

        assertThat(facade.getTraineeByUsername("a", "pw", "john.smith")).isSameAs(trainee);
        assertThat(facade.getTrainerByUsername("a", "pw", "anna.jones")).isSameAs(trainer);
    }

    @Test
    void passwordChangeOperationsDelegateToServices() {
        facade.changeTraineePassword("john.smith", "old", "new");
        facade.changeTrainerPassword("anna.jones", "old", "new");

        verify(traineeService).changePassword("john.smith", "old", "new");
        verify(trainerService).changePassword("anna.jones", "old", "new");
    }

    @Test
    void updateOperationsDelegateToServices() {
        Trainee trainee = new Trainee();
        Trainer trainer = new Trainer();
        when(traineeService.update("a", "pw", trainee)).thenReturn(trainee);
        when(trainerService.update("a", "pw", trainer)).thenReturn(trainer);

        assertThat(facade.updateTrainee("a", "pw", trainee)).isSameAs(trainee);
        assertThat(facade.updateTrainer("a", "pw", trainer)).isSameAs(trainer);
    }

    @Test
    void activateDeactivateOperationsDelegateToServices() {
        facade.activateTrainee("a", "pw", "john.smith");
        facade.deactivateTrainee("a", "pw", "john.smith");
        facade.activateTrainer("a", "pw", "anna.jones");
        facade.deactivateTrainer("a", "pw", "anna.jones");

        verify(traineeService).activate("a", "pw", "john.smith");
        verify(traineeService).deactivate("a", "pw", "john.smith");
        verify(trainerService).activate("a", "pw", "anna.jones");
        verify(trainerService).deactivate("a", "pw", "anna.jones");
    }

    @Test
    void deleteTraineeDelegatesToService() {
        facade.deleteTraineeByUsername("a", "pw", "john.smith");

        verify(traineeService).deleteByUsername("a", "pw", "john.smith");
    }

    @Test
    void trainingsListOperationsDelegateToServices() {
        facade.getTraineeTrainings("a", "pw", "john.smith", null, null, null, null);
        facade.getTrainerTrainings("a", "pw", "anna.jones", null, null, null);

        verify(traineeService).getTrainings("a", "pw", "john.smith", null, null, null, null);
        verify(trainerService).getTrainings("a", "pw", "anna.jones", null, null, null);
    }

    @Test
    void addTrainingDelegatesToService() {
        Training training = new Training();
        LocalDate date = LocalDate.now();
        when(trainingService.create("a", "pw", "john.smith", "anna.jones", "Yoga Session", "Yoga", date, 60))
                .thenReturn(training);

        assertThat(facade.addTraining("a", "pw", "john.smith", "anna.jones", "Yoga Session", "Yoga", date, 60))
                .isSameAs(training);
    }

    @Test
    void getUnassignedTrainersDelegatesToService() {
        facade.getUnassignedTrainers("a", "pw", "john.smith");

        verify(trainerService).getUnassignedTrainers("a", "pw", "john.smith");
    }

    @Test
    void updateTraineeTrainersDelegatesToService() {
        Trainee trainee = new Trainee();
        List<String> trainerUsernames = List.of("anna.jones");
        when(traineeService.updateTrainersList("a", "pw", "john.smith", trainerUsernames)).thenReturn(trainee);

        assertThat(facade.updateTraineeTrainers("a", "pw", "john.smith", trainerUsernames)).isSameAs(trainee);
    }
}
