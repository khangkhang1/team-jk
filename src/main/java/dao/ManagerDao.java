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
 * 관리자 대시보드 집계 DAO - 강선구 담당.
 *
 * 테이블 한 개를 그대로 읽는 게 아니라 여러 테이블을 합쳐 "숫자"만 뽑는 집계 전용이라
 * DTO 클래스를 따로 만들지 않고 한 행을 HashMap(컬럼명 -> 값) 으로 돌려준다.
 * JSP 에서는 ${row.lot_id} 처럼 DTO 와 똑같이 꺼내 쓸 수 있다.
 *
 * 값은 전부 ? 바인딩. 관리자 화면은 기간·구역 같은 조건이 파라미터로 들어오므로
 * 문자열 이어붙이기(수업 방식) 대신 PreparedStatement 바인딩을 쓴다 (SQL 인젝션 방지).
 *
 * 매출 정의 (오윤섭 결제 테이블 기준)
 *   - 확정 매출 : 출차 완료(reservation_status='3') 된 예약의 결제 합계. 환불(payment_type='3')은 차감
 *   - 받아둔 예약금 : 아직 출차/취소되지 않은 예약(status 1,2)의 예약금 결제(payment_type='1')
 *   → 예약금은 맡아둔 돈(前受金)이지 매출이 아니므로 둘을 분리한다.
 * ※ 출차 결제 기능이 붙어 reservation_final_amount 컬럼이 생기면 확정 매출 SQL 을 그 컬럼 기준으로 바꾼다.
 */
public class ManagerDao {

	// 상단 지표 4개를 한 번에. 키 : today_cnt, yesterday_cnt, parking_cnt, waiting_cnt,
	//                          this_month, last_month, deposit_sum, deposit_cnt
	public HashMap<String, Object> getKpi() {
		HashMap<String, Object> kpi = new HashMap<>();

		// 오늘 / 어제 이용 예약 건수 (예약 시작 시각 기준, 취소 제외)
		kpi.putAll(selectOne(
			  "SELECT NVL(SUM(CASE WHEN TRUNC(reservation_start_time) = TRUNC(SYSDATE)     THEN 1 ELSE 0 END),0) AS today_cnt,\r\n"
			+ "       NVL(SUM(CASE WHEN TRUNC(reservation_start_time) = TRUNC(SYSDATE) - 1 THEN 1 ELSE 0 END),0) AS yesterday_cnt\r\n"
			+ "FROM   icn_reservation\r\n"
			+ "WHERE  reservation_status <> '4'"));

		// 지금 주차 중 / 아직 입차 전인 예약
		kpi.putAll(selectOne(
			  "SELECT NVL(SUM(CASE WHEN reservation_status = '2' THEN 1 ELSE 0 END),0) AS parking_cnt,\r\n"
			+ "       NVL(SUM(CASE WHEN reservation_status = '1' AND reservation_start_time >= SYSDATE THEN 1 ELSE 0 END),0) AS waiting_cnt\r\n"
			+ "FROM   icn_reservation"));

		// 확정 매출 : 이번 달 / 지난 달
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

		// 받아둔 예약금 (출차 전 예약의 예약금)
		kpi.putAll(selectOne(
			  "SELECT NVL(SUM(p.payment_amount),0) AS deposit_sum, COUNT(*) AS deposit_cnt\r\n"
			+ "FROM   icn_payment p\r\n"
			+ "JOIN   icn_reservation r ON r.reservation_id = p.reservation_id\r\n"
			+ "WHERE  p.payment_type = '1'\r\n"
			+ "AND    r.reservation_status IN ('1','2')"));

		return kpi;
	}

	// 최근 N일 일별 예약 건수 + 입금액. 예약이 없는 날도 0 으로 나오게 날짜 표(CONNECT BY)에 붙인다.
	// 키 : day (YYYY-MM-DD), day_label (MM-DD), resv_cnt, pay_amt
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
	// "사용 중" = 취소/출차 아닌 예약 중 시작시각이 지났고 종료시각이 안 지난 것 (자유출차형은 종료시각이 없으니 출차 전까지)
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

	// 최근 N일 동안 한 번도 예약되지 않은 좌석 수, 구역별. 키 : lot_id, total_cnt, unsold_cnt, unsold_rate
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

	// 최근 예약 N건 (이용 시작 시각 역순). 키 : reservation_id, member_name, seat_no, type_label,
	//                                       status, status_label, start_text, deposit
	public ArrayList<HashMap<String, Object>> getRecentReservations(int limit) {
		String sql =
			  "SELECT * FROM (\r\n"
			+ "    SELECT r.reservation_id, NVL(m.name, r.member_id) AS member_name, r.seat_no,\r\n"
			+ "           DECODE(r.reservation_type,'1','예약형','2','자유출차형',r.reservation_type) AS type_label,\r\n"
			+ "           r.reservation_status AS status,\r\n"
			+ "           DECODE(r.reservation_status,'1','예약완료','2','주차 중','3','출차 완료','4','취소',r.reservation_status) AS status_label,\r\n"
			+ "           TO_CHAR(r.reservation_start_time,'MM-DD HH24:MI') AS start_text,\r\n"
			+ "           NVL(r.reservation_deposit_amount,0) AS deposit\r\n"
			+ "    FROM   icn_reservation r\r\n"
			+ "    LEFT JOIN icn_member m ON m.member_id = r.member_id\r\n"
			+ "    ORDER BY r.reservation_start_time DESC, r.reservation_id DESC\r\n"
			+ ") WHERE ROWNUM <= ?";
		return selectRows(sql, limit);
	}

	// 데이터 점검. 키 : dup_payment(같은 예약에 같은 종류 결제 2건 이상), orphan_payment(예약 없는 결제),
	//                  resv_no_payment(결제 없는 예약), overdue_resv(종료시각 지났는데 아직 예약완료 상태 = 노쇼 의심)
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

	// ---------------------------------------------------------------- 공통 조회 도우미

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
}
