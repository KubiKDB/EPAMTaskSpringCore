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

class TrainingTypeCatalogHealthIndicatorTest {

    @Mock
    private GymStatsService statsService;
    @InjectMocks
    private TrainingTypeCatalogHealthIndicator indicator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void reportsUpWhenCatalogIsSeeded() {
        when(statsService.countTrainingTypes()).thenReturn(3L);

        Health health = indicator.health();

        assertThat(health.getStatus()).isEqualTo(Status.UP);
        assertThat(health.getDetails()).containsEntry("trainingTypes", 3L);
    }

    @Test
    void reportsDownWhenCatalogIsEmpty() {
        when(statsService.countTrainingTypes()).thenReturn(0L);

        Health health = indicator.health();

        assertThat(health.getStatus()).isEqualTo(Status.DOWN);
        assertThat(health.getDetails()).containsEntry("trainingTypes", 0L);
        assertThat(health.getDetails().get("reason").toString()).contains("reference data is missing");
    }

    @Test
    void reportsDownWhenTheQueryFails() {
        when(statsService.countTrainingTypes()).thenThrow(new IllegalStateException("no connection"));

        Health health = indicator.health();

        assertThat(health.getStatus()).isEqualTo(Status.DOWN);
        assertThat(health.getDetails()).containsKey("error");
    }
}
