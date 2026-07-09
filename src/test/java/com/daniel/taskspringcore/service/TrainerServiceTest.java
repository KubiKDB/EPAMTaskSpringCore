package com.daniel.taskspringcore.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.daniel.taskspringcore.dao.TrainerDAO;
import com.daniel.taskspringcore.dao.TrainingDAO;
import com.daniel.taskspringcore.dao.UserDAO;
import com.daniel.taskspringcore.exception.EntityNotFoundException;
import com.daniel.taskspringcore.model.Trainer;
import com.daniel.taskspringcore.model.TrainingType;
import com.daniel.taskspringcore.service.util.AuthenticationService;
import com.daniel.taskspringcore.service.util.UserCredentialGenerator;

class TrainerServiceTest {

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
    private TrainerService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Trainer newTrainer() {
        Trainer t = new Trainer();
        t.setFirstName("Anna");
        t.setLastName("Jones");
        t.setSpecialization(new TrainingType("Yoga"));
        return t;
    }

    @Test
    void createComputesCredentialsAndActivatesTrainer() {
        when(userDAO.findAllUsernames()).thenReturn(List.of());
        when(credentialGenerator.generateUsername(eq("Anna"), eq("Jones"), anySet()))
                .thenReturn("Anna.Jones");
        when(credentialGenerator.generatePassword()).thenReturn("xyz9876543");

        Trainer result = service.create(newTrainer());

        assertThat(result.getUsername()).isEqualTo("Anna.Jones");
        assertThat(result.getPassword()).isEqualTo("xyz9876543");
        assertThat(result.isActive()).isTrue();
        verify(trainerDAO).save(result);
    }

    @Test
    void createRejectsMissingSpecialization() {
        Trainer t = newTrainer();
        t.setSpecialization(null);

        assertThatThrownBy(() -> service.create(t)).isInstanceOf(RuntimeException.class);
    }

    @Test
    void selectByUsernameReturnsTrainer() {
        Trainer t = newTrainer();
        t.setUsername("anna.jones");
        when(trainerDAO.findByUsername("anna.jones")).thenReturn(Optional.of(t));

        Trainer result = service.selectByUsername("anna.jones", "pw", "anna.jones");

        assertThat(result).isSameAs(t);
    }

    @Test
    void selectByUsernameThrowsWhenNotFound() {
        when(trainerDAO.findByUsername("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.selectByUsername("missing", "pw", "missing"))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void changePasswordUpdatesEntity() {
        Trainer t = newTrainer();
        t.setUsername("anna.jones");
        when(trainerDAO.findByUsername("anna.jones")).thenReturn(Optional.of(t));

        service.changePassword("anna.jones", "oldPassword1", "newPassword1");

        assertThat(t.getPassword()).isEqualTo("newPassword1");
        verify(trainerDAO).update(t);
    }

    @Test
    void deactivateTwiceThrowsBecauseNotIdempotent() {
        Trainer t = newTrainer();
        t.setUsername("anna.jones");
        t.setActive(false);
        when(trainerDAO.findByUsername("anna.jones")).thenReturn(Optional.of(t));

        assertThatThrownBy(() -> service.deactivate("anna.jones", "pw", "anna.jones"))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void activateSetsActive() {
        Trainer t = newTrainer();
        t.setUsername("anna.jones");
        t.setActive(false);
        when(trainerDAO.findByUsername("anna.jones")).thenReturn(Optional.of(t));

        service.activate("anna.jones", "pw", "anna.jones");

        assertThat(t.isActive()).isTrue();
        verify(trainerDAO).update(t);
    }

    @Test
    void getUnassignedTrainersDelegatesToDao() {
        service.getUnassignedTrainers("anna.jones", "pw", "john.smith");

        verify(trainerDAO).findNotAssignedToTrainee("john.smith");
    }

    @Test
    void getTrainingsDelegatesToTrainingDao() {
        service.getTrainings("anna.jones", "pw", "anna.jones", null, null, null);

        verify(trainingDAO).findTrainerTrainings("anna.jones", null, null, null);
    }
}
