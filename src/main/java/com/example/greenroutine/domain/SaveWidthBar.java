package com.example.greenroutine.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "save_width_bar",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_swb_user_ym_cat", columnNames = {"user_id", "ym", "category"})
        },
        indexes = {
                @Index(name = "idx_swb_user_ym", columnList = "user_id, ym"),
                @Index(name = "idx_swb_user_ym_cat", columnList = "user_id, ym, category")
        }
)
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaveWidthBar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "category", nullable = false, length = 20)
    private String category;

    @Column(name = "target_amount", nullable = false)
    private Long targetAmount;

    @Column(name = "used_amount", nullable = false)
    private Long usedAmount;

    @Column(name = "ym", nullable = false, length = 7)
    private String ym;

    @PrePersist
    public void prePersist() {
        if (targetAmount == null) targetAmount = 0L;
        if (usedAmount == null) usedAmount = 0L;
    }

    public void setTarget(Long won) {
        this.targetAmount = (won == null ? 0L : Math.max(0L, won));
    }

    public void setUsed(Long won) {
        this.usedAmount = (won == null ? 0L : Math.max(0L, won));
    }
}
