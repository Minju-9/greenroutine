package com.example.greenroutine.api.dto.saveTop;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ThisMonthGoalDto {
    @JsonProperty("ym")
    private String periodYm;
    private int goalWon;// 목표 금액(원) (0~200,000, 1,000단위)
    private int baselineTotalWon;
    private int currentTotalWon;
    private int savedWon;           // 이번 달 절감액
    private int remainingWon;       // 남은 금액
    private double savingPercent;   // 절감률
}
