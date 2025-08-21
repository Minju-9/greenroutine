package com.example.greenroutine.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "save_length_bar",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_slb_user_ym", columnNames = {"user_id", "ym"})
        },
        indexes = {
                @Index(name = "idx_slb_user_ym", columnList = "user_id, ym"),
                @Index(name = "idx_slb_area_ym", columnList = "area, ym")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaveLengthBar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "area", nullable = false, length = 50)
    private String area;

    @Column(name = "ym", nullable = false, length = 7)
    private String ym;

    @Column(name = "success_count", nullable = false)
    private Integer successCount;

    @Column(name = "total_count", nullable = false)
    private Integer totalCount;

    @PrePersist
    public void prePersist() {
        if (successCount == null) successCount = 0;
        if (totalCount == null) totalCount = 0;
        if (area == null) area = "";
    }

    @Transient
    public double getSuccessRate() {
        if (totalCount == null || totalCount <= 0) return 0.0;
        double v = (successCount == null ? 0 : successCount) * 100.0 / totalCount;
        return Math.round(v * 10.0) / 10.0;
    }

}
