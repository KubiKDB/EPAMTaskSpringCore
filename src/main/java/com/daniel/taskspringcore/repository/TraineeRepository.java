package com.daniel.taskspringcore.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.daniel.taskspringcore.model.Trainee;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class TraineeRepository {

    @PersistenceContext
    private EntityManager em;

    public Trainee save(Trainee trainee) {
        em.persist(trainee);
        return trainee;
    }

    public Trainee update(Trainee trainee) {
        return em.merge(trainee);
    }

    public Optional<Trainee> findByUsername(String username) {
        return em.createQuery("SELECT t FROM Trainee t WHERE t.username = :username", Trainee.class)
                .setParameter("username", username)
                .getResultStream()
                .findFirst();
    }

    public List<Trainee> findAll() {
        return em.createQuery("SELECT t FROM Trainee t", Trainee.class).getResultList();
    }

    public long countActive() {
        return em.createQuery("SELECT COUNT(t) FROM Trainee t WHERE t.isActive = true", Long.class)
                .getSingleResult();
    }

    public void deleteByUsername(String username) {
        findByUsername(username).ifPresent(em::remove);
    }
}
