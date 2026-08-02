package com.daniel.taskspringcore.service.util;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.daniel.taskspringcore.dao.UserDAO;
import com.daniel.taskspringcore.exception.AuthenticationException;
import com.daniel.taskspringcore.metrics.GymMetrics;
import com.daniel.taskspringcore.model.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AuthenticationService {

    private UserDAO userDAO;
    private GymMetrics metrics;

    @Autowired
    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Autowired
    public void setMetrics(GymMetrics metrics) {
        this.metrics = metrics;
    }

    public void authenticate(String username, String password) {
        long startNanos = System.nanoTime();
        try {
            verify(username, password);
            record(GymMetrics.OUTCOME_SUCCESS, startNanos);
            log.debug("Authenticated user {}", username);
        } catch (AuthenticationException ex) {
            record(GymMetrics.OUTCOME_FAILURE, startNanos);
            throw ex;
        }
    }

    private void verify(String username, String password) {
        User user = userDAO.findByUsername(username)
                .orElseThrow(() -> failure(username));
        if (!user.getPassword().equals(password)) {
            throw failure(username);
        }
    }

    private void record(String outcome, long startNanos) {
        metrics.recordAuthentication(outcome, Duration.ofNanos(System.nanoTime() - startNanos));
    }

    private AuthenticationException failure(String username) {
        log.warn("Authentication failed for username {}", username);
        return new AuthenticationException("Invalid username or password for: " + username);
    }
}
