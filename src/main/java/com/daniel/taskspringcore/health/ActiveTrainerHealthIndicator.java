package com.daniel.taskspringcore.health;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

import com.daniel.taskspringcore.service.GymStatsService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ActiveTrainerHealthIndicator implements HealthIndicator {

    private final GymStatsService statsService;

    public ActiveTrainerHealthIndicator(GymStatsService statsService) {
        this.statsService = statsService;
    }

    @Override
    public Health health() {
        try {
            long activeTrainers = statsService.countActiveTrainers();
            long activeTrainees = statsService.countActiveTrainees();
            if (activeTrainers == 0) {
                log.warn("Health check degraded: no active trainers are available for {} active trainees",
                        activeTrainees);
                return Health.outOfService()
                        .withDetail("activeTrainers", activeTrainers)
                        .withDetail("activeTrainees", activeTrainees)
                        .withDetail("reason", "No active trainer is available to deliver trainings")
                        .build();
            }
            return Health.up()
                    .withDetail("activeTrainers", activeTrainers)
                    .withDetail("activeTrainees", activeTrainees)
                    .build();
        } catch (Exception ex) {
            log.error("Health check failed: unable to count active trainers", ex);
            return Health.down(ex).build();
        }
    }
}