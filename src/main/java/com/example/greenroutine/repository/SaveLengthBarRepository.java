package com.example.greenroutine.repository;

import com.example.greenroutine.domain.SaveLengthBar;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class SaveLengthBarRepository {
    @PersistenceContext
    private EntityManager em;

    public SaveLengthBar save(SaveLengthBar e) {
        if (e.getId() == null) {
            em.persist(e);
            return e;
        }
        return em.merge(e);
    }

    public Optional<SaveLengthBar> findById(Long id) {
        return Optional.ofNullable(em.find(SaveLengthBar.class, id));
    }

    public void deleteById(Long id) {
        SaveLengthBar found = em.find(SaveLengthBar.class, id);
        if (found != null) em.remove(found);
    }

    public Optional<SaveLengthBar> findByUserIdAndYm(Long userId, String ym) {
        List<SaveLengthBar> list = em.createQuery(
                        "SELECT s FROM SaveLengthBar s " +
                                "WHERE s.userId = :userId AND s.ym = :ym",
                        SaveLengthBar.class)
                .setParameter("userId", userId)
                .setParameter("ym", ym)
                .setMaxResults(1)
                .getResultList();
        return list.stream().findFirst();
    }

    public List<SaveLengthBar> findByAreaAndYm(String area, String ym) {
        return em.createQuery(
                        "SELECT s FROM SaveLengthBar s " +
                                "WHERE s.area = :area AND s.ym = :ym",
                        SaveLengthBar.class)
                .setParameter("area", area)
                .setParameter("ym", ym)
                .getResultList();
    }

    public boolean existsByUserIdAndYm(Long userId, String ym) {
        Long cnt = em.createQuery(
                        "SELECT COUNT(s) FROM SaveLengthBar s " +
                                "WHERE s.userId = :userId AND s.ym = :ym",
                        Long.class)
                .setParameter("userId", userId)
                .setParameter("ym", ym)
                .getSingleResult();
        return cnt != null && cnt > 0;
    }
}
