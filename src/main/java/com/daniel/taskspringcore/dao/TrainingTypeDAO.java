package com.daniel.taskspringcore.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.daniel.taskspringcore.model.TrainingType;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class TrainingTypeDAO {

    @PersistenceContext
    private EntityManager em;

    public Optional<TrainingType> findByName(String trainingTypeName) {
        return em.createQuery("SELECT t FROM TrainingType t WHERE t.trainingTypeName = :name", TrainingType.class)
                .setParameter("name", trainingTypeName)
                .getResultStream()
                .findFirst();
    }

    public List<TrainingType> findAll() {
        return em.createQuery("SELECT t FROM TrainingType t", TrainingType.class).getResultList();
    }
}
