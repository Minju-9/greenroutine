package com.example.greenroutine.service;

import com.example.greenroutine.adapter.ai.GeminiClient;
import com.example.greenroutine.domain.TipRule;
import com.example.greenroutine.domain.UserPreference;
import com.example.greenroutine.repository.TipRuleRepository;
import com.example.greenroutine.repository.UserPreferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class TipService {
    private final UserPreferenceRepository userPreferenceRepository;
    private final TipRuleRepository tipRuleRepository;

    @org.springframework.beans.factory.annotation.Autowired(required = false)
    private GeminiClient geminiClient; // 없으면 자동 폴백

    @Transactional
    public void savePreference(com.example.greenroutine.api.dto.tip.SavePreferenceRequest req) {
        UserPreference pref = userPreferenceRepository.findByUserId(req.getUserId())
                .orElse(UserPreference.builder().userId(req.getUserId()).build());
        if (req.getHousingType() != null) pref.setHousingType(req.getHousingType());
        if (req.getHasDoubleDoor() != null) pref.setHasDoubleDoor(req.getHasDoubleDoor());
        if (req.getWindowType() != null) pref.setWindowType(req.getWindowType());
        userPreferenceRepository.save(pref);
    }

    @Transactional(readOnly = true)
    public Map<String, List<String>> getTips(Long userId, int elecCount, int gasCount) {
        Optional<UserPreference> prefOpt = userPreferenceRepository.findByUserId(userId);
        UserPreference p = prefOpt.orElse(null);

        Map<String, List<String>> external = tryGemini(p, elecCount, gasCount);
        if (external != null && (!external.getOrDefault("ELEC", List.of()).isEmpty()
                || !external.getOrDefault("GAS", List.of()).isEmpty())) {
            return external;
        }
        return getFromRules(prefOpt, elecCount, gasCount);
    }

    private Map<String, List<String>> tryGemini(UserPreference p, int elecCount, int gasCount) {
        if (geminiClient == null) return null;

        String prompt = """
        다음 사용자에게 전기/가스 절약 팁을 한국어로 짧게 만들어 주세요.
        각 항목은 1줄 문장, 이모지 0~1개.
        아래 JSON 형식만 반환하세요(코드블록 금지).
        {
          "ELEC": ["문구1", "문구2", ...],   // 전기 팁 %d개
          "GAS":  ["문구1", "문구2", ...]    // 가스 팁 %d개
        }
        사용자 정보:
        - 주거형태: %s
        - 현관 이중문: %s
        - 창호: %s
        """.formatted(
                Math.max(0, elecCount),
                Math.max(0, gasCount),
                p == null ? "미선택" : nz(p.getHousingType()),
                p == null ? "미선택" : (Boolean.TRUE.equals(p.getHasDoubleDoor()) ? "있음" : "없음"),
                p == null ? "미선택" : nz(p.getWindowType())
        );

        return geminiClient.getTips(prompt);
    }

    private String nz(String s) { return s == null ? "미선택" : s; }

    private Map<String, List<String>> getFromRules(Optional<UserPreference> prefOpt, int elecCount, int gasCount) {
        List<TipRule> elecRules = tipRuleRepository.findByUtilityOrderByPriorityAsc("ELEC");
        List<TipRule> gasRules  = tipRuleRepository.findByUtilityOrderByPriorityAsc("GAS");

        Predicate<TipRule> matches = r -> {
            if (prefOpt.isEmpty()) {
                return r.getHousingType() == null && r.getHasDoubleDoor() == null && r.getWindowType() == null;
            }
            UserPreference p = prefOpt.get();
            boolean h = (r.getHousingType() == null) || Objects.equals(r.getHousingType(), p.getHousingType());
            boolean d = (r.getHasDoubleDoor() == null) || Objects.equals(r.getHasDoubleDoor(), p.getHasDoubleDoor());
            boolean w = (r.getWindowType() == null) || Objects.equals(r.getWindowType(), p.getWindowType());
            return h && d && w;
        };

        List<String> elecTips = elecRules.stream().filter(matches).limit(Math.max(0, elecCount)).map(TipRule::getMessage).toList();
        List<String> gasTips  = gasRules .stream().filter(matches).limit(Math.max(0, gasCount)).map(TipRule::getMessage).toList();

        if (elecTips.isEmpty()) elecTips = List.of("멀티탭 대기전력 OFF로 전기 누수부터 막아봐요 🔌");
        if (gasTips.isEmpty())  gasTips  = List.of("보일러 외출 모드로 불필요한 가동을 줄여요 ⏱️");

        Map<String, List<String>> result = new LinkedHashMap<>();
        result.put("ELEC", elecTips);
        result.put("GAS", gasTips);
        return result;
    }
}
