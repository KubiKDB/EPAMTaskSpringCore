package com.daniel.taskspringcore.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.daniel.taskspringcore.model.User;
import com.daniel.taskspringcore.repository.UserRepository;

@Component
public class UserDAO {

    private final UserRepository userRepository;

    public UserDAO(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<String> findAllUsernames() {
        return userRepository.findAllUsernames();
    }

    public long count() {
        return userRepository.count();
    }
}
