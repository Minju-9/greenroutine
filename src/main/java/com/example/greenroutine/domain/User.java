package com.example.greenroutine.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "gr_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // PK (자동 증가 ID)

    @Column(nullable = false)
    private String nickname;

    private String region;
    // 지역 정보 (이게 화면에는 없는데 안서동,신부동 필요해 보여서 한다면 이 변수명을 쓸 것,,,,, )

    private String profileImageUrl;
    // 프로필 이미지 URL 넣을 것
}
