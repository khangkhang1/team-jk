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
