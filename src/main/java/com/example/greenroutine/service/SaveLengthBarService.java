package com.example.greenroutine.service;

import com.example.greenroutine.domain.SaveLengthBar;
import com.example.greenroutine.repository.SaveLengthBarRepository;
import lombok.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SaveLengthBarService {
    private final SaveLengthBarRepository repo;


    public RankingCardDto getRankingCardByYm(Long userId, String area, String ym, int window) {
        String month = normalizeYm(ym);

        SaveLengthBar mine = repo.findByUserIdAndYm(userId, month).orElse(null);
        int mySuccess = (mine != null && mine.getSuccessCount() != null) ? mine.getSuccessCount() : 0;
        int myTotal   = (mine != null && mine.getTotalCount()   != null) ? mine.getTotalCount()   : 0;
        if (myTotal <= 0) myTotal = 1; // 분모 0 방지
        double myRate = pct(mySuccess, myTotal); // 소수 1자리

        List<SaveLengthBar> areaRows = repo.findByAreaAndYm(area, month);
        double areaAvg = 0.0;
        if (!areaRows.isEmpty()) {
            DoubleSummaryStatistics stats = areaRows.stream()
                    .filter(Objects::nonNull)
                    .mapToDouble(e -> pct(nvl(e.getSuccessCount()), Math.max(1, nvl(e.getTotalCount()))))
                    .summaryStatistics();
            areaAvg = stats.getAverage();
        }

        int topPercent = calcTopPercent(myRate, areaRows);

        return RankingCardDto.builder()
                .userId(userId)
                .area(area)
                .ym(month)
                .windowSize(window)
                .mySuccessCount(mySuccess)
                .myTotalCount(myTotal)
                .myRate(round1(myRate))
                .areaAvgRate(round1(areaAvg))
                .topPercent(topPercent)
                .build();
    }

    public RankingCardDto getRankingCard(Long userId, String area, int windowSize) {
        return getRankingCardByYm(userId, area, null, windowSize);
    }

    private static String normalizeYm(String ym) {
        return (ym == null || ym.isBlank()) ? YearMonth.now().toString() : ym;
    }

    private static int calcTopPercent(double myRate, List<SaveLengthBar> areaRows) {
        if (areaRows == null || areaRows.isEmpty()) return 0;
        long belowOrEqual = areaRows.stream()
                .mapToDouble(e -> pct(nvl(e.getSuccessCount()), Math.max(1, nvl(e.getTotalCount()))))
                .filter(r -> r <= myRate)
                .count();
        double percentile = (belowOrEqual * 100.0) / areaRows.size();
        double top = Math.max(0.0, 100.0 - percentile);
        return (int) Math.round(top);
    }

    private static double pct(int success, int total) {
        if (total <= 0) return 0.0;
        double v = success * 100.0 / total;
        return round1(v);
    }

    private static double round1(double v) {
        return BigDecimal.valueOf(v).setScale(1, RoundingMode.HALF_UP).doubleValue();
    }

    private static int nvl(Integer v) { return v == null ? 0 : v; }


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RankingCardDto {
        private Long userId;
        private String area;
        private String ym;
        private int windowSize;
        private int mySuccessCount;
        private int myTotalCount;
        private double myRate;
        private double areaAvgRate;
        private int topPercent;
    }
}
