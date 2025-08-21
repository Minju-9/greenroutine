package com.example.greenroutine.api.dto.saveTop;

import com.example.greenroutine.domain.SaveLengthBar;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaveLengthBarDto {
    private Long id;
    private Long userId;
    private String area;
    private String ym;
    private Integer success;
    private Integer total;
    private double rate;

    public static SaveLengthBarDto of(SaveLengthBar e) {
        return SaveLengthBarDto.builder()
                .id(e.getId())
                .userId(e.getUserId())
                .area(e.getArea())
                .ym(e.getYm())
                .success(e.getSuccessCount())
                .total(e.getTotalCount())
                .rate(e.getSuccessRate())
                .build();
    }
}
