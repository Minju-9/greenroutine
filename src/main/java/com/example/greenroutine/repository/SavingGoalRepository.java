package com.example.greenroutine.repository;

import com.example.greenroutine.domain.SavingGoal;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SavingGoalRepository {
    @PersistenceContext
    private final EntityManager em;

    public Optional<SavingGoal> findByUserIdAndPeriodYm(Long userId, String periodYm) {
        List<SavingGoal> list = em.createQuery(
                        "select g from SavingGoal g " +
                                "where g.userId = :userId and g.periodYm = :ym",
                        SavingGoal.class)
                .setParameter("userId", userId)
                .setParameter("ym", periodYm)
                .getResultList();
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    public SavingGoal save(SavingGoal entity) {
        if (entity.getId() == null) {
            em.persist(entity);
            return entity;
        } else {
            return em.merge(entity);
        }
    }
}
