package com.example.greenroutine.api.dto.tip;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public class TipsResponse {
    private Map<String, List<String>> tips;
}
