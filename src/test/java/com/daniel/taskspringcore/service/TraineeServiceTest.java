package com.daniel.taskspringcore.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.daniel.taskspringcore.dao.TraineeDAO;
import com.daniel.taskspringcore.dao.TrainerDAO;
import com.daniel.taskspringcore.dao.TrainingDAO;
import com.daniel.taskspringcore.dao.UserDAO;
import com.daniel.taskspringcore.exception.AuthenticationException;
import com.daniel.taskspringcore.exception.EntityNotFoundException;
import com.daniel.taskspringcore.model.Trainee;
import com.daniel.taskspringcore.model.Trainer;
import com.daniel.taskspringcore.service.util.AuthenticationService;
import com.daniel.taskspringcore.service.util.UserCredentialGenerator;

class TraineeServiceTest {

    @Mock
    private TraineeDAO traineeDAO;
    @Mock
    private TrainerDAO trainerDAO;
    @Mock
    private TrainingDAO trainingDAO;
    @Mock
    private UserDAO userDAO;
    @Mock
    private UserCredentialGenerator credentialGenerator;
    @Mock
    private AuthenticationService authenticationService;
    @InjectMocks
    private TraineeService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Trainee newTrainee() {
        Trainee t = new Trainee();
        t.setFirstName("John");
        t.setLastName("Smith");
        return t;
    }

    @Test
    void createComputesCredentialsAndActivatesTrainee() {
        when(userDAO.findAllUsernames()).thenReturn(List.of());
        when(credentialGenerator.generateUsername(eq("John"), eq("Smith"), anySet()))
                .thenReturn("John.Smith");
        when(credentialGenerator.generatePassword()).thenReturn("abc1234567");

        Trainee result = service.create(newTrainee());

        assertThat(result.getUsername()).isEqualTo("John.Smith");
        assertThat(result.getPassword()).isEqualTo("abc1234567");
        assertThat(result.isActive()).isTrue();
        verify(traineeDAO).save(result);
    }

    @Test
    void createRejectsMissingFirstName() {
        Trainee t = newTrainee();
        t.setFirstName(null);

        assertThatThrownBy(() -> service.create(t)).isInstanceOf(RuntimeException.class);
    }

    @Test
    void selectByUsernameFailsAuthenticationPropagates() {
        doThrow(new AuthenticationException("bad credentials"))
                .when(authenticationService).authenticate("john.smith", "wrong");

        assertThatThrownBy(() -> service.selectByUsername("john.smith", "wrong", "john.smith"))
                .isInstanceOf(AuthenticationException.class);
    }

    @Test
    void selectByUsernameReturnsTrainee() {
        Trainee t = newTrainee();
        t.setUsername("john.smith");
        when(traineeDAO.findByUsername("john.smith")).thenReturn(Optional.of(t));

        Trainee result = service.selectByUsername("john.smith", "pw", "john.smith");

        assertThat(result).isSameAs(t);
    }

    @Test
    void selectByUsernameThrowsWhenNotFound() {
        when(traineeDAO.findByUsername("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.selectByUsername("missing", "pw", "missing"))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void changePasswordRequiresOldPasswordAuthentication() {
        doThrow(new AuthenticationException("bad credentials"))
                .when(authenticationService).authenticate("john.smith", "wrongOld");

        assertThatThrownBy(() -> service.changePassword("john.smith", "wrongOld", "newPassword1"))
                .isInstanceOf(AuthenticationException.class);
    }

    @Test
    void changePasswordUpdatesEntity() {
        Trainee t = newTrainee();
        t.setUsername("john.smith");
        when(traineeDAO.findByUsername("john.smith")).thenReturn(Optional.of(t));

        service.changePassword("john.smith", "oldPassword1", "newPassword1");

        assertThat(t.getPassword()).isEqualTo("newPassword1");
        verify(traineeDAO).update(t);
    }

    @Test
    void activateTwiceThrowsBecauseNotIdempotent() {
        Trainee t = newTrainee();
        t.setUsername("john.smith");
        t.setActive(true);
        when(traineeDAO.findByUsername("john.smith")).thenReturn(Optional.of(t));

        assertThatThrownBy(() -> service.activate("john.smith", "pw", "john.smith"))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void deactivateSetsInactive() {
        Trainee t = newTrainee();
        t.setUsername("john.smith");
        t.setActive(true);
        when(traineeDAO.findByUsername("john.smith")).thenReturn(Optional.of(t));

        service.deactivate("john.smith", "pw", "john.smith");

        assertThat(t.isActive()).isFalse();
        verify(traineeDAO).update(t);
    }

    @Test
    void deleteByUsernameDelegatesToDao() {
        Trainee t = newTrainee();
        t.setUsername("john.smith");
        when(traineeDAO.findByUsername("john.smith")).thenReturn(Optional.of(t));

        service.deleteByUsername("john.smith", "pw", "john.smith");

        verify(traineeDAO).deleteByUsername("john.smith");
    }

    @Test
    void updateTrainersListThrowsWhenTrainerUnknown() {
        Trainee t = newTrainee();
        t.setUsername("john.smith");
        when(traineeDAO.findByUsername("john.smith")).thenReturn(Optional.of(t));
        when(trainerDAO.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateTrainersList("john.smith", "pw", "john.smith", List.of("unknown")))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void updateTrainersListReplacesSet() {
        Trainee t = newTrainee();
        t.setUsername("john.smith");
        when(traineeDAO.findByUsername("john.smith")).thenReturn(Optional.of(t));
        Trainer trainer = new Trainer();
        trainer.setUsername("anna.jones");
        when(trainerDAO.findByUsername("anna.jones")).thenReturn(Optional.of(trainer));

        Trainee result = service.updateTrainersList("john.smith", "pw", "john.smith", List.of("anna.jones"));

        assertThat(result.getTrainers()).containsExactly(trainer);
        verify(traineeDAO).update(t);
    }

    @Test
    void getTrainingsDelegatesToTrainingDao() {
        service.getTrainings("john.smith", "pw", "john.smith", null, null, null, null);

        verify(trainingDAO).findTraineeTrainings("john.smith", null, null, null, null);
    }
}
