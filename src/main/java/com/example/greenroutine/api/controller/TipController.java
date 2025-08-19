package com.example.greenroutine.api.controller;

import com.example.greenroutine.api.dto.dashboard.DailyLineResponse;
import com.example.greenroutine.api.dto.tip.SavePreferenceRequest;
import com.example.greenroutine.service.MetricsService;
import com.example.greenroutine.service.TipService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class TipController {
    private final TipService tipService;
    private final MetricsService metricsService;

    @PostMapping("/preference")
    public ResponseEntity<Void> savePreference(@RequestBody @Valid SavePreferenceRequest req) {
        tipService.savePreference(req);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/tips")
    public ResponseEntity<Map<String, List<String>>> getTips(@RequestParam Long userId,
                                                             @RequestParam(defaultValue = "2") int elecCount,
                                                             @RequestParam(defaultValue = "2") int gasCount) {
        return ResponseEntity.ok(tipService.getTips(userId, elecCount, gasCount));
    }
    @GetMapping("/daily-line")
    public ResponseEntity<DailyLineResponse> dailyLine(
            @RequestParam @NotNull @Min(1) Long userId,
            @RequestParam("date")
            @NotBlank @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "YYYY-MM-DD 형식")
            String date
    ) {
        return ResponseEntity.ok(metricsService.getDailyLine(userId, date));
    }
    @PostMapping("/preference/tips")
    public ResponseEntity<Map<String, List<String>>> savePrefAndGetTips(
            @RequestBody @Valid SavePreferenceRequest req,
            @RequestParam(defaultValue = "2") int elecCount,
            @RequestParam(defaultValue = "2") int gasCount
    ){
        tipService.savePreference(req);
        return ResponseEntity.ok(tipService.getTips(req.getUserId(), elecCount,gasCount));
    }
}
