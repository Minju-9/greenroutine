package com.example.greenroutine.service;

import com.example.greenroutine.api.dto.dashboard.DonutResponse;
import com.example.greenroutine.api.dto.dashboard.GoalResponse;
import com.example.greenroutine.api.dto.dashboard.MonthlyBarResponse;
import com.example.greenroutine.domain.MonthlyCharge;
import com.example.greenroutine.domain.SavingGoal;
import com.example.greenroutine.repository.MonthlyChargeRepository;
import com.example.greenroutine.repository.SavingGoalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {
    private final MonthlyChargeRepository monthlyChargeRepository;
    private final SavingGoalRepository savingGoalRepository;

    // 전기요금 수정(원/kWh)
    private static final int ELEC_KWH = 106;
    //도시가스 요금 수정 (원/㎥)
    private static final int GAS_M3 = 960;

    // 도넛 데이터
    public DonutResponse getDonut(Long userId, String periodYm) {
        List<MonthlyCharge> dailies =
                monthlyChargeRepository.findAllByUser_IdAndYmAndDayIsNotNullOrderByDay(userId, periodYm);

        int elecSum = 0, gasSum = 0;
        for (MonthlyCharge d : dailies) {
            elecSum += safe(d.getElec())* ELEC_KWH;
            gasSum  += safe(d.getGas())* GAS_M3;;
        }

        if (dailies.isEmpty()) {
            Optional<MonthlyCharge> monthly = monthlyChargeRepository.findByUser_IdAndYmAndDayIsNull(userId, periodYm);
            if (monthly.isPresent()) {
                MonthlyCharge m = monthly.get();
                elecSum = safe(m.getElec()) * ELEC_KWH;;
                gasSum  = safe(m.getGas())* GAS_M3;;
            }
        }

        int total = elecSum + gasSum;
        double elecRatio = (total == 0) ? 0.0 : (elecSum * 1.0) / total;
        double gasRatio  = (total == 0) ? 0.0 : (gasSum  * 1.0) / total;

        return DonutResponse.builder()
                .periodYm(periodYm)
                .elec(elecSum)
                .gas(gasSum)
                .total(total)
                .elecRatio(elecRatio)
                .gasRatio(gasRatio)
                .build();
    }

    // 월 막대 그래프
    public MonthlyBarResponse getMonthlyBar(Long userId, String periodYm) {
        YearMonth ym = YearMonth.parse(periodYm);
        int daysInMonth = ym.lengthOfMonth();

        List<MonthlyCharge> dailies =
                monthlyChargeRepository.findAllByUser_IdAndYmAndDayIsNotNullOrderByDay(userId, periodYm);

        int[] elecDay = new int[daysInMonth + 1];
        int[] gasDay  = new int[daysInMonth + 1];

        for (MonthlyCharge d : dailies) {
            Integer day = d.getDay();
            if (day != null && day >= 1 && day <= daysInMonth) {
                elecDay[day] += safe(d.getElec()) * ELEC_KWH;
                gasDay[day]  += safe(d.getGas())* GAS_M3;;
            }
        }

        List<MonthlyBarResponse.CumBar> bars = new ArrayList<>();
        int elecCum = 0, gasCum = 0;
        for (int day = 1; day <= daysInMonth; day++) {
            elecCum += elecDay[day];
            gasCum  += gasDay[day];
            bars.add(MonthlyBarResponse.CumBar.builder()
                    .day(day)
                    .elecCum(elecCum)
                    .gasCum(gasCum)
                    .totalCum(elecCum + gasCum)
                    .build());
        }

        return MonthlyBarResponse.builder()
                .periodYm(periodYm)
                .bars(bars)
                .build();
    }

    // 절약 목표 저장
    @Transactional
    public GoalResponse setSavingGoal(Long userId, String periodYm, int savingGoalWon) {
        SavingGoal goal = savingGoalRepository
                .findByUserIdAndPeriodYm(userId, periodYm)
                .orElseGet(() -> new SavingGoal(userId, periodYm, 0L));

        goal.setSavingGoalWon((long) Math.max(0, savingGoalWon));
        savingGoalRepository.save(goal);

        int baseline = getPrevMonthTotal(userId, periodYm);
        int current  = getMonthTotal(userId, periodYm);

        double savingPercent = 0.0;
        if (savingGoalWon > 0) {
            int saved = Math.max(0, baseline - current);
            savingPercent = (saved * 100.0) / savingGoalWon;
            if (savingPercent > 100.0) savingPercent = 100.0;
        }

        return GoalResponse.builder()
                .periodYm(periodYm)
                .savingGoalWon(savingGoalWon)
                .baselineTotalWon(baseline)
                .currentTotalWon(current)
                .savingPercent(savingPercent)
                .build();
    }

    // 절약 목표
    public GoalResponse getSavingGoal(Long userId, String periodYm) {
        SavingGoal goal = savingGoalRepository
                .findByUserIdAndPeriodYm(userId, periodYm)
                .orElseGet(() -> new SavingGoal(userId, periodYm, 0L));

        int savingGoalWon = (int) (goal.getSavingGoalWon() == null ? 0L : goal.getSavingGoalWon());
        int baseline = getPrevMonthTotal(userId, periodYm);
        int current  = getMonthTotal(userId, periodYm);

        double savingPercent = 0.0;
        if (savingGoalWon > 0) {
            int saved = Math.max(0, baseline - current);
            savingPercent = (saved * 100.0) / savingGoalWon;
            if (savingPercent > 100.0) savingPercent = 100.0;
        }

        return GoalResponse.builder()
                .periodYm(periodYm)
                .savingGoalWon(savingGoalWon)
                .baselineTotalWon(baseline)
                .currentTotalWon(current)
                .savingPercent(savingPercent)
                .build();
    }

    private int getMonthTotal(Long userId, String ymStr) {
        List<MonthlyCharge> dailies =
                monthlyChargeRepository.findAllByUser_IdAndYmAndDayIsNotNullOrderByDay(userId, ymStr);

        int sum = 0;
        for (MonthlyCharge d : dailies) {
            sum += safe(d.getElec()) * ELEC_KWH;
            sum += safe(d.getGas()) * GAS_M3;
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

    private int safe(Integer v) { return (v == null) ? 0 : v; }

}