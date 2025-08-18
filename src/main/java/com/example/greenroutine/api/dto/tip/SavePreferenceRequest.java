package com.example.greenroutine.api.dto.tip;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SavePreferenceRequest {
    @NotNull private Long userId;

    private String housingType;   //원룸/오피스텔/아파트/빌라/다주택
    private Boolean hasDoubleDoor;
    private String windowType;     // 단창/이중창/로이.단열
}
