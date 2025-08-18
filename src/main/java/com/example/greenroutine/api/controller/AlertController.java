package com.example.greenroutine.api.controller;

import com.example.greenroutine.api.dto.alert.AlertDto;
import com.example.greenroutine.domain.Alert;
import com.example.greenroutine.service.AlertService;
import com.example.greenroutine.service.AlertSseService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/alerts")
public class AlertController {
    private final AlertService alertService;
    private final AlertSseService alertSseService;

    //sse sub
    //GET /api/alerts/stream?userId=1
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(
            @RequestParam @NotNull @Min(1) Long userId,
            @RequestHeader(name = "Last-Event-ID", required = false) String lastEventId,
            @RequestParam(name = "lastEventId", required = false) String lastEventIdParam
    ){
        String le = (lastEventId != null) ? lastEventId : lastEventIdParam;
        return alertSseService.connect(userId,le);
    }
    //청구알림 생성
    //POST /api/alerts/bill
    @PostMapping("/bill")
    public ResponseEntity<AlertDto.AlertRes> createBill(@Valid @RequestBody AlertDto.CreateBillReq req){
        Alert saved = alertService.createBill(req.getUserId(), req.getTitle(), req.getAt());

        //SSE push
        alertSseService.send(AlertDto.AlertEvent.builder()
                .userId(req.getUserId())
                .category("BILL")
                .level("INFO")
                .title(req.getTitle())
                .build());
        return ResponseEntity
                .created(URI.create("/api/alerts/" +saved.getId()))
                .body(AlertDto.AlertRes.form(saved));
    }
    //POST /api/alerts/anomaly
    @PostMapping("/anomaly")
    public ResponseEntity<AlertDto.AlertRes> createAnomaly(@Valid @RequestBody AlertDto.CreateAnomalyReq req){
        Alert saved = alertService.createAnomaly(
                req.getUserId(), req.getCategory(), req.getLevel(), req.getTitle(),req.getAt());

        //SSE push
        alertSseService.send(AlertDto.AlertEvent.builder()
                . userId(req.getUserId())
                .category(req.getCategory())
                .level(req.getLevel())
                .title(req.getTitle())
                .build());
        return ResponseEntity
                .created(URI.create("/api/alerts/" + saved.getId()))
            .body(AlertDto.AlertRes.form(saved));
    }
    //안 읽음
    //GET /api/alerts/unread-count?userId=1
    @GetMapping("/unread-count")
    public ResponseEntity<Long> unreadConut(@RequestParam @NotNull @Min(1) Long userId){
        return ResponseEntity.ok(alertService.countUnread(userId));
    }
}
