package com.example.greenroutine.service;

import com.example.greenroutine.api.dto.saveTop.SaveWidthBarDto;
import com.example.greenroutine.domain.SaveWidthBar;
import com.example.greenroutine.repository.SaveWidthBarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SaveWidthBarService {
    private final SaveWidthBarRepository repo;

    private static String nowYm() {
        return YearMonth.now().toString();
    }

    private static String normalizeYm(String ym) {
        return (ym == null || ym.isBlank()) ? nowYm() : ym;
    }

    private static String normalizeCategory(String category) {
        return (category == null) ? "" : category.trim();
    }

    @Transactional
    public SaveWidthBarDto upsert(Long userId, String category, Long target, Long used, String ym) {
        final String month = normalizeYm(ym);
        final String cat   = normalizeCategory(category);

        SaveWidthBar swb = repo.findByUserIdAndYmAndCategory(userId, month, cat)
                .orElseGet(() -> SaveWidthBar.builder()
                        .userId(userId)
                        .category(cat)
                        .ym(month)
                        .targetAmount(0L)
                        .usedAmount(0L)
                        .build());

        if (target != null) swb.setTarget(target);
        if (used != null) swb.setUsed(used);

        return SaveWidthBarDto.of(repo.save(swb));
    }

    public List<SaveWidthBarDto> listByUserAndYm(Long userId, String ym) {
        final String month = normalizeYm(ym);
        return repo.findByUserIdAndYmOrderByCategoryAsc(userId, month)
                .stream()
                .map(SaveWidthBarDto::of)
                .collect(Collectors.toList());
    }

    public List<SaveWidthBarDto> listByUserThisMonth(Long userId) {
        return listByUserAndYm(userId, null);
    }

    public SaveWidthBarDto getOne(Long id) {
        return repo.findById(id).map(SaveWidthBarDto::of).orElse(null);
    }

    @Transactional
    public SaveWidthBarDto setUsed(Long userId, String category, Long used, String ym) {
        return upsert(userId, category, null, used, ym);
    }

    @Transactional
    public SaveWidthBarDto setTarget(Long userId, String category, Long target, String ym) {
        return upsert(userId, category, target, null, ym);
    }

    @Transactional
    public void delete(Long id) {
        repo.deleteById(id);
    }
}
