package com.daniel.taskspringcore.health;

import java.sql.Connection;
import java.sql.DatabaseMetaData;

import javax.sql.DataSource;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.daniel.taskspringcore.service.GymStatsService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class GymDatabaseHealthIndicator implements HealthIndicator {

    private final GymStatsService statsService;
    private final DataSource dataSource;
    private final Environment environment;

    public GymDatabaseHealthIndicator(GymStatsService statsService, DataSource dataSource,
                                      Environment environment) {
        this.statsService = statsService;
        this.dataSource = dataSource;
        this.environment = environment;
    }

    @Override
    public Health health() {
        try {
            long users = statsService.countUsers();
            Health.Builder builder = Health.up()
                    .withDetail("users", users)
                    .withDetail("profiles", String.join(",", environment.getActiveProfiles()));
            addConnectionDetails(builder);
            return builder.build();
        } catch (Exception ex) {
            log.error("Health check failed: the gym schema is not usable", ex);
            return Health.down(ex)
                    .withDetail("profiles", String.join(",", environment.getActiveProfiles()))
                    .build();
        }
    }

    private void addConnectionDetails(Health.Builder builder) {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            builder.withDetail("database", metaData.getDatabaseProductName())
                    .withDetail("version", metaData.getDatabaseProductVersion())
                    .withDetail("url", sanitize(metaData.getURL()));
        } catch (Exception ex) {
            log.warn("Could not read database metadata for the health report: {}", ex.getMessage());
        }
    }

    static String sanitize(String url) {
        if (url == null) {
            return "unknown";
        }
        int parameterStart = url.indexOf('?');
        return parameterStart < 0 ? url : url.substring(0, parameterStart);
    }
}