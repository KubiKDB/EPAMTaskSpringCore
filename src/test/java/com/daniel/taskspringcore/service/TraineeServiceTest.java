package com.daniel.taskspringcore.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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

import com.daniel.taskspringcore.dao.TraineeDAO;
import com.daniel.taskspringcore.model.Trainee;
import com.daniel.taskspringcore.service.util.UserCredentialGenerator;

class TraineeServiceTest {

    @Mock
    private TraineeDAO traineeDAO;
    @Mock
    private UserCredentialGenerator credentialGenerator;
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
    void createComputesCredentialsAndId() {
        when(traineeDAO.findAll()).thenReturn(List.of());
        when(credentialGenerator.generateUsername(eq("John"), eq("Smith"), anySet()))
                .thenReturn("John.Smith");
        when(credentialGenerator.generatePassword()).thenReturn("abc1234567");

        Trainee result = service.create(newTrainee());

        assertThat(result.getUsername()).isEqualTo("John.Smith");
        assertThat(result.getPassword()).isEqualTo("abc1234567");
        assertThat(result.isActive()).isTrue();
        assertThat(result.getUserId()).isEqualTo("1");
        verify(traineeDAO).save(result);
    }

    @Test
    void createAssignsNextIdAfterExisting() {
        Trainee existing = newTrainee();
        existing.setUserId("5");
        when(traineeDAO.findAll()).thenReturn(List.of(existing));
        when(credentialGenerator.generateUsername(any(), any(), anySet())).thenReturn("John.Smith1");
        when(credentialGenerator.generatePassword()).thenReturn("pwpwpwpwpw");

        Trainee result = service.create(newTrainee());

        assertThat(result.getUserId()).isEqualTo("6");
    }

    @Test
    void updateDelegatesToDaoAndReturnsUpdatedEntity() {
        Trainee t = newTrainee();
        t.setUserId("1");
        when(traineeDAO.update(t)).thenReturn(null);
        Trainee result = service.update(t);
        verify(traineeDAO).update(t);
        assertThat(result).isSameAs(t);
    }

    @Test
    void deleteDelegatesToDao() {
        service.delete("1");
        verify(traineeDAO).delete("1");
    }

    @Test
    void selectDelegatesToDao() {
        Trainee t = newTrainee();
        when(traineeDAO.findById("1")).thenReturn(t);
        assertThat(service.select("1")).isSameAs(t);
    }
}
