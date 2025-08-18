package com.example.greenroutine.api.controller;

import com.example.greenroutine.api.dto.tip.SavePreferenceRequest;
import com.example.greenroutine.service.TipService;
import jakarta.validation.Valid;
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
}
