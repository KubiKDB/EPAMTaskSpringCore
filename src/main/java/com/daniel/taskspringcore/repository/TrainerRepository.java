package com.daniel.taskspringcore.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.daniel.taskspringcore.model.Trainer;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class TrainerRepository {

    @PersistenceContext
    private EntityManager em;

    public Trainer save(Trainer trainer) {
        em.persist(trainer);
        return trainer;
    }

    public Trainer update(Trainer trainer) {
        return em.merge(trainer);
    }

    public Optional<Trainer> findByUsername(String username) {
        return em.createQuery("SELECT t FROM Trainer t WHERE t.username = :username", Trainer.class)
                .setParameter("username", username)
                .getResultStream()
                .findFirst();
    }

    public List<Trainer> findAll() {
        return em.createQuery("SELECT t FROM Trainer t", Trainer.class).getResultList();
    }

    public long countActive() {
        return em.createQuery("SELECT COUNT(t) FROM Trainer t WHERE t.isActive = true", Long.class)
                .getSingleResult();
    }

    public List<Trainer> findNotAssignedToTrainee(String traineeUsername) {
        return em.createQuery("""
                        SELECT tr FROM Trainer tr WHERE tr NOT IN (
                            SELECT tr2 FROM Trainee te JOIN te.trainers tr2 WHERE te.username = :username
                        )""", Trainer.class)
                .setParameter("username", traineeUsername)
                .getResultList();
    }
}
