package com.daniel.taskspringcore.metrics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.daniel.taskspringcore.service.GymStatsService;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

class GymMetricsTest {

    private SimpleMeterRegistry registry;
    private GymStatsService statsService;
    private GymMetrics metrics;

    @BeforeEach
    void setUp() {
        registry = new SimpleMeterRegistry();
        statsService = mock(GymStatsService.class);
        metrics = new GymMetrics(registry, statsService);
    }

    @Test
    void countsRegistrationsSeparatelyPerRole() {
        metrics.recordTraineeRegistration();
        metrics.recordTraineeRegistration();
        metrics.recordTrainerRegistration();

        assertThat(counter(GymMetrics.REGISTRATIONS, "role", GymMetrics.ROLE_TRAINEE)).isEqualTo(2.0);
        assertThat(counter(GymMetrics.REGISTRATIONS, "role", GymMetrics.ROLE_TRAINER)).isEqualTo(1.0);
    }

    @Test
    void countsAndTimesAuthenticationPerOutcome() {
        metrics.recordAuthentication(GymMetrics.OUTCOME_SUCCESS, Duration.ofMillis(10));
        metrics.recordAuthentication(GymMetrics.OUTCOME_FAILURE, Duration.ofMillis(30));
        metrics.recordAuthentication(GymMetrics.OUTCOME_FAILURE, Duration.ofMillis(50));

        assertThat(counter(GymMetrics.AUTHENTICATION_ATTEMPTS, "outcome", GymMetrics.OUTCOME_SUCCESS)).isEqualTo(1.0);
        assertThat(counter(GymMetrics.AUTHENTICATION_ATTEMPTS, "outcome", GymMetrics.OUTCOME_FAILURE)).isEqualTo(2.0);

        assertThat(registry.timer(GymMetrics.AUTHENTICATION_DURATION, "outcome", GymMetrics.OUTCOME_FAILURE).count())
                .isEqualTo(2);
    }

    @Test
    void countsTrainingsByType() {
        metrics.recordTrainingCreated("Yoga");
        metrics.recordTrainingCreated("Cardio");
        metrics.recordTrainingCreated("Yoga");

        assertThat(counter(GymMetrics.TRAININGS_CREATED, "type", "Yoga")).isEqualTo(2.0);
        assertThat(counter(GymMetrics.TRAININGS_CREATED, "type", "Cardio")).isEqualTo(1.0);
    }

    @Test
    void fallsBackToAnUnknownTagRatherThanFailingOnABlankType() {
        metrics.recordTrainingCreated(null);
        metrics.recordTrainingCreated("  ");

        assertThat(counter(GymMetrics.TRAININGS_CREATED, "type", "unknown")).isEqualTo(2.0);
    }

    @Test
    void gaugesReadCurrentCountsAtScrapeTime() {
        when(statsService.countActiveTrainees()).thenReturn(7L, 9L);
        when(statsService.countActiveTrainers()).thenReturn(2L);

        assertThat(registry.get(GymMetrics.ACTIVE_TRAINEES).gauge().value()).isEqualTo(7.0);
        assertThat(registry.get(GymMetrics.ACTIVE_TRAINEES).gauge().value()).isEqualTo(9.0);
        assertThat(registry.get(GymMetrics.ACTIVE_TRAINERS).gauge().value()).isEqualTo(2.0);
    }

    private double counter(String name, String tagKey, String tagValue) {
        return registry.get(name).tag(tagKey, tagValue).counter().count();
    }
}
