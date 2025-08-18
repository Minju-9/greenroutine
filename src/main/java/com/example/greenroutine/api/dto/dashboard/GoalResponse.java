package com.example.greenroutine.api.dto.dashboard;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class GoalResponse {
    private String periodYm;
    private int savingGoalWon;
    private int baselineTotalWon;
    private int currentTotalWon;
    private double savingPercent;
}
