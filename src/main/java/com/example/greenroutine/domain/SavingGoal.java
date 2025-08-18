package com.example.greenroutine.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavingGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String periodYm;
    private Long savingGoalWon;

    public SavingGoal(Long userId, String periodYm, Long savingGoalWon) {
        this.userId = userId;
        this.periodYm = periodYm;
        this.savingGoalWon = savingGoalWon;
    }
}