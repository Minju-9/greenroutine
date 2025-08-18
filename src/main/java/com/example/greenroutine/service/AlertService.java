package com.example.greenroutine.service;

import com.example.greenroutine.domain.Alert;
import com.example.greenroutine.domain.User;
import com.example.greenroutine.repository.AlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)

public class AlertService {
    private final AlertRepository repo;
    @Transactional
    public Alert createBill(Long userId, String title, LocalDateTime at) {
        return save(userId, "BILL", "알림", title, at);
    }

    @Transactional
    public Alert createAnomaly(Long userId, String category, String level, String title, LocalDateTime at) {
        return save(userId, category, level, title, at);
    }

    private Alert save(Long userId, String category, String level, String title, LocalDateTime at) {
        User ref = new User();
        ref.setId(userId);

        Alert a = Alert.builder()
                .user(ref)
                .category(category)
                .level(level)
                .titleExp(title)
                .createdAt(at != null ? at : LocalDateTime.now())
                .yesRead(false)
                .build();

        Alert saved = repo.save(a);
        return saved;
    }

    public Page<Alert> getAll(Long userId, Pageable pageable) {
        return repo.allList(userId, pageable);
    }


    public long countUnread(Long userId) {
        return repo.countUnread(userId);
    }


}
