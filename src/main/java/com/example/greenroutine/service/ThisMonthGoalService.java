package com.example.greenroutine.service;

import com.example.greenroutine.api.dto.saveTop.ThisMonthGoalDto;
import com.example.greenroutine.domain.MonthlyCharge;
import com.example.greenroutine.domain.ThisMonthGoal;
import com.example.greenroutine.repository.MonthlyChargeRepository;
import com.example.greenroutine.repository.ThisMonthGoalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ThisMonthGoalService {
    private static final int GOAL_MAX = 200_000;
    private static final int STEP     = 1_000;

    private static final int ELEC_KWH = 106;
    private static final int GAS_M3   = 960;

    private final ThisMonthGoalRepository repo;
    private final MonthlyChargeRepository monthlyChargeRepository;

    //조회
    public ThisMonthGoalDto get(Long userId, String ym) {
        String month = normalizeYm(ym);

        int goal = repo.findByUserIdAndPeriodYm(userId, month)
                .map(e -> e.getGoalWon() == null ? 0 : e.getGoalWon())
                .orElse(0);

        return build(userId, month, goal);
    }

    //저장
    @Transactional
    public ThisMonthGoalDto set(Long userId, String ym, int goalInput) {
        String month = normalizeYm(ym);

        // 1,000원 단위
        int goal = clampToStep(goalInput, STEP, 0, GOAL_MAX);

        ThisMonthGoal entity = repo.findByUserIdAndPeriodYm(userId, month)
                .orElseGet(() -> ThisMonthGoal.builder()
                        .userId(userId)
                        .periodYm(month)
                        .goalWon(0)
                        .build());

        entity.setGoalWon(goal);
        repo.save(entity);

        return build(userId, month, goal);
    }

    //초기화
    @Transactional
    public ThisMonthGoalDto reset(Long userId, String ym) {
        String month = normalizeYm(ym);

        ThisMonthGoal entity = repo.findByUserIdAndPeriodYm(userId, month)
                .orElseGet(() -> ThisMonthGoal.builder()
                        .userId(userId)
                        .periodYm(month)
                        .goalWon(0)
                        .build());

        entity.setGoalWon(0);
        repo.save(entity);

        return build(userId, month, 0);
    }

    private ThisMonthGoalDto build(Long userId, String month, int goal) {
        int baseline = 0;

        int current  = getMonthTotal(userId, month);
        int remaining = Math.max(0, goal - current);
        int saved     = goal - remaining;

        double percent = 0.0;
        if (goal > 0) {
            percent = (saved * 100.0) / goal;
            if (percent > 100.0) percent = 100.0;
            percent = Math.round(percent * 10.0) / 10.0;
        }

        return ThisMonthGoalDto.builder()
                .periodYm(month)
                .goalWon(goal)
                .baselineTotalWon(baseline)
                .currentTotalWon(current)
                .savedWon(saved)
                .remainingWon(remaining)
                .savingPercent(percent)
                .build();
    }

    //이번달총액
    private int getMonthTotal(Long userId, String ymStr) {
        List<MonthlyCharge> dailies =
                monthlyChargeRepository.findAllByUser_IdAndYmAndDayIsNotNullOrderByDay(userId, ymStr);

        int sum = 0;
        for (MonthlyCharge d : dailies) {
            sum += safe(d.getElec()) * ELEC_KWH;
            sum += safe(d.getGas())  * GAS_M3;
        }
        if (sum > 0) return sum;

        return monthlyChargeRepository.findByUser_IdAndYmAndDayIsNull(userId, ymStr)
                .map(m -> safe(m.getElec()) * ELEC_KWH + safe(m.getGas()) * GAS_M3)
                .orElse(0);
    }

    private int getPrevMonthTotal(Long userId, String ymStr) {
        try {
            YearMonth ym = YearMonth.parse(ymStr);
            YearMonth prev = ym.minusMonths(1);
            return getMonthTotal(userId, prev.toString());
        } catch (Exception e) {
            return 0;
        }
    }

    private static int safe(Integer v) { return v == null ? 0 : v; }
    private static int safe(int v) { return v; }

    private static String normalizeYm(String ym) {
        return (ym == null || ym.isBlank()) ? YearMonth.now().toString() : ym;
    }

    private static int clampToStep(int input, int step, int min, int max) {
        if (input < min) return min;
        if (input > max) return max;
        return (input / step) * step;
    }
}
