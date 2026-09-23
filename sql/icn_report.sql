-- 신고 접수 테이블 (강선구 담당 - 관리자 콘솔 "신고 내역")
--
-- 왜 필요한가 :
--   09-10 회의에서 "신고 게시판 / 신고 리스트 / 처리 중·처리 완료 현황"이 비어 있다는 이야기가 나왔다.
--   현장(공항 주차장)에서 실제로 생기는 일은 "예약한 내 자리에 다른 차가 서 있다", "충전기가 고장났다"
--   처럼 시스템이 스스로 알 수 없는 사건이다. 이용자가 알려주고 관리자가 조치한 기록을 남기는 곳이다.
--   → 전화로 받아 적던 것을 화면에 남기면 누가·언제·어떻게 처리했는지가 남는다 (業務改善 : 対応履歴の可視化).
--
-- 3차 회의에서 폐기한 것은 "방치차량 신고 시스템"(차량 조회·견인 연계까지 가는 무거운 기능)이고,
-- 이건 접수 → 처리 상태 관리까지만 하는 가벼운 접수 창구다. 무거운 로직은 넣지 않는다.
--
-- 서블릿 : controller.Manager (관리자 전용, sessionLevel = 'top')
--   Manager?t_gubun=report        신고 목록 (t_status / t_type / t_select / t_search / t_nowPage) → manager/report_list.jsp
--   Manager?t_gubun=reportView    신고 상세 (t_report_id)                                        → manager/report_view.jsp
--   Manager?t_gubun=reportAnswer  처리 상태 + 처리 내용 저장 command.manager.ReportAnswer         → common_alert.jsp
-- DAO : dao.ReportDao / DTO : dto.ReportDto
--
-- [외래키를 걸지 않은 이유]
--   예약·결제 데이터를 개발 중에 통째로 비우는 일이 잦다(건수가 안 맞아 지우는 등).
--   icn_reservation 을 참조하는 FK 가 걸려 있으면 그 삭제가 막히거나 순서를 맞춰야 해서 팀원 작업을 방해한다.
--   그래서 seat_no / reservation_id 는 "참고용 값"으로만 두고, 존재 여부는 화면에서 LEFT JOIN 으로 확인한다.
--   데이터가 안정되는 결합테스트(結合テスト) 단계에서 FK 를 추가하는 것이 원래 순서다.

CREATE SEQUENCE icn_report_seq
    START WITH 1
    INCREMENT BY 1
    NOCACHE;

CREATE TABLE icn_report (
    report_id       NUMBER          PRIMARY KEY,                -- 시퀀스로 채움
    report_type     VARCHAR2(1)     DEFAULT '5' NOT NULL,       -- 1 자리 무단점유 / 2 시설 파손·고장 / 3 차량 훼손 / 4 불법 주차 / 5 기타
    report_status   VARCHAR2(1)     DEFAULT '1' NOT NULL,       -- 1 접수 / 2 처리 중 / 3 처리 완료 / 4 반려
    title           VARCHAR2(200)   NOT NULL,
    content         VARCHAR2(2000)  NOT NULL,
    member_id       VARCHAR2(20),                               -- 신고한 회원 (icn_member.member_id)
    seat_no         VARCHAR2(20),                               -- 신고 대상 좌석 (icn_seat.seat_no) - 없을 수 있음
    reservation_id  VARCHAR2(30),                               -- 관련 예약 (icn_reservation.reservation_id) - 없을 수 있음
    reg_date        DATE            DEFAULT SYSDATE NOT NULL,
    answer_content  VARCHAR2(2000),                             -- 관리자 처리 내용
    answer_id       VARCHAR2(20),                               -- 처리한 관리자 ID
    answer_date     DATE,                                       -- 마지막 처리 일시
    CONSTRAINT ck_icn_report_type   CHECK (report_type   IN ('1','2','3','4','5')),
    CONSTRAINT ck_icn_report_status CHECK (report_status IN ('1','2','3','4'))
);

-- 목록은 "상태 + 최신순"으로만 본다. 건수가 늘어도 정렬·필터가 인덱스를 타게 한다.
CREATE INDEX ix_icn_report_status ON icn_report (report_status, reg_date);

-- ------------------------------------------------------------------ 확인용 데이터
-- 화면(목록 필터·상세·처리)이 도는지 보려고 넣는 값이다. 실제 발표 전에 지워도 된다.
-- DELETE FROM icn_report; COMMIT;

INSERT INTO icn_report (report_id, report_type, report_status, title, content, member_id, seat_no, reg_date)
VALUES (icn_report_seq.NEXTVAL, '1', '1', '예약한 자리에 다른 차가 서 있습니다',
        'P1-07 로 예약했는데 흰색 SUV 가 이미 주차되어 있습니다. 옆자리에 임시로 세워 두었습니다. 확인 부탁드립니다.',
        'asdf', 'P1-07', SYSDATE - 0.2);

INSERT INTO icn_report (report_id, report_type, report_status, title, content, member_id, seat_no, reg_date, answer_content, answer_id, answer_date)
VALUES (icn_report_seq.NEXTVAL, '2', '2', '전기차 충전기가 작동하지 않습니다',
        'P6-03 충전기 화면이 꺼져 있고 케이블을 꽂아도 반응이 없습니다.',
        'asdf', 'P6-03', SYSDATE - 1.1,
        '시설팀에 점검 요청했습니다. 오늘 오후 방문 예정입니다.', 'manager', SYSDATE - 0.9);

INSERT INTO icn_report (report_id, report_type, report_status, title, content, member_id, seat_no, reg_date)
VALUES (icn_report_seq.NEXTVAL, '3', '1', '출차해 보니 뒷범퍼에 긁힌 자국이 있습니다',
        '입차할 때는 없던 자국입니다. 주변 CCTV 확인 가능할까요? 연락 주시면 사진 보내드리겠습니다.',
        'asdf', 'P2-05', SYSDATE - 3.4);

INSERT INTO icn_report (report_id, report_type, report_status, title, content, member_id, seat_no, reg_date)
VALUES (icn_report_seq.NEXTVAL, '1', '1', '장기 주차 차량이 계속 자리를 막고 있습니다',
        'P9-20 자리에 같은 차가 일주일 넘게 서 있는 것 같습니다. 예약자가 있는 자리인지 확인 부탁드립니다.',
        'asdf', 'P9-20', SYSDATE - 5.3);

INSERT INTO icn_report (report_id, report_type, report_status, title, content, member_id, seat_no, reg_date, answer_content, answer_id, answer_date)
VALUES (icn_report_seq.NEXTVAL, '4', '3', '장애인 구역에 일반 차량이 주차했습니다',
        'P5-01 장애인 전용 구역에 표지 없는 차량이 서 있습니다.',
        'asdf', 'P5-01', SYSDATE - 9.5,
        '현장 확인 후 차주에게 연락하여 이동 조치했습니다. 안내문을 추가로 부착했습니다.', 'manager', SYSDATE - 9.1);

INSERT INTO icn_report (report_id, report_type, report_status, title, content, member_id, seat_no, reg_date, answer_content, answer_id, answer_date)
VALUES (icn_report_seq.NEXTVAL, '2', '3', '주차장 3구역 조명이 꺼져 있습니다',
        '야간에 P3 구역 가운데 조명 두 개가 들어오지 않습니다.',
        'asdf', 'P3-12', SYSDATE - 14.2,
        '등 교체 완료했습니다. 알려주셔서 감사합니다.', 'manager', SYSDATE - 13.6);

INSERT INTO icn_report (report_id, report_type, report_status, title, content, member_id, reg_date, answer_content, answer_id, answer_date)
VALUES (icn_report_seq.NEXTVAL, '5', '4', '주차 요금이 잘못 결제된 것 같습니다',
        '카드 명세에 두 번 결제된 것으로 보입니다.',
        'asdf', SYSDATE - 20.4,
        '결제 내역 확인 결과 승인 취소된 건이 함께 표시된 것으로, 실제 청구는 1건입니다. 요금 문의는 마이페이지 결제 내역에서 확인 부탁드립니다.',
        'manager', SYSDATE - 20.1);

COMMIT;

-- 확인
-- SELECT report_id, report_type, report_status, title, TO_CHAR(reg_date,'MM-DD HH24:MI') FROM icn_report ORDER BY report_id;
