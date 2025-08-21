package com.example.greenroutine.repository;

import com.example.greenroutine.domain.SaveWidthBar;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class SaveWidthBarRepository {
    @PersistenceContext
    private EntityManager em;

    public SaveWidthBar save(SaveWidthBar e) {
        if (e.getId() == null) {
            em.persist(e);
            return e;
        }
        return em.merge(e);
    }

    public Optional<SaveWidthBar> findById(Long id) {
        return Optional.ofNullable(em.find(SaveWidthBar.class, id));
    }

    public void deleteById(Long id) {
        SaveWidthBar found = em.find(SaveWidthBar.class, id);
        if (found != null) em.remove(found);
    }

    public void delete(SaveWidthBar e) {
        em.remove(em.contains(e) ? e : em.merge(e));
    }

    public List<SaveWidthBar> findByUserIdAndYmOrderByCategoryAsc(Long userId, String ym) {
        return em.createQuery(
                        "SELECT s FROM SaveWidthBar s " +
                                "WHERE s.userId = :userId AND s.ym = :ym " +
                                "ORDER BY s.category ASC", SaveWidthBar.class)
                .setParameter("userId", userId)
                .setParameter("ym", ym)
                .getResultList();
    }

    public Optional<SaveWidthBar> findByUserIdAndYmAndCategory(Long userId, String ym, String category) {
        List<SaveWidthBar> list = em.createQuery(
                        "SELECT s FROM SaveWidthBar s " +
                                "WHERE s.userId = :userId AND s.ym = :ym AND s.category = :category",
                        SaveWidthBar.class)
                .setParameter("userId", userId)
                .setParameter("ym", ym)
                .setParameter("category", category)
                .setMaxResults(1)
                .getResultList();
        return list.stream().findFirst();
    }

    public boolean existsByUserIdAndYmAndCategory(Long userId, String ym, String category) {
        Long cnt = em.createQuery(
                        "SELECT COUNT(s) FROM SaveWidthBar s " +
                                "WHERE s.userId = :userId AND s.ym = :ym AND s.category = :category",
                        Long.class)
                .setParameter("userId", userId)
                .setParameter("ym", ym)
                .setParameter("category", category)
                .getSingleResult();
        return cnt != null && cnt > 0;
    }
}
