package com.daniel.taskspringcore.health;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.daniel.taskspringcore.service.GymStatsService;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.Status;

class ActiveTrainerHealthIndicatorTest {

    @Mock
    private GymStatsService statsService;
    @InjectMocks
    private ActiveTrainerHealthIndicator indicator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void reportsUpWhenTrainersAreAvailable() {
        when(statsService.countActiveTrainers()).thenReturn(4L);
        when(statsService.countActiveTrainees()).thenReturn(17L);

        Health health = indicator.health();

        assertThat(health.getStatus()).isEqualTo(Status.UP);
        assertThat(health.getDetails())
                .containsEntry("activeTrainers", 4L)
                .containsEntry("activeTrainees", 17L);
    }

    @Test
    void reportsOutOfServiceWhenNoTrainerIsActive() {
        when(statsService.countActiveTrainers()).thenReturn(0L);
        when(statsService.countActiveTrainees()).thenReturn(17L);

        Health health = indicator.health();

        assertThat(health.getStatus()).isEqualTo(Status.OUT_OF_SERVICE);
        assertThat(health.getDetails()).containsEntry("activeTrainers", 0L);
    }

    @Test
    void reportsDownWhenTheQueryFails() {
        when(statsService.countActiveTrainers()).thenThrow(new IllegalStateException("no connection"));

        Health health = indicator.health();

        assertThat(health.getStatus()).isEqualTo(Status.DOWN);
        assertThat(health.getDetails()).containsKey("error");
    }
}
