package com.example.greenroutine.repository;

import com.example.greenroutine.domain.TipRule;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TipRuleRepository {
    @PersistenceContext
    private EntityManager em;

    // 전체 우선순위
    public List<TipRule> findAllOrderByPriorityAsc() {
        return em.createQuery(
                "select r from TipRule r order by r.priority asc", TipRule.class
        ).getResultList();
    }

    // ELEC/GAS만, 우선순위
    public List<TipRule> findByUtilityOrderByPriorityAsc(String utility) {
        return em.createQuery(
                "select r from TipRule r where r.utility = :u order by r.priority asc", TipRule.class
        ).setParameter("u", utility).getResultList();
    }


    public List<TipRule> findTopNByUtilityOrderByPriorityAsc(String utility, int limit) {
        return em.createQuery(
                        "select r from TipRule r where r.utility = :u order by r.priority asc", TipRule.class
                ).setParameter("u", utility)
                .setMaxResults(limit)
                .getResultList();
    }

    public TipRule save(TipRule entity) {
        if (entity.getId() == null) {
            em.persist(entity);
            return entity;
        }
        return em.merge(entity);
    }
}
