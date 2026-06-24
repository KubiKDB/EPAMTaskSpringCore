package com.daniel.taskspringcore.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    String firstName;
    String lastName;
    String username;
    String password;
    boolean isActive;
}