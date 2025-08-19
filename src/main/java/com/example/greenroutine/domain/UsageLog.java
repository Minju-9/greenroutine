package com.example.greenroutine.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "usage_log", indexes = {
        @Index(name = "idx_usage_log_user_ts", columnList = "user_id, ts")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsageLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "ts", nullable = false)
    private LocalDateTime ts;      //30초 폴링

    @Column(name = "elec", nullable = false)
    private Integer elec;          // 전기

    @Column(name = "gas", nullable = false)
    private Integer gas;           // 가스
}
