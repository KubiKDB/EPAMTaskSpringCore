package com.daniel.taskspringcore.service.util;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.daniel.taskspringcore.dao.UserDAO;
import com.daniel.taskspringcore.exception.AuthenticationException;
import com.daniel.taskspringcore.metrics.GymMetrics;
import com.daniel.taskspringcore.model.User;

class AuthenticationServiceTest {

    private UserDAO userDAO;
    private GymMetrics metrics;
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        userDAO = mock(UserDAO.class);
        metrics = mock(GymMetrics.class);
        authenticationService = new AuthenticationService();
        authenticationService.setUserDAO(userDAO);
        authenticationService.setMetrics(metrics);
    }

    @Test
    void acceptsMatchingCredentialsAndRecordsSuccess() {
        when(userDAO.findByUsername("John.Smith"))
                .thenReturn(Optional.of(new User("John", "Smith", "John.Smith", "secret", true)));

        assertThatCode(() -> authenticationService.authenticate("John.Smith", "secret"))
                .doesNotThrowAnyException();

        verify(metrics).recordAuthentication(eq(GymMetrics.OUTCOME_SUCCESS), any(Duration.class));
    }

    @Test
    void rejectsAWrongPasswordAndRecordsFailure() {
        when(userDAO.findByUsername("John.Smith"))
                .thenReturn(Optional.of(new User("John", "Smith", "John.Smith", "secret", true)));

        assertThatThrownBy(() -> authenticationService.authenticate("John.Smith", "wrong"))
                .isInstanceOf(AuthenticationException.class);

        verify(metrics).recordAuthentication(eq(GymMetrics.OUTCOME_FAILURE), any(Duration.class));
    }

    @Test
    void rejectsAnUnknownUsernameAndRecordsFailure() {
        when(userDAO.findByUsername("Nobody")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authenticationService.authenticate("Nobody", "secret"))
                .isInstanceOf(AuthenticationException.class);

        verify(metrics).recordAuthentication(eq(GymMetrics.OUTCOME_FAILURE), any(Duration.class));
    }
}
