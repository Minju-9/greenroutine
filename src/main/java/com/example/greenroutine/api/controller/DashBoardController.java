package com.example.greenroutine.api.controller;

import com.example.greenroutine.api.dto.dashboard.DonutResponse;
import com.example.greenroutine.api.dto.dashboard.GoalRequest;
import com.example.greenroutine.api.dto.dashboard.GoalResponse;
import com.example.greenroutine.api.dto.dashboard.MonthlyBarResponse;
import com.example.greenroutine.service.DashboardService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Validated
public class DashBoardController {
    private final DashboardService dashboardService;

    @GetMapping("/donut")
    public ResponseEntity<DonutResponse> donut(
        @RequestParam @NotNull @Min(1) Long userId,
        @RequestParam("period")
        @NotBlank @Pattern(regexp = "\\d{4}-\\d{2}", message = "YYYY-MM 형식")
        String periodYm
    ){
        return ResponseEntity.ok(dashboardService.getDonut(userId,periodYm));
    }
    @GetMapping("/bars")
    public ResponseEntity<MonthlyBarResponse> bars(
            @RequestParam @NotNull @Min(1) Long userId,
            @RequestParam("period")
            @NotBlank @Pattern(regexp = "\\d{4}-\\d{2}", message = "YYYY-MM 형식")
            String periodYm
    ){
        return ResponseEntity.ok(dashboardService.getMonthlyBar(userId,periodYm));
    }
    @PostMapping("/goal")
    public ResponseEntity<GoalResponse> setGoal(@Valid @RequestBody GoalRequest req){
        return ResponseEntity.ok(dashboardService.setSavingGoal(req.getUserId(), req.getPeriodYm(), req.getSavingGoalWon())
        );
    }
    @GetMapping("/goal")
    public ResponseEntity<GoalResponse> getGoal(
            @RequestParam @NotNull @Min(1) Long userId,
            @RequestParam("period")
            @NotBlank @Pattern(regexp = "\\d{4}-\\d{2}", message = "YYYY-MM 형식")
            String periodYm
    ){
        return ResponseEntity.ok(dashboardService.getSavingGoal(userId, periodYm));
    }
}
