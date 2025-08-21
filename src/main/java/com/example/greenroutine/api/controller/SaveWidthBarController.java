package com.example.greenroutine.api.controller;

import com.example.greenroutine.api.dto.saveTop.SaveWidthBarDto;
import com.example.greenroutine.api.dto.saveTop.ThisMonthGoalDto;
import com.example.greenroutine.service.SaveLengthBarService;
import com.example.greenroutine.service.SaveWidthBarService;
import com.example.greenroutine.service.ThisMonthGoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SaveWidthBarController {
    private final SaveWidthBarService widthService;
    private final SaveLengthBarService lengthService;
    private final ThisMonthGoalService monthGoalService;

    // 가로
    @GetMapping("/savebars")
    public List<SaveWidthBarDto> list(@RequestParam Long userId,
                                      @RequestParam(required = false) String ym) {
        return widthService.listByUserAndYm(userId, normalizeYm(ym));
    }

    @PostMapping("/savebars/used")
    public SaveWidthBarDto setUsed(@RequestParam Long userId,
                                   @RequestParam String category,
                                   @RequestParam Long used,
                                   @RequestParam(required = false) String ym) {
        return widthService.setUsed(userId, category, used, normalizeYm(ym));
    }

    @PostMapping("/savebars/target")
    public SaveWidthBarDto setTarget(@RequestParam Long userId,
                                     @RequestParam String category,
                                     @RequestParam Long target,
                                     @RequestParam(required = false) String ym) {
        return widthService.setTarget(userId, category, target, normalizeYm(ym));
    }

    // 세러
    @GetMapping("/ranking")
    public SaveLengthBarService.RankingCardDto ranking(@RequestParam Long userId,
                                                       @RequestParam String area,
                                                       @RequestParam(required = false) String ym,
                                                       @RequestParam(required = false, defaultValue = "100") int window) {
        return lengthService.getRankingCardByYm(userId, area, normalizeYm(ym), window);
    }

    private static String normalizeYm(String ym) {
        return (ym == null || ym.isBlank()) ? YearMonth.now().toString() : ym;
    }


    // 이번달 절약 목표
    @GetMapping("/month-goal")
    public ThisMonthGoalDto getMonthGoal(@RequestParam Long userId,
                                         @RequestParam(required = false) String ym) {
        return monthGoalService.get(userId, normalizeYm(ym));
    }

    @PostMapping("/month-goal")
    public ThisMonthGoalDto setMonthGoal(@RequestParam Long userId,
                                         @RequestParam(required = false) String ym,
                                         @RequestParam(name = "goalWon") int goalWon) {
        return monthGoalService.set(userId, normalizeYm(ym), goalWon);
    }

    // 초기화
    @PostMapping("/month-goal/reset")
    public ThisMonthGoalDto resetMonthGoal(@RequestParam Long userId,
                                           @RequestParam(required = false) String ym) {
        return monthGoalService.reset(userId, normalizeYm(ym));
    }
}
