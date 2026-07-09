package com.daniel.taskspringcore.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "trainers")
@PrimaryKeyJoinColumn(name = "user_id")
@Getter
@Setter
@NoArgsConstructor
public class Trainer extends User {
    @ManyToOne(optional = false)
    @JoinColumn(name = "specialization")
    TrainingType specialization;

    @OneToMany(mappedBy = "trainer", fetch = FetchType.LAZY)
    List<Training> trainings = new ArrayList<>();

    @ManyToMany(mappedBy = "trainers")
    Set<Trainee> trainees = new HashSet<>();

    public Trainer(String firstName, String lastName, String username, String password,
                   boolean isActive, TrainingType specialization) {
        super(firstName, lastName, username, password, isActive);
        this.specialization = specialization;
    }
}
