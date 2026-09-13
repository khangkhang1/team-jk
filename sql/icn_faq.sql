-- FAQ(자주 묻는 질문) 테이블 (강선구 담당)
--
-- 공지사항(icn_notice)과 따로 두는 이유 :
--   공지는 "날짜순으로 흘러가는 글"이고 FAQ는 "카테고리별로 고정돼 있는 Q&A"라
--   정렬 기준(sort_no)과 분류(category)가 필요하다. 조회수·첨부파일은 필요 없다.
--
-- 서블릿 : controller.Faq (/Faq, t_gubun 으로 분기 - Member 서블릿과 같은 구조)
--   Faq                     목록 + 카테고리 필터(t_category)      → faq/faq_list.jsp
--   Faq?t_gubun=writeForm   등록 화면 (관리자)                    → faq/faq_write.jsp
--   Faq?t_gubun=save        등록 처리 (관리자) command.faq.FaqSave → common_alert.jsp
--   Faq?t_gubun=updateForm  수정 화면 (관리자)                    → faq/faq_update.jsp
--   Faq?t_gubun=update      수정 처리 (관리자) command.faq.FaqUpdate
--   Faq?t_gubun=delete      삭제 처리 (관리자) command.faq.FaqDelete
-- DAO : dao.FaqDao / DTO : dto.FaqDto
--
-- 관리자 판별은 세션 sessionLevel = 'top' (MemberLogin 이 manager 로그인 때 넣어줌). 등록자 ID 는 세션 sessionId.

CREATE SEQUENCE icn_faq_seq
    START WITH 1
    INCREMENT BY 1
    NOCACHE;

CREATE TABLE icn_faq (
    faq_id      NUMBER          PRIMARY KEY,                -- 시퀀스로 채움
    category    VARCHAR2(20)    NOT NULL,                   -- 예약 / 요금·결제 / 입·출차 / 항공편
    question    VARCHAR2(300)   NOT NULL,
    answer      CLOB            NOT NULL,                   -- 길어질 수 있어서 CLOB
    sort_no     NUMBER          DEFAULT 100,                -- 작을수록 위. 같으면 faq_id 순
    use_yn      CHAR(1)         DEFAULT 'Y',                -- N 이면 이용자에게 안 보임(관리자만 보임)
    hit         NUMBER          DEFAULT 0,                  -- 조회수. 아코디언이라 지금은 안 씀
    reg_id      VARCHAR2(30),                               -- 등록한 관리자 ID (icn_member.member_id)
    reg_date    DATE            DEFAULT SYSDATE
);

-- 초기 데이터 예시
INSERT INTO icn_faq (faq_id, category, question, answer, sort_no, reg_id)
VALUES (icn_faq_seq.NEXTVAL, '예약', '주차 예약은 며칠 전부터 할 수 있나요?',
        '이용일 기준 30일 전부터 예약할 수 있습니다.', 100, 'manager');
COMMIT;

-- [정리 필요] 처음 넣은 데이터의 reg_id 가 'admin' 인데 회원 테이블의 관리자 ID 는 'manager' 다
UPDATE icn_faq SET reg_id = 'manager' WHERE reg_id = 'admin';
COMMIT;


-- ============================================================
-- 아래는 FaqDao.java 가 실제로 실행하는 쿼리 (참고/공유용)
-- ============================================================

-- 1) 목록 (FaqDao.getFaqList) - 이용자는 use_yn='Y' 만, 관리자는 전부.
--    카테고리 버튼을 누르면 AND category = '예약' 이 붙는다.
SELECT faq_id, category, question, answer, sort_no, use_yn, hit, reg_id,
       TO_CHAR(reg_date, 'yy-MM-dd') AS reg_date
FROM   icn_faq
WHERE  1 = 1
AND    use_yn = 'Y'              -- 관리자면 이 줄 없음
AND    category = '예약'          -- 전체 조회면 이 줄 없음
ORDER BY sort_no, faq_id;

-- 2) 한 건 (FaqDao.getFaqView) - 수정 화면 채우기
SELECT faq_id, category, question, answer, sort_no, use_yn, hit, reg_id,
       TO_CHAR(reg_date, 'yy-MM-dd') AS reg_date
FROM   icn_faq
WHERE  faq_id = 1;

-- 3) 등록 (FaqDao.faqSave) - reg_date / hit 는 DEFAULT 가 채우므로 안 적는다
INSERT INTO icn_faq
(faq_id, category, question, answer, sort_no, use_yn, reg_id)
VALUES
(icn_faq_seq.NEXTVAL, '예약', '질문', '답변', 100, 'Y', 'manager');

-- 4) 수정 (FaqDao.faqUpdate) - 등록자/등록일은 안 바꾼다
UPDATE icn_faq
SET    category = '예약',
       question = '질문',
       answer   = '답변',
       sort_no  = 100,
       use_yn   = 'Y'
WHERE  faq_id = 1;

-- 5) 삭제 (FaqDao.faqDelete)
DELETE FROM icn_faq
WHERE  faq_id = 1;
