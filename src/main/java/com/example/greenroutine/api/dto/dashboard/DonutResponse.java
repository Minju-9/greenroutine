package com.example.greenroutine.api.dto.dashboard;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class DonutResponse {
    private String periodYm;
    private int elec;
    private int gas;
    private int total;
    private double elecRatio;
    private double gasRatio;
}
