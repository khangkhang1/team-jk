-- 항공편 테이블 (강선구 담당)
-- 5차 회의(2026-09-07) 결론 반영:
--   1) PK는 편명이 아니라 임의 생성 flight_id (같은 항공기가 하루에도 여러 번 운항해서 편명은 PK 불가)
--   2) 레코드 생성 시점은 "예약 시 회원이 항공편명을 검색·입력하는 순간" (미리 전체 항공편을 적재해두지 않음)
--   3) 결항여부(remark)만 실 API로 갱신, 나머지 항목은 참고용

CREATE SEQUENCE icn_flight_seq
    START WITH 1
    INCREMENT BY 1
    NOCACHE;

CREATE TABLE icn_flight (
    flight_id           NUMBER          PRIMARY KEY,
    flight_no           VARCHAR2(10)    NOT NULL,           -- 편명, 예: OZ704 (중복 허용)
    airport              VARCHAR2(50),                       -- 상대(출발지) 공항명
    schedule_datetime    VARCHAR2(14),                       -- 원래 도착 예정시각, API 원본 포맷 YYYYMMDDHH24MI 그대로 저장
    estimated_datetime   VARCHAR2(14),                       -- 변경된 시각(API 응답 그대로)
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
