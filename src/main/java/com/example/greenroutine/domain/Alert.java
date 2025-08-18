package com.example.greenroutine.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "alert",
        indexes = {
                @Index(name = "idx_alert_user",     columnList = "user_id"),
                @Index(name = "idx_alert_created",  columnList = "createdAt"),
                @Index(name = "idx_alert_category", columnList = "category"),
                @Index(name = "idx_alert_level",    columnList = "level"),
                @Index(name = "idx_alert_read",     columnList = "isRead")
        }
)
public class Alert {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String category;
    // 알림 카테고리 ("BILL" / "SAVING_TIP" / "SYSTEM") (순서대로 관리비 알림, 절약 팁 알림, 시스템 자체 알림 뜻함요)

    @Column(nullable = false)
    private String level;
    // 알림 단계 ("긍정" / "주의" / "팁" / "알림")

    @Column(nullable = false)
    private String titleExp;
    // 상단바에 설명 (이번 달에 관리비 @@% 줄었다 설명)


    @Column(nullable = false)
    private LocalDateTime createdAt;
    // 생성 시각 (필요할진 모르겠는데 2일전 3일전 들어있어서 적긴 했어)

    @Column(nullable = false)
    private boolean yesRead;
    // 읽음 여부 (미확인만)
}
