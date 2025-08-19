package com.example.greenroutine.service;

import com.example.greenroutine.api.dto.dashboard.DailyLineResponse;
import com.example.greenroutine.domain.UsageLog;
import com.example.greenroutine.repository.UsageLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MetricsService {
    private final UsageLogRepository usageLogRepository;

    @Transactional(readOnly = true)
    public DailyLineResponse getDailyLine(Long userId, String date) {
        // 0~23 라벨
        List<String> labels = java.util.stream.IntStream.range(0, 24)
                .mapToObj(h -> String.format("%02d", h)).toList();

        LocalDate d = LocalDate.parse(date);
        LocalDateTime start = d.atStartOfDay();
        LocalDateTime end   = d.plusDays(1).atStartOfDay();

        List<UsageLog> logs = usageLogRepository.findByUserAndRange(userId, start, end);
        // 시간대 합산
        int[] elecArr = new int[24];
        int[] gasArr  = new int[24];
        for (UsageLog l : logs) {
            int h = l.getTs().getHour();
            elecArr[h] += (l.getElec() == null ? 0 : l.getElec());
            gasArr[h]  += (l.getGas()  == null ? 0 : l.getGas());
        }

        List<Integer> elec = new ArrayList<>(24);
        List<Integer> gas  = new ArrayList<>(24);

        // 데이터 없으면 시연용 더미 생성
        if (logs.isEmpty()) {
            java.util.Random rnd = new java.util.Random((userId + date).hashCode());
            for (int h = 0; h < 24; h++) {
                int baseElec = (h >= 11 && h <= 21) ? 50 : 20;
                int baseGas = (h >= 6 && h <= 9) || (h >= 19 && h <= 22) ? 18 : 8;
                elec.add(Math.max(0, baseElec + rnd.nextInt(15) - 7));
                gas.add(Math.max(0, baseGas + rnd.nextInt(8) - 4));
            }
        } else {
            for (int h = 0; h < 24; h++) {
                elec.add(elecArr[h]);
                gas.add(gasArr[h]);
            }
        }

        return DailyLineResponse.builder()
                .date(date)
                .labels(labels)
                .elec(elec)
                .gas(gas)
                .build();
        }
    }
