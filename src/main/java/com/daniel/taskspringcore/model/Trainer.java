package com.daniel.taskspringcore.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Trainer extends User {
    TrainingType specialization;
    String userId;

    public Trainer(String firstName, String lastName, String username, String password,
                   boolean isActive, TrainingType specialization, String userId) {
        super(firstName, lastName, username, password, isActive);
        this.specialization = specialization;
        this.userId = userId;
    }
}
