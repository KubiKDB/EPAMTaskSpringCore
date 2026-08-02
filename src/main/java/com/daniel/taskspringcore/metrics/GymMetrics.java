package com.daniel.taskspringcore.metrics;

import java.time.Duration;

import org.springframework.stereotype.Component;

import com.daniel.taskspringcore.service.GymStatsService;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class GymMetrics {

    public static final String REGISTRATIONS = "gym.profile.registrations";
    public static final String AUTHENTICATION_ATTEMPTS = "gym.authentication.attempts";
    public static final String AUTHENTICATION_DURATION = "gym.authentication.duration";
    public static final String TRAININGS_CREATED = "gym.trainings";
    public static final String ACTIVE_TRAINEES = "gym.trainees.active";
    public static final String ACTIVE_TRAINERS = "gym.trainers.active";

    public static final String ROLE_TRAINEE = "trainee";
    public static final String ROLE_TRAINER = "trainer";
    public static final String OUTCOME_SUCCESS = "success";
    public static final String OUTCOME_FAILURE = "failure";

    private static final String UNKNOWN_TYPE = "unknown";

    private final MeterRegistry registry;

    public GymMetrics(MeterRegistry registry, GymStatsService statsService) {
        this.registry = registry;

        Gauge.builder(ACTIVE_TRAINEES, statsService, GymStatsService::countActiveTrainees)
                .description("Trainee profiles that are currently active")
                .register(registry);
        Gauge.builder(ACTIVE_TRAINERS, statsService, GymStatsService::countActiveTrainers)
                .description("Trainer profiles that are currently active")
                .register(registry);

        log.info("Gym domain metrics registered with {}", registry.getClass().getSimpleName());
    }

    public void recordTraineeRegistration() {
        recordRegistration(ROLE_TRAINEE);
    }

    public void recordTrainerRegistration() {
        recordRegistration(ROLE_TRAINER);
    }

    public void recordAuthentication(String outcome, Duration duration) {
        registry.counter(AUTHENTICATION_ATTEMPTS, "outcome", outcome).increment();
        registry.timer(AUTHENTICATION_DURATION, "outcome", outcome).record(duration);
        log.trace("Recorded {} authentication taking {}", outcome, duration);
    }

    public void recordTrainingCreated(String trainingTypeName) {
        String type = (trainingTypeName == null || trainingTypeName.isBlank())
                ? UNKNOWN_TYPE
                : trainingTypeName;
        registry.counter(TRAININGS_CREATED, "type", type).increment();
        log.trace("Recorded created training of type '{}'", type);
    }

    private void recordRegistration(String role) {
        registry.counter(REGISTRATIONS, "role", role).increment();
        log.trace("Recorded {} registration", role);
    }
}