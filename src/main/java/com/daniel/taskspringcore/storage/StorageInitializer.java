package com.daniel.taskspringcore.storage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.Map;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import com.daniel.taskspringcore.model.Trainee;
import com.daniel.taskspringcore.model.Trainer;
import com.daniel.taskspringcore.model.Training;
import com.daniel.taskspringcore.model.TrainingType;

@Component
public class StorageInitializer implements BeanPostProcessor {

    private static final Logger log = LoggerFactory.getLogger(StorageInitializer.class);

    private static final String TRAINEE_STORAGE = "traineeStorage";
    private static final String TRAINER_STORAGE = "trainerStorage";
    private static final String TRAINING_STORAGE = "trainingStorage";

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private String dataFilePath;
    private ResourceLoader resourceLoader;

    @Value("${storage.init.file}")
    public void setDataFilePath(String dataFilePath) {
        this.dataFilePath = dataFilePath;
    }

    @Autowired
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        switch (beanName) {
            case TRAINEE_STORAGE ->
                    seed(beanName, "trainee", p -> parseTrainee((Map<String, Trainee>) bean, p));
            case TRAINER_STORAGE ->
                    seed(beanName, "trainer", p -> parseTrainer((Map<String, Trainer>) bean, p));
            case TRAINING_STORAGE ->
                    seed(beanName, "training", p -> parseTraining((Map<String, Training>) bean, p));
            default -> { /* not a storage bean */ }
        }
        return bean;
    }

    private void seed(String beanName, String typePrefix, Consumer<String[]> recordHandler) {
        Resource resource = resourceLoader.getResource(dataFilePath);
        if (!resource.exists()) {
            log.warn("Storage seed file '{}' not found; bean '{}' left empty", dataFilePath, beanName);
            return;
        }
        int count = 0;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                String[] parts = line.split(";");
                if (!parts[0].trim().equals(typePrefix)) {
                    continue;
                }
                recordHandler.accept(parts);
                count++;
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read storage seed file: " + dataFilePath, e);
        }
        log.info("Seeded {} '{}' record(s) into bean '{}'", count, typePrefix, beanName);
    }

    private void parseTrainee(Map<String, Trainee> storage, String[] p) {
        String userId = p[1].trim();
        Trainee trainee = new Trainee(
                p[2].trim(), p[3].trim(), p[4].trim(), p[5].trim(),
                Boolean.parseBoolean(p[6].trim()),
                parseDate(p[7].trim()), p[8].trim(), userId);
        storage.put(userId, trainee);
    }

    private void parseTrainer(Map<String, Trainer> storage, String[] p) {
        String userId = p[1].trim();
        Trainer trainer = new Trainer(
                p[2].trim(), p[3].trim(), p[4].trim(), p[5].trim(),
                Boolean.parseBoolean(p[6].trim()),
                TrainingType.valueOf(p[7].trim()), userId);
        storage.put(userId, trainer);
    }

    private void parseTraining(Map<String, Training> storage, String[] p) {
        String trainingId = p[1].trim();
        Training training = new Training(
                trainingId, p[2].trim(), p[3].trim(), p[4].trim(),
                TrainingType.valueOf(p[5].trim()),
                parseDate(p[6].trim()),
                Duration.ofMinutes(Long.parseLong(p[7].trim())));
        storage.put(trainingId, training);
    }

    private Date parseDate(String value) {
        try {
            return dateFormat.parse(value);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid date in seed file: " + value, e);
        }
    }
}