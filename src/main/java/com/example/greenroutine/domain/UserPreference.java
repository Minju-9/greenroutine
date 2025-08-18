package com.example.greenroutine.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "user_preference",
        uniqueConstraints = @UniqueConstraint(name = "uk_user_preference_user", columnNames = "user_id")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPreference {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    // 원룸/오피스텔/아파트/빌라/다주택
    @Column(name = "housing_type", length = 20)
    private String housingType;

    // 이중문 있음/없음
    @Column(name = "has_double_door")
    private Boolean hasDoubleDoor;

    // 단창/이중창/로이/단열
    @Column(name = "window_type", length = 20)
    private String windowType;
}
