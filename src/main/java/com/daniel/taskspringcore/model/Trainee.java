package com.daniel.taskspringcore.model;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class Trainee extends User {
    Date dateOfBirth;
    String address;
    String userId;

    public Trainee(String firstName, String lastName, String username, String password,
                   boolean isActive, Date dateOfBirth, String address, String userId) {
        super(firstName, lastName, username, password, isActive);
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.userId = userId;
    }
}
