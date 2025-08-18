package com.example.greenroutine.repository;

import com.example.greenroutine.domain.MonthlyCharge;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor

public class MonthlyChargeRepository {
    @PersistenceContext
    private final EntityManager em;

    public Optional<MonthlyCharge> findByUser_IdAndYmAndDayIsNull(Long userId, String ym) {
        List<MonthlyCharge> list = em.createQuery(
                        "select m from MonthlyCharge m " +
                                "where m.user.id = :userId and m.ym = :ym and m.day is null",
                        MonthlyCharge.class)
                .setParameter("userId", userId)
                .setParameter("ym", ym)
                .getResultList();
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    public List<MonthlyCharge> findAllByUser_IdAndYmAndDayIsNotNullOrderByDay(Long userId, String ym) {
        return em.createQuery(
                        "select m from MonthlyCharge m " +
                                "where m.user.id = :userId and m.ym = :ym and m.day is not null " +
                                "order by m.day asc",
                        MonthlyCharge.class)
                .setParameter("userId", userId)
                .setParameter("ym", ym)
                .getResultList();
    }

    public List<MonthlyCharge> findAllByUser_IdAndYm(Long userId, String ym) {
        return em.createQuery(
                        "select m from MonthlyCharge m " +
                                "where m.user.id = :userId and m.ym = :ym",
                        MonthlyCharge.class)
                .setParameter("userId", userId)
                .setParameter("ym", ym)
                .getResultList();
    }

    public MonthlyCharge save(MonthlyCharge entity) {
        if (entity.getId() == null) {
            em.persist(entity);
            return entity;
        } else {
            return em.merge(entity);
        }
    }

    public void delete(MonthlyCharge entity) {
        em.remove(em.contains(entity) ? entity : em.merge(entity));
    }
}
