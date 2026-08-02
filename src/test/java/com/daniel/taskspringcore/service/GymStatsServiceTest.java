package com.daniel.taskspringcore.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.daniel.taskspringcore.dao.TraineeDAO;
import com.daniel.taskspringcore.dao.TrainerDAO;
import com.daniel.taskspringcore.dao.TrainingTypeDAO;
import com.daniel.taskspringcore.dao.UserDAO;

class GymStatsServiceTest {

    @Mock
    private TraineeDAO traineeDAO;
    @Mock
    private TrainerDAO trainerDAO;
    @Mock
    private TrainingTypeDAO trainingTypeDAO;
    @Mock
    private UserDAO userDAO;
    @InjectMocks
    private GymStatsService statsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void delegatesEachCountToItsDao() {
        when(traineeDAO.countActive()).thenReturn(5L);
        when(trainerDAO.countActive()).thenReturn(3L);
        when(trainingTypeDAO.count()).thenReturn(4L);
        when(userDAO.count()).thenReturn(8L);

        assertThat(statsService.countActiveTrainees()).isEqualTo(5L);
        assertThat(statsService.countActiveTrainers()).isEqualTo(3L);
        assertThat(statsService.countTrainingTypes()).isEqualTo(4L);
        assertThat(statsService.countUsers()).isEqualTo(8L);
    }
}
