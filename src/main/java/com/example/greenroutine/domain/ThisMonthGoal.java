package com.example.greenroutine.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "this_month_goal",
        uniqueConstraints = @UniqueConstraint(name = "uk_tmg_user_ym", columnNames = {"user_id", "period_ym"}),
        indexes = {
                @Index(name = "idx_tmg_user_ym", columnList = "user_id, period_ym")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ThisMonthGoal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "period_ym", nullable = false, length = 7)
    private String periodYm;

    @Column(name = "goal_won", nullable = false)
    private Integer goalWon;

    @PrePersist
    public void prePersist() {
        if (goalWon == null) goalWon = 0;
    }
}
