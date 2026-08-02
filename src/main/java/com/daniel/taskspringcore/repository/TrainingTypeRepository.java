package com.daniel.taskspringcore.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.daniel.taskspringcore.model.TrainingType;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class TrainingTypeRepository {

    @PersistenceContext
    private EntityManager em;

    public Optional<TrainingType> findByTrainingTypeName(String trainingTypeName) {
        return em.createQuery("SELECT t FROM TrainingType t WHERE t.trainingTypeName = :name", TrainingType.class)
                .setParameter("name", trainingTypeName)
                .getResultStream()
                .findFirst();
    }

    public List<TrainingType> findAll() {
        return em.createQuery("SELECT t FROM TrainingType t", TrainingType.class).getResultList();
    }

    public long count() {
        return em.createQuery("SELECT COUNT(t) FROM TrainingType t", Long.class).getSingleResult();
    }
}
