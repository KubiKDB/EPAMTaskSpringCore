package com.daniel.taskspringcore.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.daniel.taskspringcore.dao.UserDAO;
import com.daniel.taskspringcore.exception.EntityNotFoundException;
import com.daniel.taskspringcore.model.Trainee;
import com.daniel.taskspringcore.model.Trainer;

class UserServiceTest {

    @Mock
    private UserDAO userDAO;
    @Mock
    private TraineeService traineeService;
    @Mock
    private TrainerService trainerService;
    @InjectMocks
    private UserService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private void existsAsTrainee(String username) {
        when(userDAO.findByUsername(username)).thenReturn(Optional.of(new Trainee()));
    }

    private void existsAsTrainer(String username) {
        when(userDAO.findByUsername(username)).thenReturn(Optional.of(new Trainer()));
    }

    @Test
    void changePasswordDispatchesToTraineeService() {
        existsAsTrainee("john.smith");

        service.changePassword("john.smith", "old", "new");

        verify(traineeService).changePassword("john.smith", "old", "new");
    }

    @Test
    void changePasswordDispatchesToTrainerService() {
        existsAsTrainer("anna.jones");

        service.changePassword("anna.jones", "old", "new");

        verify(trainerService).changePassword("anna.jones", "old", "new");
    }

    @Test
    void setActiveActivatesTrainee() {
        existsAsTrainee("john.smith");

        service.setActive("a", "pw", "john.smith", true);

        verify(traineeService).activate("a", "pw", "john.smith");
    }

    @Test
    void setActiveDeactivatesTrainee() {
        existsAsTrainee("john.smith");

        service.setActive("a", "pw", "john.smith", false);

        verify(traineeService).deactivate("a", "pw", "john.smith");
    }

    @Test
    void setActiveActivatesTrainer() {
        existsAsTrainer("anna.jones");

        service.setActive("a", "pw", "anna.jones", true);

        verify(trainerService).activate("a", "pw", "anna.jones");
    }

    @Test
    void setActiveDeactivatesTrainer() {
        existsAsTrainer("anna.jones");

        service.setActive("a", "pw", "anna.jones", false);

        verify(trainerService).deactivate("a", "pw", "anna.jones");
    }

    @Test
    void unknownUserThrows() {
        when(userDAO.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.setActive("a", "pw", "ghost", true))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void authenticateDelegates() {
        service.authenticate("john.smith", "pw");

        verify(traineeService).authenticate("john.smith", "pw");
    }
}
