package com.daniel.taskspringcore.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.daniel.taskspringcore.model.Trainee;
import com.daniel.taskspringcore.model.Trainer;
import com.daniel.taskspringcore.model.Training;

@Configuration
public class StorageConfig {

    @Bean
    public Map<String, Trainee> traineeStorage() {
        return new HashMap<>();
    }

    @Bean
    public Map<String, Trainer> trainerStorage() {
        return new HashMap<>();
    }

    @Bean
    public Map<String, Training> trainingStorage() {
        return new HashMap<>();
    }
}
