package com.example.greenroutine.repository;

import com.example.greenroutine.domain.ThisMonthGoal;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ThisMonthGoalRepository {
    @PersistenceContext
    private EntityManager em;

    public ThisMonthGoal save(ThisMonthGoal e) {
        if (e.getId() == null) {
            em.persist(e);
            return e;
        }
        return em.merge(e);
    }

    public Optional<ThisMonthGoal> findByUserIdAndPeriodYm(Long userId, String periodYm) {
        List<ThisMonthGoal> list = em.createQuery(
                        "SELECT t FROM ThisMonthGoal t WHERE t.userId = :uid AND t.periodYm = :ym",
                        ThisMonthGoal.class
                )
                .setParameter("uid", userId)
                .setParameter("ym", periodYm)
                .setMaxResults(1)
                .getResultList();
        return list.stream().findFirst();
    }
}
