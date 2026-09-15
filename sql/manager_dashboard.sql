-- 관리자 대시보드 집계 쿼리 (강선구 담당) - dao.ManagerDao 가 실행하는 SQL 그대로 (참고/공유용)
-- 화면 : /Manager -> manager/dashboard.jsp   (관리자 sessionLevel='top' 전용)
--
-- 매출 정의
--   확정 매출   : 출차 완료(reservation_status='3') 예약의 결제 합계. 환불(payment_type='3')은 차감
--   받아둔 예약금 : 아직 출차/취소 전 예약(status 1,2)의 예약금 결제(payment_type='1')
--   → 예약금은 前受金이지 매출이 아니라서 분리. 출차 결제 기능이 붙어 reservation_final_amount 가 생기면
--     확정 매출은 그 컬럼 합계로 바꾼다.
--
-- 상태 코드 (오윤섭 ReservationInfoDto / PaymentDto 주석 기준)
--   reservation_status : 1 예약완료  2 주차 중  3 출차 완료  4 취소
--   payment_type       : 1 예약금    2 출차 결제  3 환불

-- 1) 오늘/어제 이용 예약 건수
SELECT NVL(SUM(CASE WHEN TRUNC(reservation_start_time) = TRUNC(SYSDATE)     THEN 1 ELSE 0 END),0) AS today_cnt,
       NVL(SUM(CASE WHEN TRUNC(reservation_start_time) = TRUNC(SYSDATE) - 1 THEN 1 ELSE 0 END),0) AS yesterday_cnt
FROM   icn_reservation
WHERE  reservation_status <> '4';

-- 2) 지금 주차 중 / 입차 대기
SELECT NVL(SUM(CASE WHEN reservation_status = '2' THEN 1 ELSE 0 END),0) AS parking_cnt,
       NVL(SUM(CASE WHEN reservation_status = '1' AND reservation_start_time >= SYSDATE THEN 1 ELSE 0 END),0) AS waiting_cnt
FROM   icn_reservation;

-- 3) 확정 매출 이번 달 / 지난 달
SELECT NVL(SUM(CASE WHEN TRUNC(pay_date,'MM') = TRUNC(SYSDATE,'MM')                THEN amt END),0) AS this_month,
       NVL(SUM(CASE WHEN TRUNC(pay_date,'MM') = ADD_MONTHS(TRUNC(SYSDATE,'MM'),-1) THEN amt END),0) AS last_month
FROM (
    SELECT p.payment_date AS pay_date,
           CASE WHEN p.payment_type = '3' THEN -p.payment_amount ELSE p.payment_amount END AS amt
    FROM   icn_payment p
    JOIN   icn_reservation r ON r.reservation_id = p.reservation_id
    WHERE  r.reservation_status = '3'
);

-- 4) 받아둔 예약금
SELECT NVL(SUM(p.payment_amount),0) AS deposit_sum, COUNT(*) AS deposit_cnt
FROM   icn_payment p
JOIN   icn_reservation r ON r.reservation_id = p.reservation_id
WHERE  p.payment_type = '1'
AND    r.reservation_status IN ('1','2');

-- 5) 최근 14일 일별 예약 건수 + 입금액 (예약 없는 날도 0 으로 나오게 날짜 표를 CONNECT BY 로 만든다)
SELECT TO_CHAR(d.dt,'YYYY-MM-DD') AS day, TO_CHAR(d.dt,'MM-DD') AS day_label,
       NVL(r.cnt,0) AS resv_cnt, NVL(p.amt,0) AS pay_amt
FROM  (SELECT TRUNC(SYSDATE) - 14 + LEVEL AS dt FROM dual CONNECT BY LEVEL <= 14) d
LEFT JOIN (SELECT TRUNC(reservation_start_time) AS dt, COUNT(*) AS cnt
           FROM   icn_reservation WHERE reservation_status <> '4'
           GROUP BY TRUNC(reservation_start_time)) r ON r.dt = d.dt
LEFT JOIN (SELECT TRUNC(payment_date) AS dt,
                  SUM(CASE WHEN payment_type = '3' THEN -payment_amount ELSE payment_amount END) AS amt
           FROM   icn_payment GROUP BY TRUNC(payment_date)) p ON p.dt = d.dt
ORDER BY d.dt;

-- 6) 구역별 현재 이용률 (사용 중 = 시작시각 지났고 종료시각 안 지난 예약. 자유출차형은 종료시각이 없으니 출차 전까지)
SELECT s.lot_id, COUNT(*) AS total_cnt, NVL(u.used_cnt,0) AS used_cnt,
       ROUND(NVL(u.used_cnt,0) * 100 / COUNT(*)) AS use_rate
FROM   icn_seat s
LEFT JOIN (SELECT s2.lot_id, COUNT(DISTINCT r.seat_no) AS used_cnt
           FROM   icn_reservation r
           JOIN   icn_seat s2 ON s2.seat_no = r.seat_no
           WHERE  r.reservation_status IN ('1','2')
           AND    r.reservation_start_time <= SYSDATE
           AND   (r.reservation_end_time IS NULL OR r.reservation_end_time >= SYSDATE)
           GROUP BY s2.lot_id) u ON u.lot_id = s.lot_id
GROUP BY s.lot_id, u.used_cnt
ORDER BY s.lot_id;

-- 7) 최근 30일 미판매 좌석 (구역별)
SELECT s.lot_id, COUNT(*) AS total_cnt,
       SUM(CASE WHEN r.seat_no IS NULL THEN 1 ELSE 0 END) AS unsold_cnt,
       ROUND(SUM(CASE WHEN r.seat_no IS NULL THEN 1 ELSE 0 END) * 100 / COUNT(*)) AS unsold_rate
FROM   icn_seat s
LEFT JOIN (SELECT DISTINCT seat_no FROM icn_reservation
           WHERE reservation_status <> '4' AND reservation_start_time >= TRUNC(SYSDATE) - 30) r
       ON r.seat_no = s.seat_no
GROUP BY s.lot_id
ORDER BY s.lot_id;

-- 8) 최근 예약 10건
SELECT * FROM (
    SELECT r.reservation_id, NVL(m.name, r.member_id) AS member_name, r.seat_no,
           DECODE(r.reservation_type,'1','예약형','2','자유출차형',r.reservation_type) AS type_label,
           r.reservation_status AS status,
           DECODE(r.reservation_status,'1','예약완료','2','주차 중','3','출차 완료','4','취소',r.reservation_status) AS status_label,
           TO_CHAR(r.reservation_start_time,'MM-DD HH24:MI') AS start_text,
           NVL(r.reservation_deposit_amount,0) AS deposit
    FROM   icn_reservation r
    LEFT JOIN icn_member m ON m.member_id = r.member_id
    ORDER BY r.reservation_start_time DESC, r.reservation_id DESC
) WHERE ROWNUM <= 10;

-- 9) 데이터 점검
SELECT
 (SELECT COUNT(*) FROM (SELECT reservation_id, payment_type FROM icn_payment
                        GROUP BY reservation_id, payment_type HAVING COUNT(*) > 1)) AS dup_payment,      -- 중복 결제
 (SELECT COUNT(*) FROM icn_payment p
  WHERE NOT EXISTS (SELECT 1 FROM icn_reservation r WHERE r.reservation_id = p.reservation_id)) AS orphan_payment, -- 예약 없는 결제
 (SELECT COUNT(*) FROM icn_reservation r WHERE r.reservation_status <> '4'
  AND NOT EXISTS (SELECT 1 FROM icn_payment p WHERE p.reservation_id = r.reservation_id)) AS resv_no_payment,       -- 결제 없는 예약
 (SELECT COUNT(*) FROM icn_reservation WHERE reservation_status = '1'
  AND reservation_type = '1' AND reservation_end_time < SYSDATE) AS overdue_resv                                     -- 노쇼 의심
FROM dual;
