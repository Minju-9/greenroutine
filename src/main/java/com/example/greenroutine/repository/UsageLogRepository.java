package com.example.greenroutine.repository;

import com.example.greenroutine.domain.UsageLog;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class UsageLogRepository {
    @PersistenceContext
    private final EntityManager em;

    public List<UsageLog> findByUserAndRange(Long userId, LocalDateTime start, LocalDateTime end) {
        return em.createQuery("""
            select u from UsageLog u
            where u.userId = :uid and u.ts >= :start and u.ts < :end
            order by u.ts asc
        """, UsageLog.class)
                .setParameter("uid", userId)
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultList();
    }
    public UsageLog save(UsageLog e) {
        if (e.getId() == null) { em.persist(e); return e; }
        return em.merge(e);
    }
}
