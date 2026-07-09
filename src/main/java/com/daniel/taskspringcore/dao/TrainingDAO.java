package com.daniel.taskspringcore.dao;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.daniel.taskspringcore.model.Trainee;
import com.daniel.taskspringcore.model.Trainer;
import com.daniel.taskspringcore.model.Training;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Repository
public class TrainingDAO {

    @PersistenceContext
    private EntityManager em;

    public Training save(Training training) {
        em.persist(training);
        return training;
    }

    public List<Training> findTraineeTrainings(String traineeUsername, LocalDate fromDate, LocalDate toDate,
                                               String trainerName, String trainingTypeName) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Training> query = cb.createQuery(Training.class);
        Root<Training> training = query.from(Training.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(training.get("trainee").get("username"), traineeUsername));
        addDateRange(cb, training, predicates, fromDate, toDate);
        if (trainerName != null) {
            Join<Training, Trainer> trainer = training.join("trainer");
            predicates.add(cb.equal(trainer.get("firstName"), trainerName));
        }
        if (trainingTypeName != null) {
            predicates.add(cb.equal(training.get("trainingType").get("trainingTypeName"), trainingTypeName));
        }

        query.select(training).where(predicates.toArray(new Predicate[0]));
        return em.createQuery(query).getResultList();
    }

    public List<Training> findTrainerTrainings(String trainerUsername, LocalDate fromDate, LocalDate toDate,
                                               String traineeName) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Training> query = cb.createQuery(Training.class);
        Root<Training> training = query.from(Training.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(training.get("trainer").get("username"), trainerUsername));
        addDateRange(cb, training, predicates, fromDate, toDate);
        if (traineeName != null) {
            Join<Training, Trainee> trainee = training.join("trainee");
            predicates.add(cb.equal(trainee.get("firstName"), traineeName));
        }

        query.select(training).where(predicates.toArray(new Predicate[0]));
        return em.createQuery(query).getResultList();
    }

    private void addDateRange(CriteriaBuilder cb, Root<Training> training, List<Predicate> predicates,
                              LocalDate fromDate, LocalDate toDate) {
        if (fromDate != null) {
            predicates.add(cb.greaterThanOrEqualTo(training.get("trainingDate"), fromDate));
        }
        if (toDate != null) {
            predicates.add(cb.lessThanOrEqualTo(training.get("trainingDate"), toDate));
        }
    }
}
