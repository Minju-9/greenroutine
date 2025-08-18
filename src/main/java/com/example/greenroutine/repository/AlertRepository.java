package com.example.greenroutine.repository;

import com.example.greenroutine.domain.Alert;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(readOnly = true)

public class AlertRepository {
    @PersistenceContext
    private EntityManager em;

    @Transactional
    public Alert save(Alert alert) {
        if (alert.getId() == null) {
            em.persist(alert);
            return alert;
        }
        return em.merge(alert);
    }

    public Page<Alert> allList(Long userId, Pageable pageable) {
        Long total = em.createQuery(
                        "select count(a) from Alert a where a.user.id = :uid", Long.class)
                .setParameter("uid", userId)
                .getSingleResult();

        List<Alert> list = em.createQuery(
                        "select a from Alert a " +
                                "where a.user.id = :uid " +
                                "order by a.createdAt desc",
                        Alert.class)
                .setParameter("uid", userId)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        return new PageImpl<>(list, pageable, total);
    }

    public long countUnread(Long userId) {
        return em.createQuery(
                        "select count(a) from Alert a " +
                                "where a.user.id = :uid and a.yesRead = false",
                        Long.class)
                .setParameter("uid", userId)
                .getSingleResult();
    }


}
