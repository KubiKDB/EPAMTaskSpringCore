package com.daniel.taskspringcore.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.daniel.taskspringcore.dao.TrainerDAO;
import com.daniel.taskspringcore.model.Trainer;
import com.daniel.taskspringcore.model.TrainingType;
import com.daniel.taskspringcore.service.util.UserCredentialGenerator;

class TrainerServiceTest {

    @Mock
    private TrainerDAO trainerDAO;
    @Mock
    private UserCredentialGenerator credentialGenerator;
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
        t.setSpecialization(TrainingType.Yoga);
        return t;
    }

    @Test
    void createComputesCredentialsAndId() {
        when(trainerDAO.findAll()).thenReturn(List.of());
        when(credentialGenerator.generateUsername(eq("Anna"), eq("Jones"), anySet()))
                .thenReturn("Anna.Jones");
        when(credentialGenerator.generatePassword()).thenReturn("xyz9876543");

        Trainer result = service.create(newTrainer());

        assertThat(result.getUsername()).isEqualTo("Anna.Jones");
        assertThat(result.getPassword()).isEqualTo("xyz9876543");
        assertThat(result.isActive()).isTrue();
        assertThat(result.getUserId()).isEqualTo("1");
        verify(trainerDAO).save(result);
    }

    @Test
    void updateDelegatesToDaoAndReturnsUpdatedEntity() {
        Trainer t = newTrainer();
        t.setUserId("1");
        when(trainerDAO.update(t)).thenReturn(null);
        Trainer result = service.update(t);
        verify(trainerDAO).update(t);
        assertThat(result).isSameAs(t);
    }

    @Test
    void selectDelegatesToDao() {
        Trainer t = newTrainer();
        when(trainerDAO.findById("1")).thenReturn(t);
        assertThat(service.select("1")).isSameAs(t);
    }
}
