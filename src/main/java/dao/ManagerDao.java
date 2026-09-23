package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import common.DBConnection;

/**
 * 관리자 콘솔 DAO - 강선구 담당.
 *
 * 여러 테이블을 합쳐 "숫자"를 뽑는 집계 전용이라 DTO 클래스를 따로 만들지 않고
 * 한 행을 HashMap(소문자 컬럼명 -> 값) 으로 돌려준다. JSP 에서는 ${row.lot_id} 처럼 DTO 와 똑같이 꺼내 쓴다.
 *
 * 값은 전부 ? 바인딩. 관리자 화면은 기간·구역·검색어 같은 조건이 파라미터로 들어오므로
 * 문자열 이어붙이기(수업 방식) 대신 PreparedStatement 바인딩을 쓴다 (SQL 인젝션 방지).
 * 컬럼명처럼 바인딩이 안 되는 것은 화이트리스트로 고른다 (getReservationList 의 select).
 *
 * 매출 정의 (오윤섭 결제 테이블 기준)
 *   - 확정 매출   : 출차 완료(reservation_status='3') 된 예약의 결제 합계. 환불(payment_type='3')은 차감
 *   - 받아둔 예약금 : 아직 출차/취소되지 않은 예약(status 1,2)의 예약금 결제(payment_type='1')
 *   → 예약금은 맡아둔 돈(前受金)이지 매출이 아니므로 둘을 분리한다.
 *
 * 상태 코드 : reservation_status 1 예약완료 / 2 주차 중 / 3 출차 완료 / 4 취소
 *            payment_type       1 예약금  / 2 출차 결제 / 3 환불
 */
public class ManagerDao {

	// 목록·상세에서 반복해서 쓰는 라벨 변환
	private static final String STATUS_LABEL =
		"DECODE(r.reservation_status,'1','예약완료','2','주차 중','3','출차 완료','4','취소',r.reservation_status)";
	private static final String TYPE_LABEL =
		"DECODE(r.reservation_type,'1','예약형','2','자유출차형',r.reservation_type)";

	// 예약 테이블이 flight_id 대신 flight_no 를 갖게 바뀌었다. 같은 편명이 날짜별로 여러 줄일 수 있어 최신 1줄만 붙인다
	private static final String FLIGHT_JOIN =
		  "LEFT JOIN (SELECT flight_no, airport, schedule_datetime, remark,\r\n"
		+ "                  ROW_NUMBER() OVER (PARTITION BY flight_no ORDER BY updated_at DESC NULLS LAST, flight_id DESC) AS rn\r\n"
		+ "           FROM icn_flight) f ON f.flight_no = r.flight_no AND f.rn = 1\r\n";

	// ================================================================ 대시보드

	// 상단 지표 4개. 키 : today_cnt, yesterday_cnt, parking_cnt, waiting_cnt, this_month, last_month, deposit_sum, deposit_cnt
	public HashMap<String, Object> getKpi() {
		HashMap<String, Object> kpi = new HashMap<>();

		kpi.putAll(selectOne(
			  "SELECT NVL(SUM(CASE WHEN TRUNC(reservation_start_time) = TRUNC(SYSDATE)     THEN 1 ELSE 0 END),0) AS today_cnt,\r\n"
			+ "       NVL(SUM(CASE WHEN TRUNC(reservation_start_time) = TRUNC(SYSDATE) - 1 THEN 1 ELSE 0 END),0) AS yesterday_cnt\r\n"
			+ "FROM   icn_reservation\r\n"
			+ "WHERE  reservation_status <> '4'"));

		kpi.putAll(selectOne(
			  "SELECT NVL(SUM(CASE WHEN reservation_status = '2' THEN 1 ELSE 0 END),0) AS parking_cnt,\r\n"
			+ "       NVL(SUM(CASE WHEN reservation_status = '1' AND reservation_start_time >= SYSDATE THEN 1 ELSE 0 END),0) AS waiting_cnt\r\n"
			+ "FROM   icn_reservation"));

		kpi.putAll(selectOne(
			  "SELECT NVL(SUM(CASE WHEN TRUNC(pay_date,'MM') = TRUNC(SYSDATE,'MM')                THEN amt END),0) AS this_month,\r\n"
			+ "       NVL(SUM(CASE WHEN TRUNC(pay_date,'MM') = ADD_MONTHS(TRUNC(SYSDATE,'MM'),-1) THEN amt END),0) AS last_month\r\n"
			+ "FROM (\r\n"
			+ "    SELECT p.payment_date AS pay_date,\r\n"
			+ "           CASE WHEN p.payment_type = '3' THEN -p.payment_amount ELSE p.payment_amount END AS amt\r\n"
			+ "    FROM   icn_payment p\r\n"
			+ "    JOIN   icn_reservation r ON r.reservation_id = p.reservation_id\r\n"
			+ "    WHERE  r.reservation_status = '3'\r\n"
			+ ")"));

		kpi.putAll(selectOne(
			  "SELECT NVL(SUM(p.payment_amount),0) AS deposit_sum, COUNT(*) AS deposit_cnt\r\n"
			+ "FROM   icn_payment p\r\n"
			+ "JOIN   icn_reservation r ON r.reservation_id = p.reservation_id\r\n"
			+ "WHERE  p.payment_type = '1'\r\n"
			+ "AND    r.reservation_status IN ('1','2')"));

		return kpi;
	}

	// 최근 N일 일별 예약 건수 + 입금액. 예약이 없는 날도 0 으로 나오게 날짜 표(CONNECT BY)에 붙인다.
	public ArrayList<HashMap<String, Object>> getDailyStats(int days) {
		String sql =
			  "SELECT TO_CHAR(d.dt,'YYYY-MM-DD') AS day, TO_CHAR(d.dt,'MM-DD') AS day_label,\r\n"
			+ "       NVL(r.cnt,0) AS resv_cnt, NVL(p.amt,0) AS pay_amt\r\n"
			+ "FROM  (SELECT TRUNC(SYSDATE) - ? + LEVEL AS dt FROM dual CONNECT BY LEVEL <= ?) d\r\n"
			+ "LEFT JOIN (SELECT TRUNC(reservation_start_time) AS dt, COUNT(*) AS cnt\r\n"
			+ "           FROM   icn_reservation WHERE reservation_status <> '4'\r\n"
			+ "           GROUP BY TRUNC(reservation_start_time)) r ON r.dt = d.dt\r\n"
			+ "LEFT JOIN (SELECT TRUNC(payment_date) AS dt,\r\n"
			+ "                  SUM(CASE WHEN payment_type = '3' THEN -payment_amount ELSE payment_amount END) AS amt\r\n"
			+ "           FROM   icn_payment GROUP BY TRUNC(payment_date)) p ON p.dt = d.dt\r\n"
			+ "ORDER BY d.dt";
		return selectRows(sql, days, days);
	}

	// 구역별 좌석 수와 지금 사용 중인 좌석 수. 키 : lot_id, total_cnt, used_cnt, use_rate
	public ArrayList<HashMap<String, Object>> getLotOccupancy() {
		String sql =
			  "SELECT s.lot_id, COUNT(*) AS total_cnt, NVL(u.used_cnt,0) AS used_cnt,\r\n"
			+ "       ROUND(NVL(u.used_cnt,0) * 100 / COUNT(*)) AS use_rate\r\n"
			+ "FROM   icn_seat s\r\n"
			+ "LEFT JOIN (SELECT s2.lot_id, COUNT(DISTINCT r.seat_no) AS used_cnt\r\n"
			+ "           FROM   icn_reservation r\r\n"
			+ "           JOIN   icn_seat s2 ON s2.seat_no = r.seat_no\r\n"
			+ "           WHERE  r.reservation_status IN ('1','2')\r\n"
			+ "           AND    r.reservation_start_time <= SYSDATE\r\n"
			+ "           AND   (r.reservation_end_time IS NULL OR r.reservation_end_time >= SYSDATE)\r\n"
			+ "           GROUP BY s2.lot_id) u ON u.lot_id = s.lot_id\r\n"
			+ "GROUP BY s.lot_id, u.used_cnt\r\n"
			+ "ORDER BY s.lot_id";
		return selectRows(sql);
	}

	// 최근 N일 동안 한 번도 예약되지 않은 좌석 수, 구역별
	public ArrayList<HashMap<String, Object>> getUnsoldByLot(int days) {
		String sql =
			  "SELECT s.lot_id, COUNT(*) AS total_cnt,\r\n"
			+ "       SUM(CASE WHEN r.seat_no IS NULL THEN 1 ELSE 0 END) AS unsold_cnt,\r\n"
			+ "       ROUND(SUM(CASE WHEN r.seat_no IS NULL THEN 1 ELSE 0 END) * 100 / COUNT(*)) AS unsold_rate\r\n"
			+ "FROM   icn_seat s\r\n"
			+ "LEFT JOIN (SELECT DISTINCT seat_no FROM icn_reservation\r\n"
			+ "           WHERE reservation_status <> '4' AND reservation_start_time >= TRUNC(SYSDATE) - ?) r\r\n"
			+ "       ON r.seat_no = s.seat_no\r\n"
			+ "GROUP BY s.lot_id\r\n"
			+ "ORDER BY s.lot_id";
		return selectRows(sql, days);
	}

	// 최근 예약 N건
	public ArrayList<HashMap<String, Object>> getRecentReservations(int limit) {
		String sql =
			  "SELECT * FROM (\r\n"
			+ "    SELECT r.reservation_id, NVL(m.name, r.member_id) AS member_name, r.seat_no,\r\n"
			+ "           " + TYPE_LABEL + " AS type_label,\r\n"
			+ "           r.reservation_status AS status, " + STATUS_LABEL + " AS status_label,\r\n"
			+ "           TO_CHAR(r.reservation_start_time,'MM-DD HH24:MI') AS start_text,\r\n"
			+ "           (SELECT NVL(SUM(p.payment_amount),0) FROM icn_payment p WHERE p.reservation_id = r.reservation_id AND p.payment_type = '1') AS deposit\r\n"
			+ "    FROM   icn_reservation r\r\n"
			+ "    LEFT JOIN icn_member m ON m.member_id = r.member_id\r\n"
			+ "    ORDER BY r.reservation_start_time DESC, r.reservation_id DESC\r\n"
			+ ") WHERE ROWNUM <= ?";
		return selectRows(sql, limit);
	}

	// 데이터 점검. 키 : dup_payment, orphan_payment, resv_no_payment, overdue_resv
	public HashMap<String, Object> getIntegrityCheck() {
		String sql =
			  "SELECT\r\n"
			+ " (SELECT COUNT(*) FROM (SELECT reservation_id, payment_type FROM icn_payment\r\n"
			+ "                        GROUP BY reservation_id, payment_type HAVING COUNT(*) > 1)) AS dup_payment,\r\n"
			+ " (SELECT COUNT(*) FROM icn_payment p\r\n"
			+ "  WHERE NOT EXISTS (SELECT 1 FROM icn_reservation r WHERE r.reservation_id = p.reservation_id)) AS orphan_payment,\r\n"
			+ " (SELECT COUNT(*) FROM icn_reservation r WHERE r.reservation_status <> '4'\r\n"
			+ "  AND NOT EXISTS (SELECT 1 FROM icn_payment p WHERE p.reservation_id = r.reservation_id)) AS resv_no_payment,\r\n"
			+ " (SELECT COUNT(*) FROM icn_reservation WHERE reservation_status = '1'\r\n"
			+ "  AND reservation_type = '1' AND reservation_end_time < SYSDATE) AS overdue_resv\r\n"
			+ "FROM dual";
		return selectOne(sql);
	}

	// ================================================================ 예약 관리

	// 검색 컬럼은 바인딩이 안 되므로 화이트리스트에서 고른다. 목록에 없는 값이면 예약번호 검색.
	private String searchColumn(String select) {
		if ("member_id".equals(select)) return "r.member_id";
		if ("seat_no".equals(select))   return "r.seat_no";
		return "r.reservation_id";
	}

	// 검색 조건 WHERE 절 + 바인딩 값. 목록과 건수가 같은 조건을 쓰도록 한 곳에서 만든다.
	private String reservationWhere(String select, String search, String status, ArrayList<Object> params) {
		String where = "WHERE UPPER(" + searchColumn(select) + ") LIKE UPPER('%' || ? || '%')\r\n";
		params.add(search == null ? "" : search);
		if (status != null && !status.equals("")) {
			where += "AND   r.reservation_status = ?\r\n";
			params.add(status);
		}
		return where;
	}

	public int getReservationCount(String select, String search, String status) {
		ArrayList<Object> params = new ArrayList<>();
		String sql = "SELECT COUNT(*) AS cnt FROM icn_reservation r\r\n" + reservationWhere(select, search, status, params);
		HashMap<String, Object> row = selectOne(sql, params.toArray());
		return row.isEmpty() ? 0 : ((Number) row.get("cnt")).intValue();
	}

	// 예약 목록 한 페이지 (rnum start~end). 키 : reservation_id, member_name, member_id, seat_no, type_label,
	//   status, status_label, start_text, end_text, out_text, deposit, flight_no, flight_remark
	public ArrayList<HashMap<String, Object>> getReservationList(String select, String search, String status, int start, int end) {
		ArrayList<Object> params = new ArrayList<>();
		String where = reservationWhere(select, search, status, params);
		params.add(start);
		params.add(end);
		String sql =
			  "SELECT * FROM (\r\n"
			+ "  SELECT ROWNUM AS rnum, t.* FROM (\r\n"
			+ "    SELECT r.reservation_id, NVL(m.name, r.member_id) AS member_name, r.member_id, r.seat_no,\r\n"
			+ "           " + TYPE_LABEL + " AS type_label,\r\n"
			+ "           r.reservation_status AS status, " + STATUS_LABEL + " AS status_label,\r\n"
			+ "           TO_CHAR(r.reservation_start_time,'MM-DD HH24:MI') AS start_text,\r\n"
			+ "           TO_CHAR(r.reservation_end_time,'MM-DD HH24:MI')   AS end_text,\r\n"
			+ "           TO_CHAR(r.reservation_out_time,'MM-DD HH24:MI')   AS out_text,\r\n"
			+ "           (SELECT NVL(SUM(p.payment_amount),0) FROM icn_payment p WHERE p.reservation_id = r.reservation_id AND p.payment_type = '1') AS deposit,\r\n"
			+ "           r.flight_no, f.remark AS flight_remark\r\n"
			+ "    FROM   icn_reservation r\r\n"
			+ "    LEFT JOIN icn_member m ON m.member_id = r.member_id\r\n"
			+ "    " + FLIGHT_JOIN
			+ where
			+ "    ORDER BY r.reservation_start_time DESC, r.reservation_id DESC\r\n"
			+ "  ) t\r\n"
			+ ") WHERE rnum BETWEEN ? AND ?";
		return selectRows(sql, params.toArray());
	}

	// 예약 한 건 상세. 없으면 빈 Map
	//   parked_minutes : 실제 입차 시각(없으면 예약 시작)부터 출차(없으면 지금)까지 분
	//   over_minutes   : 예약형이 종료 예정 시각을 넘긴 분
	//   paid_prepay    : 예약 때 낸 금액(payment_type 1), paid_total : 결제 합계(환불 차감)
	public HashMap<String, Object> getReservationView(String reservationId) {
		String sql =
			  "SELECT r.reservation_id, r.member_id, NVL(m.name, r.member_id) AS member_name, m.phone_number, m.vehicle_number,\r\n"
			+ "       r.seat_no, s.lot_id, DECODE(s.seat_type,'N','일반','D','장애인','E','전기차',s.seat_type) AS seat_type_label,\r\n"
			+ "       r.reservation_type, " + TYPE_LABEL + " AS type_label,\r\n"
			+ "       r.reservation_status AS status, " + STATUS_LABEL + " AS status_label,\r\n"
			+ "       TO_CHAR(r.reservation_start_time,'YYYY-MM-DD HH24:MI') AS start_text,\r\n"
			+ "       TO_CHAR(r.reservation_end_time,'YYYY-MM-DD HH24:MI')   AS end_text,\r\n"
			+ "       TO_CHAR(r.reservation_out_time,'YYYY-MM-DD HH24:MI')   AS out_text,\r\n"
			+ "       NVL(r.reservation_estimate_amount,0) AS estimate, NVL(r.reservation_deposit_amount,0) AS deposit,\r\n"
			+ "       r.flight_no, f.airport, TO_CHAR(f.schedule_datetime,'MM-DD HH24:MI') AS flight_sched, f.remark AS flight_remark,\r\n"
			+ "       TO_CHAR(r.reservation_parking_start_time,'YYYY-MM-DD HH24:MI') AS parking_start_text,\r\n"
			+ "       NVL(r.reservation_final_amount,0) AS final_amount,\r\n"
			+ "       ROUND((NVL(r.reservation_out_time, SYSDATE) - NVL(r.reservation_parking_start_time, r.reservation_start_time)) * 1440) AS parked_minutes,\r\n"
			+ "       CASE WHEN r.reservation_end_time IS NULL THEN 0\r\n"
			+ "            ELSE GREATEST(0, ROUND((NVL(r.reservation_out_time, SYSDATE) - r.reservation_end_time) * 1440)) END AS over_minutes,\r\n"
			+ "       (SELECT NVL(SUM(CASE WHEN p.payment_type = '3' THEN -p.payment_amount ELSE p.payment_amount END),0)\r\n"
			+ "        FROM icn_payment p WHERE p.reservation_id = r.reservation_id) AS paid_total,\r\n"
			+ "       (SELECT NVL(SUM(p.payment_amount),0)\r\n"
			+ "        FROM icn_payment p WHERE p.reservation_id = r.reservation_id AND p.payment_type = '1') AS paid_prepay\r\n"
			+ "FROM   icn_reservation r\r\n"
			+ "LEFT JOIN icn_member m ON m.member_id = r.member_id\r\n"
			+ "LEFT JOIN icn_seat   s ON s.seat_no   = r.seat_no\r\n"
			+ FLIGHT_JOIN
			+ "WHERE  r.reservation_id = ?";
		return selectOne(sql, reservationId);
	}

	// 예약 한 건의 결제 내역. 키 : payment_id, type_label, amount(환불은 음수), method, pay_text
	public ArrayList<HashMap<String, Object>> getPaymentList(String reservationId) {
		String sql =
			  "SELECT p.payment_id, p.payment_type,\r\n"
			+ "       DECODE(p.payment_type,'1','예약금','2','출차 결제','3','환불',p.payment_type) AS type_label,\r\n"
			+ "       CASE WHEN p.payment_type = '3' THEN -p.payment_amount ELSE p.payment_amount END AS amount,\r\n"
			+ "       p.payment_method AS method, TO_CHAR(p.payment_date,'YYYY-MM-DD HH24:MI') AS pay_text\r\n"
			+ "FROM   icn_payment p\r\n"
			+ "WHERE  p.reservation_id = ?\r\n"
			+ "ORDER BY p.payment_date, p.payment_id";
		return selectRows(sql, reservationId);
	}

	// ================================================================ 입·출차 처리

	// 오늘 처리할 예약 : 주차 중(2) 전부 + 이용 시작일이 오늘이거나 지났는데 아직 입차 안 된(1) 것. 최대 30건
	public ArrayList<HashMap<String, Object>> getGateTodayList() {
		String sql =
			  "SELECT * FROM (\r\n"
			+ "    SELECT r.reservation_id, NVL(m.name, r.member_id) AS member_name, r.seat_no,\r\n"
			+ "           r.reservation_status AS status, " + STATUS_LABEL + " AS status_label,\r\n"
			+ "           TO_CHAR(r.reservation_start_time,'MM-DD HH24:MI') AS start_text,\r\n"
			+ "           " + TYPE_LABEL + " AS type_label\r\n"
			+ "    FROM   icn_reservation r\r\n"
			+ "    LEFT JOIN icn_member m ON m.member_id = r.member_id\r\n"
			+ "    WHERE  r.reservation_status = '2'\r\n"
			+ "    OR    (r.reservation_status = '1' AND TRUNC(r.reservation_start_time) <= TRUNC(SYSDATE))\r\n"
			+ "    ORDER BY r.reservation_status DESC, r.reservation_start_time DESC\r\n"
			+ ") WHERE ROWNUM <= 30";
		return selectRows(sql);
	}

	// 입차 : 예약완료(1) → 주차 중(2) + 실제 입차 시각 기록 (자율출차형 출차 요금이 이 시각부터 계산된다)
	// 상태 조건을 WHERE 에 넣어서 두 번 눌러도 두 번 처리되지 않게 한다.
	public int gateIn(String reservationId) {
		String sql =
			  "UPDATE icn_reservation\r\n"
			+ "SET    reservation_status = '2', reservation_parking_start_time = SYSDATE\r\n"
			+ "WHERE  reservation_id = ? AND reservation_status = '1'";
		return executeUpdate(sql, reservationId);
	}

	// 출차 + 정산 : 주차 중(2) → 출차 완료(3) + 출차 시각·최종 요금 기록 + 추가 결제 한 줄(payment_type 2).
	// 예약 갱신과 결제 저장 중 하나만 성공하면 돈 기록이 어긋나므로 하나의 트랜잭션으로 묶는다.
	//   due > 0 : 추가 결제 저장,  due == 0 : 결제 행 없음 (선결제로 끝)
	public int gateOut(String reservationId, String paymentId, int finalAmount, int due, String method) {
		int result = 0;
		Connection con = null;
		PreparedStatement ps = null;
		String sqlResv =
			  "UPDATE icn_reservation\r\n"
			+ "SET    reservation_status = '3', reservation_out_time = SYSDATE, reservation_final_amount = ?\r\n"
			+ "WHERE  reservation_id = ? AND reservation_status = '2'";
		String sqlPay =
			  "INSERT INTO icn_payment\r\n"
			+ "(payment_id, payment_amount, payment_method, payment_type, payment_date, reservation_id)\r\n"
			+ "VALUES (?, ?, ?, ?, SYSDATE, ?)";
		try {
			con = DBConnection.getConnection();
			con.setAutoCommit(false);                 // 여기부터 commit 전까지는 한 묶음

			ps = con.prepareStatement(sqlResv);
			ps.setInt(1, finalAmount);
			ps.setString(2, reservationId);
			int updated = ps.executeUpdate();
			ps.close();
			if (updated != 1) {                       // 주차 중이 아니었다 → 아무것도 바꾸지 않고 종료
				con.rollback();
				return 0;
			}

			if (due > 0) {
				ps = con.prepareStatement(sqlPay);
				ps.setString(1, paymentId);
				ps.setInt(2, due);
				ps.setString(3, method);
				ps.setString(4, "2");
				ps.setString(5, reservationId);
				if (ps.executeUpdate() != 1) {
					con.rollback();
					return 0;
				}
			}

			con.commit();
			result = 1;
		} catch (Exception e) {
			try { if (con != null) con.rollback(); } catch (Exception ignore) { }
			e.printStackTrace();
			System.out.println("gateOut() 오류 : " + reservationId);
		} finally {
			try { if (con != null) con.setAutoCommit(true); } catch (Exception ignore) { }
			DBConnection.closeDB(con, ps, null);
		}
		return result;
	}

	// ================================================================ 회원 관리

	// 회원 목록의 검색 컬럼. 바인딩이 안 되므로 화이트리스트에서 고른다.
	private String memberColumn(String select) {
		if ("name".equals(select))           return "m.name";
		if ("phone_number".equals(select))   return "m.phone_number";
		if ("vehicle_number".equals(select)) return "m.vehicle_number";
		return "m.member_id";
	}

	// 회원 한 명당 예약 건수·이용 금액을 같이 뽑는다. 목록에서 한 명씩 다시 조회하면 화면당 쿼리가 수십 번 나간다.
	private static final String MEMBER_COLS =
		  "m.member_id, m.name, m.phone_number, m.email, m.vehicle_number,\r\n"
		+ "       DECODE(m.vehicle_type,'N','일반','D','장애인','E','전기차',m.vehicle_type) AS vehicle_type_label,\r\n"
		+ "       TO_CHAR(m.reg_date,'YYYY-MM-DD') AS reg_date,\r\n"
		+ "       TO_CHAR(m.exit_date,'YYYY-MM-DD') AS exit_date,\r\n"
		+ "       NVL(r.resv_cnt,0) AS resv_cnt, NVL(r.done_cnt,0) AS done_cnt, NVL(r.cancel_cnt,0) AS cancel_cnt,\r\n"
		+ "       NVL(r.parking_cnt,0) AS parking_cnt, TO_CHAR(r.last_start,'YYYY-MM-DD') AS last_use,\r\n"
		+ "       NVL(p.paid,0) AS paid";

	private static final String MEMBER_FROM =
		  "FROM   icn_member m\r\n"
		+ "LEFT JOIN (SELECT member_id, COUNT(*) AS resv_cnt,\r\n"
		+ "                  SUM(CASE WHEN reservation_status = '3' THEN 1 ELSE 0 END) AS done_cnt,\r\n"
		+ "                  SUM(CASE WHEN reservation_status = '4' THEN 1 ELSE 0 END) AS cancel_cnt,\r\n"
		+ "                  SUM(CASE WHEN reservation_status = '2' THEN 1 ELSE 0 END) AS parking_cnt,\r\n"
		+ "                  MAX(reservation_start_time) AS last_start\r\n"
		+ "           FROM   icn_reservation GROUP BY member_id) r ON r.member_id = m.member_id\r\n"
		+ "LEFT JOIN (SELECT r2.member_id,\r\n"
		+ "                  SUM(CASE WHEN p2.payment_type = '3' THEN -p2.payment_amount ELSE p2.payment_amount END) AS paid\r\n"
		+ "           FROM   icn_payment p2\r\n"
		+ "           JOIN   icn_reservation r2 ON r2.reservation_id = p2.reservation_id\r\n"
		+ "           GROUP BY r2.member_id) p ON p.member_id = m.member_id\r\n";

	public int getMemberCount(String select, String search) {
		String sql = "SELECT COUNT(*) AS cnt FROM icn_member m\r\n"
				+ "WHERE UPPER(" + memberColumn(select) + ") LIKE UPPER('%' || ? || '%')";
		HashMap<String, Object> row = selectOne(sql, search == null ? "" : search);
		return row.isEmpty() ? 0 : ((Number) row.get("cnt")).intValue();
	}

	public ArrayList<HashMap<String, Object>> getMemberList(String select, String search, int start, int end) {
		String sql =
			  "SELECT * FROM (\r\n"
			+ "  SELECT ROWNUM AS rnum, t.* FROM (\r\n"
			+ "    SELECT " + MEMBER_COLS + "\r\n"
			+ "    " + MEMBER_FROM
			+ "    WHERE UPPER(" + memberColumn(select) + ") LIKE UPPER('%' || ? || '%')\r\n"
			+ "    ORDER BY m.reg_date DESC, m.member_id\r\n"
			+ "  ) t\r\n"
			+ ") WHERE rnum BETWEEN ? AND ?";
		return selectRows(sql, search == null ? "" : search, start, end);
	}

	public HashMap<String, Object> getMemberView(String memberId) {
		String sql = "SELECT " + MEMBER_COLS + "\r\n" + MEMBER_FROM + "WHERE  m.member_id = ?";
		return selectOne(sql, memberId);
	}

	// 회원 한 명의 예약 내역 (최근 순). 키는 예약 관리 목록과 같게 맞춰 화면에서 같은 식으로 꺼내 쓴다.
	public ArrayList<HashMap<String, Object>> getMemberReservations(String memberId, int limit) {
		String sql =
			  "SELECT * FROM (\r\n"
			+ "    SELECT r.reservation_id, r.seat_no, s.lot_id,\r\n"
			+ "           " + TYPE_LABEL + " AS type_label,\r\n"
			+ "           r.reservation_status AS status, " + STATUS_LABEL + " AS status_label,\r\n"
			+ "           TO_CHAR(r.reservation_start_time,'YYYY-MM-DD HH24:MI') AS start_text,\r\n"
			+ "           TO_CHAR(r.reservation_end_time,'MM-DD HH24:MI') AS end_text,\r\n"
			+ "           TO_CHAR(r.reservation_out_time,'MM-DD HH24:MI') AS out_text,\r\n"
			+ "           r.flight_no, NVL(r.reservation_final_amount,0) AS final_amount,\r\n"
			+ "           (SELECT NVL(SUM(p.payment_amount),0) FROM icn_payment p\r\n"
			+ "            WHERE p.reservation_id = r.reservation_id AND p.payment_type = '1') AS prepaid,\r\n"
			+ "           (SELECT NVL(SUM(CASE WHEN p.payment_type = '3' THEN -p.payment_amount ELSE p.payment_amount END),0)\r\n"
			+ "            FROM icn_payment p WHERE p.reservation_id = r.reservation_id) AS paid_total\r\n"
			+ "    FROM   icn_reservation r\r\n"
			+ "    LEFT JOIN icn_seat s ON s.seat_no = r.seat_no\r\n"
			+ "    WHERE  r.member_id = ?\r\n"
			+ "    ORDER BY r.reservation_start_time DESC, r.reservation_id DESC\r\n"
			+ ") WHERE ROWNUM <= ?";
		return selectRows(sql, memberId, limit);
	}

	// 회원이 남긴 문의 (관리자 문의 내역 화면으로 이어진다)
	public ArrayList<HashMap<String, Object>> getMemberReports(String memberId, int limit) {
		String sql =
			  "SELECT * FROM (\r\n"
			+ "    SELECT r.report_id, r.title,\r\n"
			+ "           DECODE(r.report_type,'1','자리 무단점유','2','시설 파손·고장','3','차량 훼손','4','불법 주차','5','기타 문의',r.report_type) AS type_label,\r\n"
			+ "           r.report_status, DECODE(r.report_status,'1','접수','2','처리 중','3','처리 완료','4','반려',r.report_status) AS status_label,\r\n"
			+ "           TO_CHAR(r.reg_date,'YYYY-MM-DD') AS reg_date\r\n"
			+ "    FROM   icn_report r\r\n"
			+ "    WHERE  r.member_id = ?\r\n"
			+ "    ORDER BY r.reg_date DESC, r.report_id DESC\r\n"
			+ ") WHERE ROWNUM <= ?";
		return selectRows(sql, memberId, limit);
	}

	// ================================================================ 좌석·구역 현황

	// 한 구역의 좌석 전부 + 지금 걸려 있는 예약. 키 : seat_no, seat_type, seat_type_label, state(parking/reserved/free),
	//   reservation_id, status, member_name, start_text
	// "걸려 있는 예약" = 취소/출차 아닌 것 중 시작이 24시간 안이고 종료가 안 지난 것. 좌석당 하나(가장 최근 예약번호)만.
	public ArrayList<HashMap<String, Object>> getSeatStatus(String lotId) {
		String sql =
			  "SELECT s.seat_no, s.seat_type,\r\n"
			+ "       DECODE(s.seat_type,'N','일반','D','장애인','E','전기차',s.seat_type) AS seat_type_label,\r\n"
			+ "       a.reservation_id, a.reservation_status AS status, NVL(m.name, a.member_id) AS member_name,\r\n"
			+ "       TO_CHAR(a.reservation_start_time,'MM-DD HH24:MI') AS start_text,\r\n"
			+ "       CASE WHEN a.reservation_status = '2' THEN 'parking'\r\n"
			+ "            WHEN a.reservation_status = '1' THEN 'reserved'\r\n"
			+ "            ELSE 'free' END AS state\r\n"
			+ "FROM   icn_seat s\r\n"
			+ "LEFT JOIN (SELECT seat_no, MAX(reservation_id) AS reservation_id\r\n"
			+ "           FROM   icn_reservation\r\n"
			+ "           WHERE  reservation_status IN ('1','2')\r\n"
			+ "           AND    reservation_start_time <= SYSDATE + 1\r\n"
			+ "           AND   (reservation_end_time IS NULL OR reservation_end_time >= SYSDATE)\r\n"
			+ "           GROUP BY seat_no) x ON x.seat_no = s.seat_no\r\n"
			+ "LEFT JOIN icn_reservation a ON a.reservation_id = x.reservation_id\r\n"
			+ "LEFT JOIN icn_member m ON m.member_id = a.member_id\r\n"
			+ "WHERE  s.lot_id = ?\r\n"
			+ "ORDER BY s.seat_no";
		return selectRows(sql, lotId);
	}

	// ================================================================ 매출 통계 (기간 : 결제일 기준, from~to 포함)

	private static final String SALES_FROM =
		  "FROM  (SELECT p.*, CASE WHEN p.payment_type = '3' THEN -p.payment_amount ELSE p.payment_amount END AS amt\r\n"
		+ "       FROM icn_payment p) p\r\n"
		+ "LEFT JOIN icn_reservation r ON r.reservation_id = p.reservation_id\r\n";
	private static final String SALES_WHERE =
		  "WHERE p.payment_date >= TO_DATE(?,'YYYY-MM-DD') AND p.payment_date < TO_DATE(?,'YYYY-MM-DD') + 1\r\n";
	private static final String SALES_COLS =
		  "       NVL(SUM(CASE WHEN r.reservation_status = '3' THEN amt END),0)                 AS confirmed_sales,\r\n"
		+ "       NVL(SUM(CASE WHEN p.payment_type IN ('1','2') THEN p.payment_amount END),0)   AS paid_in,\r\n"
		+ "       NVL(SUM(CASE WHEN p.payment_type = '3' THEN p.payment_amount END),0)          AS refund,\r\n"
		+ "       COUNT(*) AS pay_cnt\r\n";

	// 기간 합계. 키 : confirmed_sales, paid_in, refund, pay_cnt, done_cnt
	public HashMap<String, Object> getSalesSummary(String from, String to) {
		String sql = "SELECT\r\n" + SALES_COLS
			+ "     , COUNT(DISTINCT CASE WHEN r.reservation_status = '3' THEN r.reservation_id END) AS done_cnt\r\n"
			+ SALES_FROM + SALES_WHERE;
		return selectOne(sql, from, to);
	}

	// 일별. 키 : day + SALES_COLS
	public ArrayList<HashMap<String, Object>> getSalesDaily(String from, String to) {
		String sql = "SELECT TO_CHAR(p.payment_date,'YYYY-MM-DD') AS day,\r\n" + SALES_COLS
			+ SALES_FROM + SALES_WHERE
			+ "GROUP BY TO_CHAR(p.payment_date,'YYYY-MM-DD')\r\n"
			+ "ORDER BY 1";
		return selectRows(sql, from, to);
	}

	// 구역별. 예약이 없는 결제는 구역을 알 수 없어 '(미연결)' 으로 묶인다
	public ArrayList<HashMap<String, Object>> getSalesByLot(String from, String to) {
		String sql = "SELECT NVL(s.lot_id,'(미연결)') AS lot_id,\r\n" + SALES_COLS
			+ SALES_FROM
			+ "LEFT JOIN icn_seat s ON s.seat_no = r.seat_no\r\n"
			+ SALES_WHERE
			+ "GROUP BY s.lot_id\r\n"
			+ "ORDER BY 1";
		return selectRows(sql, from, to);
	}

	// 결제수단별
	public ArrayList<HashMap<String, Object>> getSalesByMethod(String from, String to) {
		String sql = "SELECT p.payment_method AS method,\r\n" + SALES_COLS
			+ SALES_FROM + SALES_WHERE
			+ "GROUP BY p.payment_method\r\n"
			+ "ORDER BY 1";
		return selectRows(sql, from, to);
	}

	// ================================================================ 공통 도우미

	// 여러 행. 한 행 = LinkedHashMap(소문자 컬럼명 -> 값)
	private ArrayList<HashMap<String, Object>> selectRows(String sql, Object... params) {
		ArrayList<HashMap<String, Object>> rows = new ArrayList<>();
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			con = DBConnection.getConnection();
			ps  = con.prepareStatement(sql);
			for (int i = 0; i < params.length; i++) {
				ps.setObject(i + 1, params[i]);
			}
			rs = ps.executeQuery();
			ResultSetMetaData md = rs.getMetaData();
			int colCount = md.getColumnCount();
			while (rs.next()) {
				HashMap<String, Object> row = new LinkedHashMap<>();
				for (int i = 1; i <= colCount; i++) {
					row.put(md.getColumnLabel(i).toLowerCase(), rs.getObject(i));
				}
				rows.add(row);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("ManagerDao.selectRows() 오류 : " + sql);
		} finally {
			DBConnection.closeDB(con, ps, rs);
		}
		return rows;
	}

	// 한 행. 결과가 없으면 빈 Map (JSP 에서 ${kpi.x} 가 빈 값으로 나올 뿐 터지지 않는다)
	private HashMap<String, Object> selectOne(String sql, Object... params) {
		ArrayList<HashMap<String, Object>> rows = selectRows(sql, params);
		if (rows.isEmpty()) return new HashMap<>();
		return rows.get(0);
	}

	// UPDATE/DELETE 한 문장. 처리된 행 수를 돌려준다
	private int executeUpdate(String sql, Object... params) {
		int result = 0;
		Connection con = null;
		PreparedStatement ps = null;
		try {
			con = DBConnection.getConnection();
			ps  = con.prepareStatement(sql);
			for (int i = 0; i < params.length; i++) {
				ps.setObject(i + 1, params[i]);
			}
			result = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("ManagerDao.executeUpdate() 오류 : " + sql);
		} finally {
			DBConnection.closeDB(con, ps, null);
		}
		return result;
	}
}
