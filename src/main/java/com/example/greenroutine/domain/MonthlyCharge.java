package com.example.greenroutine.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Table(
        name = "monthly_charge",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "ym", "day_value"})
)
public class MonthlyCharge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, length = 7)
    private String ym;

    @Column(name = "day")
    private Integer day;

    @Column(nullable = false)
    private int elec;

    @Column(nullable = false)
    private int gas;

    @Transient
    public int getTotal() {
        return Math.max(0, elec) + Math.max(0, gas);
    }

    @Transient
    public double getElecRatio() {
        int total = getTotal();
        return total == 0 ? 0.0 : (double) Math.max(0, elec) / total;
    }

    @Transient
    public double getGasRatio() {
        int total = getTotal();
        return total == 0 ? 0.0 : (double) Math.max(0, gas) / total;
    }
}