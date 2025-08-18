package com.example.greenroutine.api.dto.alert;

import com.example.greenroutine.domain.Alert;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

public class AlertDto {
    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @Builder
    public static class AlertEvent{
        @NotNull private Long userId;
        @NotBlank private String category;
        @NotBlank private String level;
        @NotBlank private String title;
        @Builder.Default
        private LocalDateTime at = LocalDateTime.now();
    }
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class CreateBillReq{
        @NotNull @Min(1) private Long userId;
        @NotBlank private String title;
        private LocalDateTime at;
    }
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class CreateAnomalyReq{
        @NotNull @Min(1) private Long userId;
        @NotBlank private String category;
        @NotBlank private String level;
        @NotBlank private String title;
        private LocalDateTime at;
    }
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class AlertRes{
        private Long id;
        private Long userId;
        private String category;
        private String level;
        private String title;
        private LocalDateTime createdAt;
        private boolean yesRead;

        public static AlertRes form(Alert a){
            return AlertRes.builder()
                    .id(a.getId())
                    .userId(a.getUser().getId())
                    .category(a.getCategory())
                    .level(a.getLevel())
                    .title(a.getTitleExp())
                    .createdAt(a.getCreatedAt())
                    .yesRead(a.isYesRead())
                    .build();
        }
    }

}
