package com.example.greenroutine.api.dto.dashboard;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class GoalRequest {
    @NotNull
    @Min(1)
    private Long userId;
    @NotBlank
    @Pattern(regexp = "\\d{4}-\\d{2}", message = "YYYY-MM 형식")
    private String periodYm;

    @Min(0)
    private int savingGoalWon;
}
