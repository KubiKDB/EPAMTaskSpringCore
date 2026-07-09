package com.daniel.taskspringcore.service.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.daniel.taskspringcore.dao.UserDAO;
import com.daniel.taskspringcore.exception.AuthenticationException;
import com.daniel.taskspringcore.model.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AuthenticationService {

    private UserDAO userDAO;

    @Autowired
    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public void authenticate(String username, String password) {
        User user = userDAO.findByUsername(username)
                .orElseThrow(() -> failure(username));
        if (!user.getPassword().equals(password)) {
            throw failure(username);
        }
        log.debug("Authenticated user {}", username);
    }

    private AuthenticationException failure(String username) {
        log.warn("Authentication failed for username {}", username);
        return new AuthenticationException("Invalid username or password for: " + username);
    }
}
