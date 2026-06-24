package com.daniel.taskspringcore.model;

import java.time.Duration;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Training {
    String trainingId; // The ERD in task didn't include this, but I decided that other fields aren't as suitable as primary key as explicit id
    String traineeId;
    String trainerId;
    String trainingName;
    TrainingType trainingType;
    Date trainingDate;
    Duration trainingDuration;
}
