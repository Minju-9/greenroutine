package com.example.greenroutine.service;

import com.example.greenroutine.api.dto.tip.SavePreferenceRequest;
import com.example.greenroutine.domain.TipRule;
import com.example.greenroutine.domain.UserPreference;
import com.example.greenroutine.repository.TipRuleRepository;
import com.example.greenroutine.repository.UserPreferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TipService {
    private final UserPreferenceRepository userPreferenceRepository;
    private final TipRuleRepository tipRuleRepository;

    @Transactional
    public void savePreference(SavePreferenceRequest req) {
        UserPreference pref = userPreferenceRepository.findByUserId(req.getUserId())
                .orElse(UserPreference.builder().userId(req.getUserId()).build());


        if (req.getHousingType() != null) {
            pref.setHousingType(req.getHousingType());
        }
        if (req.getHasDoubleDoor() != null) {
            pref.setHasDoubleDoor(req.getHasDoubleDoor());
        }
        if (req.getWindowType() != null) {
            pref.setWindowType(req.getWindowType());
        }

        userPreferenceRepository.save(pref);
    }

    @Transactional(readOnly = true)
    public Map<String, List<String>> getTips(Long userId, int elecCount, int gasCount) {
        Optional<UserPreference> prefOpt = userPreferenceRepository.findByUserId(userId);

        List<TipRule> elecRules = tipRuleRepository.findByUtilityOrderByPriorityAsc("ELEC");
        List<TipRule> gasRules  = tipRuleRepository.findByUtilityOrderByPriorityAsc("GAS");

        Predicate<TipRule> matches = r -> {
            if (prefOpt.isEmpty()) {
                return r.getHousingType() == null
                        && r.getHasDoubleDoor() == null
                        && r.getWindowType() == null;
            }
            UserPreference p = prefOpt.get();
            boolean h = (r.getHousingType() == null) || Objects.equals(r.getHousingType(), p.getHousingType());
            boolean d = (r.getHasDoubleDoor() == null) || Objects.equals(r.getHasDoubleDoor(), p.getHasDoubleDoor());
            boolean w = (r.getWindowType() == null) || Objects.equals(r.getWindowType(), p.getWindowType());
            return h && d && w;
        };

        List<String> elecTips = elecRules.stream()
                .filter(matches)
                .limit(Math.max(0, elecCount))
                .map(TipRule::getMessage)
                .collect(Collectors.toList());

        List<String> gasTips = gasRules.stream()
                .filter(matches)
                .limit(Math.max(0, gasCount))
                .map(TipRule::getMessage)
                .collect(Collectors.toList());

        if (elecTips.isEmpty()) elecTips = List.of("멀티탭 대기전력 OFF로 전기 누수부터 막아봐요 🔌");
        if (gasTips.isEmpty())  gasTips  = List.of("보일러 외출 모드로 불필요한 가동을 줄여요 ⏱️");

        Map<String, List<String>> result = new LinkedHashMap<>();
        result.put("ELEC", elecTips);
        result.put("GAS", gasTips);
        return result;
    }
}
