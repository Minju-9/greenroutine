package com.example.greenroutine.api.dto.dashboard;

import lombok.*;

import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class DailyLineResponse {
    private String date;
    private List<String> labels;
    private List<Integer> elec;
    private List<Integer> gas;
}
