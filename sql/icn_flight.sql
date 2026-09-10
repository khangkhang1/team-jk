-- 항공편 테이블 (강선구 담당)
-- 5차 회의(2026-09-07) 결론 반영:
--   1) PK는 편명이 아니라 임의 생성 flight_id (같은 항공기가 하루에도 여러 번 운항해서 편명은 PK 불가)
--   2) 레코드 생성 시점은 "예약 시 회원이 항공편명을 검색·입력하는 순간" (미리 전체 항공편을 적재해두지 않음)
--   3) 결항여부(remark)만 실 API로 갱신, 나머지 항목은 참고용
--
-- [2026-09-10 변경] schedule_datetime / estimated_datetime : VARCHAR2(14) -> DATE
--   처음엔 API 원본 문자열("202609062355", 12자리)을 그대로 담으려고 VARCHAR2로 뒀는데,
--   예약 테이블(오윤섭)에서 도착시각과 예약시각을 비교해야 하고 지연 시간 계산도 필요해서
--   DATE가 맞다. 문자열이면 "몇 분 차이"를 SQL에서 못 구한다.
--   (오라클 DATE는 시:분:초까지 담기므로 TIMESTAMP까지는 필요 없다)
--
--   Java쪽은 DTO 필드를 문자열(API 포맷) 그대로 두고 SQL 경계에서만 변환한다.
--     INSERT : to_date(?, 'YYYYMMDDHH24MI')
--     SELECT : to_char(schedule_datetime, 'YYYYMMDDHH24MI')
--   API가 값을 비워 보내는 경우가 있어 FlightDao.setApiDate()에서 12자리 검증 후
--   아니면 NULL을 넣는다(그냥 넣으면 ORA-01861).

CREATE SEQUENCE icn_flight_seq
    START WITH 1
    INCREMENT BY 1
    NOCACHE;

CREATE TABLE icn_flight (
    flight_id           NUMBER          PRIMARY KEY,
    flight_no           VARCHAR2(10)    NOT NULL,           -- 편명, 예: OZ704 (중복 허용)
    airport              VARCHAR2(50),                       -- 상대(출발지) 공항명
    schedule_datetime    DATE,                               -- 원래 도착 예정시각
    estimated_datetime   DATE,                                -- 변경된(지연 등) 도착 예정시각
    remark               VARCHAR2(10)    DEFAULT '도착',      -- 현황: 도착/결항/지연/회항/착륙 (data.go.kr API 값 그대로)
    updated_at           DATE            DEFAULT SYSDATE      -- 마지막으로 API 재조회해서 갱신한 시각
);

-- 참고: 예약 테이블(오윤섭 담당)에는 아래 컬럼이 FK로 필요함 (5차 회의 확정 사항)
--   reservation.flight_id NUMBER  -- 처음엔 FK 제약 없이 컬럼만, 예약 테이블 완성되면 나중에 ALTER TABLE로 추가
-- ALTER TABLE icn_reservation ADD CONSTRAINT fk_reservation_flight
--     FOREIGN KEY (flight_id) REFERENCES icn_flight(flight_id);


-- ============================================================
-- 아래는 FlightDao.java가 실제로 실행하는 쿼리 그대로 (참고/공유용).
-- ============================================================

-- 1) 편명으로 이미 저장돼 있는지 조회 (FlightDao.findByFlightNo)
--    같은 편명이 날짜 바뀌어 여러 번 저장될 수 있어서, 가장 최근 것(flight_id가 가장 큰 것) 하나만 씀.
SELECT *
FROM icn_flight
WHERE flight_no = ?          -- 예: 'OZ704'
ORDER BY flight_id DESC;

-- 2) 신규 항공편 저장 (FlightDao.insertFlight) - 시퀀스 값을 먼저 뽑아서 그 값으로 insert
SELECT icn_flight_seq.NEXTVAL AS newid FROM dual;

INSERT INTO icn_flight (flight_id, flight_no, airport, schedule_datetime, estimated_datetime, remark)
VALUES (?, ?, ?, ?, ?, ?);
-- 바인딩 순서: flight_id(방금 뽑은 시퀀스값), flight_no, airport, schedule_datetime, estimated_datetime, remark

-- 3) flight_id로 단건 조회 (FlightDao.findById) - 결항 재확인할 때 먼저 현재 저장값을 읽어옴
SELECT *
FROM icn_flight
WHERE flight_id = ?;

-- 4) 결항 확정 시 갱신 (FlightDao.updateRemark)
UPDATE icn_flight
SET remark = ?, updated_at = SYSDATE      -- remark에 '결항' 바인딩
WHERE flight_id = ?;


-- ============================================================
-- 참고용: 예약 테이블과 조인하는 예시 (오윤섭 파트에서 쓸 쿼리 - icn_reservation 컬럼명은 가정)
-- "내 예약 목록 + 그 예약에 걸린 항공편의 결항여부"를 한 번에 보고 싶을 때
-- ============================================================
SELECT r.reservation_id, r.seat_id, r.start_time, r.end_time, r.reservation_status,
       f.flight_no, f.remark AS flight_status
FROM icn_reservation r
LEFT JOIN icn_flight f ON r.flight_id = f.flight_id
WHERE r.member_id = ?
ORDER BY r.start_time DESC;


-- ============================================================
-- [이미 테이블을 만든 사람용] VARCHAR2 -> DATE 변경 방법
-- ============================================================
-- 오라클은 값이 들어있는 컬럼의 자료형을 VARCHAR2에서 DATE로 바로 못 바꾼다.
-- (ORA-01439: 데이터 유형을 변경할 열은 비어 있어야 합니다)

-- 방법 A) 테스트 데이터뿐이라 지워도 되면 - 제일 간단
DELETE FROM icn_flight;
ALTER TABLE icn_flight MODIFY (schedule_datetime DATE, estimated_datetime DATE);
COMMIT;

-- 방법 B) 데이터를 살려야 하면 - 임시 컬럼을 거친다
-- ALTER TABLE icn_flight ADD (sched_tmp DATE, est_tmp DATE);
-- UPDATE icn_flight
--    SET sched_tmp = to_date(schedule_datetime,  'YYYYMMDDHH24MI'),
--        est_tmp   = to_date(estimated_datetime, 'YYYYMMDDHH24MI');
-- ALTER TABLE icn_flight DROP (schedule_datetime, estimated_datetime);
-- ALTER TABLE icn_flight RENAME COLUMN sched_tmp TO schedule_datetime;
-- ALTER TABLE icn_flight RENAME COLUMN est_tmp   TO estimated_datetime;
-- COMMIT;

-- 확인
-- SELECT flight_no,
--        to_char(schedule_datetime,  'YYYY-MM-DD HH24:MI') AS 도착예정,
--        to_char(estimated_datetime, 'YYYY-MM-DD HH24:MI') AS 변경시각,
--        remark
--   FROM icn_flight;
