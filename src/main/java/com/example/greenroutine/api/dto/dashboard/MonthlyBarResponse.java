package com.example.greenroutine.api.dto.dashboard;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class MonthlyBarResponse {
    private String periodYm;
    private List<CumBar> bars;
    @Getter
    @Setter
    @NoArgsConstructor
    @Builder
    @AllArgsConstructor
    public static class CumBar{
        private int day;
        private int elecCum;
        private int gasCum;
        private int totalCum;
    }
}
