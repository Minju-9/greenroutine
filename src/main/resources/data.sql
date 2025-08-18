USE greenroutine;

SET FOREIGN_KEY_CHECKS=0;

-- 기존 alert/monthly_charge 정리 & 삽입 (네가 준 그대로)
DELETE FROM alert
WHERE created_at BETWEEN '2025-07-01 00:00:00' AND '2025-08-31 23:59:59';

DELETE FROM monthly_charge
WHERE ym IN ('2025-07','2025-08') AND user_id = 1;

INSERT INTO monthly_charge (user_id, ym, day_value, elec, gas) VALUES
(1,'2025-07',1,18000,4500),
(1,'2025-07',2,21500,4200),
(1,'2025-07',3,59000,5200),
(1,'2025-07',4,21000,4300),
(1,'2025-07',5,20500,4100),
(1,'2025-07',6,22500,4400),
(1,'2025-07',7,23500,4500),

(1,'2025-08',1,52000,4000),
(1,'2025-08',2,15000,6000),
(1,'2025-08',3,18000,5000),
(1,'2025-08',4,21000,5000),
(1,'2025-08',5,20000,4200),
(1,'2025-08',6,22000,5200),
(1,'2025-08',7,24000,5200);

INSERT INTO alert (user_id, message, created_at, is_read, category, level, title_exp, yes_read) VALUES
(1,'2025-07-03 전기요금 급증(데모)','2025-07-03 09:00:00',0,'전기','경고','7월 3일 전력 급증',b'0'),
(1,'2025-08-01 전기요금 급증(데모)','2025-08-01 09:00:00',0,'전기','경고','8월 1일 전력 급증',b'0');

-- ====== 재미나이 팁 룰(전기/가스 카드용) ======
DELETE FROM tip_rule;

-- [전기 카드(ELEC)]
-- 원룸 + 이중문 없음 + 단창 → 가장 구체
INSERT INTO tip_rule(housing_type, has_double_door, window_type, utility, priority, message) VALUES
('원룸', 0, '단창', 'ELEC', 0, '단창이면 낮 시간대 일사차단이 핵심! 커튼/블라인드로 햇빛을 먼저 막아주세요 🌞✋'),
('원룸', 0, NULL , 'ELEC', 1, '현관 쪽 냉기 유입 컷! 바람막이 커튼/도어실 패드가 효과적이에요 🛡️'),
(NULL  , NULL, '단창', 'ELEC', 2, '단열 뽁뽁이/필름만으로도 에어컨 효율이 확 올라갑니다 ❄️'),
(NULL  , NULL, NULL , 'ELEC', 3, '에어컨은 26~27℃, 선풍기 병행으로 체감온도만 내려도 절약 💨');

-- [가스 카드(GAS)]
INSERT INTO tip_rule(housing_type, has_double_door, window_type, utility, priority, message) VALUES
('원룸', NULL, NULL , 'GAS', 0, '보일러는 외출모드/예약가동으로 불필요한 연소 시간을 줄여요 ⏱️'),
(NULL  , NULL, '단창', 'GAS', 1, '창틀 틈 실리콘/패킹 보수로 보일러 열손실 최소화 🔧'),
(NULL  , NULL, NULL , 'GAS', 2, '온수는 “가열 후 끄기” 루틴! 샤워 10분 이내로 줄이면 절감 효과 ↑ 🚿');

SET FOREIGN_KEY_CHECKS=1;
