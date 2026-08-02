package com.daniel.taskspringcore.health;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

import com.daniel.taskspringcore.service.GymStatsService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TrainingTypeCatalogHealthIndicator implements HealthIndicator {

    private final GymStatsService statsService;

    public TrainingTypeCatalogHealthIndicator(GymStatsService statsService) {
        this.statsService = statsService;
    }

    @Override
    public Health health() {
        try {
            long trainingTypes = statsService.countTrainingTypes();
            if (trainingTypes == 0) {
                log.warn("Health check failed: the training type catalogue is empty");
                return Health.down()
                        .withDetail("trainingTypes", trainingTypes)
                        .withDetail("reason", "Training type reference data is missing; trainings cannot be created")
                        .build();
            }
            return Health.up().withDetail("trainingTypes", trainingTypes).build();
        } catch (Exception ex) {
            log.error("Health check failed: unable to read the training type catalogue", ex);
            return Health.down(ex).build();
        }
    }
}