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

import com.daniel.taskspringcore.dto.CreateTraineeDTO;
import com.daniel.taskspringcore.dto.CreateTrainerDTO;
import com.daniel.taskspringcore.dto.TraineeDTO;
import com.daniel.taskspringcore.dto.TrainerDTO;
import com.daniel.taskspringcore.dto.TrainerProfileDTO;
import com.daniel.taskspringcore.dto.TrainingDTO;
import com.daniel.taskspringcore.dto.UserCredentialsDTO;
import com.daniel.taskspringcore.metrics.GymMetrics;
import com.daniel.taskspringcore.service.TraineeService;
import com.daniel.taskspringcore.service.TrainerService;
import com.daniel.taskspringcore.service.TrainingService;
import com.daniel.taskspringcore.service.TrainingTypeService;
import com.daniel.taskspringcore.service.UserService;

class GymFacadeTest {

    @Mock
    private TraineeService traineeService;
    @Mock
    private TrainerService trainerService;
    @Mock
    private TrainingService trainingService;
    @Mock
    private TrainingTypeService trainingTypeService;
    @Mock
    private UserService userService;
    @Mock
    private GymMetrics metrics;
    @InjectMocks
    private GymFacade facade;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private TraineeDTO traineeDto() {
        return new TraineeDTO("john.smith", "John", "Smith", null, "Main St", true, List.of());
    }

    private TrainerDTO trainerDto() {
        return new TrainerDTO("anna.jones", "Anna", "Jones", true, "Yoga");
    }

    private TrainerProfileDTO trainerProfileDto() {
        return new TrainerProfileDTO("anna.jones", "Anna", "Jones", true, "Yoga", List.of());
    }

    @Test
    void createOperationsDelegateToServices() {
        CreateTraineeDTO trainee = new CreateTraineeDTO("John", "Smith", null, null);
        CreateTrainerDTO trainer = new CreateTrainerDTO("Anna", "Jones", "Yoga");
        UserCredentialsDTO traineeCreds = new UserCredentialsDTO("john.smith", "pw1");
        UserCredentialsDTO trainerCreds = new UserCredentialsDTO("anna.jones", "pw2");
        when(traineeService.create(trainee)).thenReturn(traineeCreds);
        when(trainerService.create(trainer)).thenReturn(trainerCreds);

        assertThat(facade.createTrainee(trainee)).isSameAs(traineeCreds);
        assertThat(facade.createTrainer(trainer)).isSameAs(trainerCreds);

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
        TraineeDTO trainee = traineeDto();
        TrainerProfileDTO trainer = trainerProfileDto();
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
        TraineeDTO trainee = traineeDto();
        TrainerDTO trainer = trainerDto();
        TrainerProfileDTO trainerProfile = trainerProfileDto();
        when(traineeService.update("a", "pw", trainee)).thenReturn(trainee);
        when(trainerService.update("a", "pw", trainer)).thenReturn(trainerProfile);

        assertThat(facade.updateTrainee("a", "pw", trainee)).isSameAs(trainee);
        assertThat(facade.updateTrainer("a", "pw", trainer)).isSameAs(trainerProfile);
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
        LocalDate date = LocalDate.now();
        TrainingDTO training = new TrainingDTO(1L, "john.smith", "anna.jones",
                "Yoga Session", "Yoga", date, 60);
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
        TraineeDTO trainee = traineeDto();
        List<String> trainerUsernames = List.of("anna.jones");
        when(traineeService.updateTrainersList("a", "pw", "john.smith", trainerUsernames)).thenReturn(trainee);

        assertThat(facade.updateTraineeTrainers("a", "pw", "john.smith", trainerUsernames)).isSameAs(trainee);
    }
}
