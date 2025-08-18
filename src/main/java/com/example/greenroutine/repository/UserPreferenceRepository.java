package com.example.greenroutine.repository;

import com.example.greenroutine.domain.UserPreference;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserPreferenceRepository {
    @PersistenceContext
    private final EntityManager em;
    public Optional<UserPreference> findByUserId(Long userId){
        List<UserPreference> list =em.createQuery( "select p from UserPreference p where p.userId = :uid", UserPreference.class)
                .setParameter("uid", userId)
                .getResultList();
        return list.isEmpty() ? Optional.empty(): Optional.of(list.get(0));
    }
    public UserPreference save(UserPreference entity){
        if (entity.getId() == null){
            em.persist(entity);
            return entity;
        }
        return em.merge(entity);
    }
}
