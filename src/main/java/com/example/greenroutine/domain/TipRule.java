package com.example.greenroutine.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tip_rule",
        indexes = @Index(name = "idx_tip_rule_priority", columnList = "priority"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipRule {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "housing_type", length = 20)
    private String housingType;        // 원룸/오피스텔/아파트/빌라/다주택/NULL

    @Column(name = "has_double_door")
    private Boolean hasDoubleDoor;     // TRUE/FALSE/NULL

    @Column(name = "window_type", length = 20)
    private String windowType;         // 단창/이중창/로이/단열/NULL

    // 어느 카드 (전기/가스)
    @Column(name = "utility", nullable = false, length = 10)
    private String utility;

    // 낮을수록 먼저 노출
    @Column(name = "priority", nullable = false)
    private Integer priority;

    @Column(name = "message", nullable = false, length = 1000)
    private String message;


}
