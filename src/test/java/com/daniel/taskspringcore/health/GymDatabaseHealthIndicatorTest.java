package com.daniel.taskspringcore.health;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.Status;
import org.springframework.mock.env.MockEnvironment;

import com.daniel.taskspringcore.service.GymStatsService;

class GymDatabaseHealthIndicatorTest {

    private GymStatsService statsService;
    private DataSource dataSource;
    private MockEnvironment environment;
    private GymDatabaseHealthIndicator indicator;

    @BeforeEach
    void setUp() throws SQLException {
        statsService = mock(GymStatsService.class);
        dataSource = mock(DataSource.class);
        environment = new MockEnvironment();
        environment.setActiveProfiles("stg");
        indicator = new GymDatabaseHealthIndicator(statsService, dataSource, environment);

        DatabaseMetaData metaData = mock(DatabaseMetaData.class);
        when(metaData.getDatabaseProductName()).thenReturn("PostgreSQL");
        when(metaData.getDatabaseProductVersion()).thenReturn("16.2");
        when(metaData.getURL()).thenReturn("jdbc:postgresql://db:5432/gymcrm_stg");
        Connection connection = mock(Connection.class);
        when(connection.getMetaData()).thenReturn(metaData);
        when(dataSource.getConnection()).thenReturn(connection);
    }

    @Test
    void reportsUpWithTheResolvedDatabaseAndProfile() {
        when(statsService.countUsers()).thenReturn(12L);

        Health health = indicator.health();

        assertThat(health.getStatus()).isEqualTo(Status.UP);
        assertThat(health.getDetails())
                .containsEntry("users", 12L)
                .containsEntry("profiles", "stg")
                .containsEntry("database", "PostgreSQL")
                .containsEntry("url", "jdbc:postgresql://db:5432/gymcrm_stg");
    }

    @Test
    void reportsDownWhenTheSchemaIsNotUsable() {
        when(statsService.countUsers()).thenThrow(new IllegalStateException("relation users does not exist"));

        Health health = indicator.health();

        assertThat(health.getStatus()).isEqualTo(Status.DOWN);
        assertThat(health.getDetails()).containsEntry("profiles", "stg");
        assertThat(health.getDetails()).containsKey("error");
    }

    @Test
    void staysUpWhenOnlyTheMetadataLookupFails() throws SQLException {
        when(statsService.countUsers()).thenReturn(1L);
        when(dataSource.getConnection()).thenThrow(new SQLException("pool exhausted"));

        Health health = indicator.health();

        assertThat(health.getStatus()).isEqualTo(Status.UP);
        assertThat(health.getDetails()).doesNotContainKey("database");
    }

    @Test
    void sanitizeStripsJdbcUrlParametersThatMayCarryCredentials() {
        assertThat(GymDatabaseHealthIndicator.sanitize("jdbc:postgresql://db:5432/gymcrm?user=gym&password=secret"))
                .isEqualTo("jdbc:postgresql://db:5432/gymcrm");
        assertThat(GymDatabaseHealthIndicator.sanitize("jdbc:h2:mem:gymcrm")).isEqualTo("jdbc:h2:mem:gymcrm");
        assertThat(GymDatabaseHealthIndicator.sanitize(null)).isEqualTo("unknown");
    }
}
