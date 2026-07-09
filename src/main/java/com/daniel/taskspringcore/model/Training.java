package com.daniel.taskspringcore.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "trainings")
@Getter
@Setter
@NoArgsConstructor
public class Training {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "trainee_id")
    Trainee trainee;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "trainer_id")
    Trainer trainer;

    @Column(nullable = false)
    String trainingName;

    @ManyToOne(optional = false)
    @JoinColumn(name = "training_type_id")
    TrainingType trainingType;

    @Column(nullable = false)
    LocalDate trainingDate;

    // Duration in minutes
    @Column(nullable = false)
    Integer trainingDuration;

    public Training(Trainee trainee, Trainer trainer, String trainingName,
                    TrainingType trainingType, LocalDate trainingDate, Integer trainingDuration) {
        this.trainee = trainee;
        this.trainer = trainer;
        this.trainingName = trainingName;
        this.trainingType = trainingType;
        this.trainingDate = trainingDate;
        this.trainingDuration = trainingDuration;
    }
}
