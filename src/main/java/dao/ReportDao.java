package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import common.DBConnection;
import dto.ReportDto;

/**
 * 신고(icn_report) DAO - 강선구 담당.
 *
 * 값은 전부 ? 바인딩한다. 검색어·상태처럼 화면에서 들어온 값을 문자열로 이어붙이면
 * 작은따옴표 하나로 SQL 이 바뀔 수 있다 (SQL 인젝션). 컬럼명은 바인딩이 안 되므로 화이트리스트로 고른다.
 *
 * 상태 코드 : report_status 1 접수 / 2 처리 중 / 3 처리 완료 / 4 반려
 * 유형 코드 : report_type   1 자리 무단점유 / 2 시설 파손·고장 / 3 차량 훼손 / 4 불법 주차 / 5 기타
 */
public class ReportDao {

	// 목록·상세에서 똑같이 쓰는 라벨 변환. 화면마다 if 문으로 한글을 붙이지 않으려고 SQL 에서 만든다.
	private static final String TYPE_LABEL =
		"DECODE(r.report_type,'1','자리 무단점유','2','시설 파손·고장','3','차량 훼손','4','불법 주차','5','기타',r.report_type)";
	private static final String STATUS_LABEL =
		"DECODE(r.report_status,'1','접수','2','처리 중','3','처리 완료','4','반려',r.report_status)";

	// 목록에 필요한 컬럼. 신고 본문(content)은 목록에서 안 쓰므로 빼둔다 (2000자를 행마다 읽을 이유가 없다)
	private static final String LIST_COLS =
		  "r.report_id, r.report_type, " + TYPE_LABEL + " AS type_label,\r\n"
		+ "       r.report_status, " + STATUS_LABEL + " AS status_label,\r\n"
		+ "       r.title, r.member_id, NVL(m.name, NVL(r.member_id,'(알 수 없음)')) AS member_name,\r\n"
		+ "       r.seat_no, s.lot_id, r.reservation_id,\r\n"
		+ "       TO_CHAR(r.reg_date,'YYYY-MM-DD HH24:MI') AS reg_date,\r\n"
		+ "       TRUNC(SYSDATE) - TRUNC(r.reg_date) AS elapsed_days,\r\n"
		+ "       r.answer_id, TO_CHAR(r.answer_date,'YYYY-MM-DD HH24:MI') AS answer_date";

	private static final String LIST_FROM =
		  "FROM   icn_report r\r\n"
		+ "LEFT JOIN icn_member m ON m.member_id = r.member_id\r\n"
		+ "LEFT JOIN icn_seat   s ON s.seat_no   = r.seat_no\r\n";

	// 처리해야 할 것(접수 → 처리 중)이 위로 오고, 같은 상태 안에서는 오래된 신고가 위로 온다.
	// 관리자 화면은 "읽는 목록"이 아니라 "처리하는 대기열"이라 최신순보다 이 순서가 맞다.
	private static final String LIST_ORDER =
		"ORDER BY DECODE(r.report_status,'1',1,'2',2,3), r.reg_date ASC, r.report_id ASC\r\n";

	// 검색 컬럼은 바인딩이 안 되므로 화이트리스트에서 고른다. 목록에 없는 값이면 제목 검색.
	private String searchColumn(String select) {
		if ("member_id".equals(select)) return "r.member_id";
		if ("seat_no".equals(select))   return "r.seat_no";
		return "r.title";
	}

	// 목록과 건수가 반드시 같은 조건을 쓰도록 WHERE 를 한 곳에서 만든다 (페이지 수가 어긋나는 것을 막는다)
	private String reportWhere(String select, String search, String status, String type, ArrayList<Object> params) {
		String where = "WHERE UPPER(" + searchColumn(select) + ") LIKE UPPER('%' || ? || '%')\r\n";
		params.add(search == null ? "" : search);
		if (status != null && !status.equals("")) {
			where += "AND   r.report_status = ?\r\n";
			params.add(status);
		}
		if (type != null && !type.equals("")) {
			where += "AND   r.report_type = ?\r\n";
			params.add(type);
		}
		return where;
	}

	public int getReportCount(String select, String search, String status, String type) {
		int cnt = 0;
		ArrayList<Object> params = new ArrayList<>();
		String sql = "SELECT COUNT(*) AS cnt\r\n" + LIST_FROM + reportWhere(select, search, status, type, params);

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			con = DBConnection.getConnection();
			ps  = con.prepareStatement(sql);
			bind(ps, params.toArray());
			rs  = ps.executeQuery();
			if (rs.next()) cnt = rs.getInt("cnt");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("getReportCount() 오류 : " + sql);
		} finally {
			DBConnection.closeDB(con, ps, rs);
		}
		return cnt;
	}

	/** 신고 목록 한 페이지 (rnum start~end). 오라클 11g 라 LIMIT/OFFSET 이 없어 ROWNUM 을 두 겹으로 쓴다. */
	public ArrayList<ReportDto> getReportList(String select, String search, String status, String type,
			int start, int end) {
		ArrayList<ReportDto> dtos = new ArrayList<>();
		ArrayList<Object> params = new ArrayList<>();
		String where = reportWhere(select, search, status, type, params);
		params.add(start);
		params.add(end);

		String sql =
			  "SELECT * FROM (\r\n"
			+ "  SELECT ROWNUM AS rnum, t.* FROM (\r\n"
			+ "    SELECT " + LIST_COLS + "\r\n"
			+ "    " + LIST_FROM
			+ "    " + where
			+ "    " + LIST_ORDER
			+ "  ) t\r\n"
			+ ") WHERE rnum BETWEEN ? AND ?";

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			con = DBConnection.getConnection();
			ps  = con.prepareStatement(sql);
			bind(ps, params.toArray());
			rs  = ps.executeQuery();
			while (rs.next()) {
				dtos.add(readRow(rs, false));
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("getReportList() 오류 : " + sql);
		} finally {
			DBConnection.closeDB(con, ps, rs);
		}
		return dtos;
	}

	/** 신고 한 건 상세. 없으면 null (화면에서 "없는 신고번호" 안내) */
	public ReportDto getReportView(int reportId) {
		ReportDto dto = null;
		String sql =
			  "SELECT " + LIST_COLS + ",\r\n"
			+ "       r.content, r.answer_content, m.phone_number\r\n"
			+ LIST_FROM
			+ "WHERE  r.report_id = ?";

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			con = DBConnection.getConnection();
			ps  = con.prepareStatement(sql);
			ps.setInt(1, reportId);
			rs  = ps.executeQuery();
			if (rs.next()) {
				dto = readRow(rs, true);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("getReportView() 오류 : " + sql);
		} finally {
			DBConnection.closeDB(con, ps, rs);
		}
		return dto;
	}

	/**
	 * 처리 상태 + 처리 내용 저장. 처리한 관리자 ID 와 일시를 같이 남긴다.
	 * 「누가 언제 무엇을 했는가」가 남지 않으면 전화로 처리하던 때와 다를 게 없다 (対応履歴).
	 */
	public int updateAnswer(int reportId, String status, String answerContent, String answerId) {
		int result = 0;
		String sql =
			  "UPDATE icn_report\r\n"
			+ "SET    report_status = ?, answer_content = ?, answer_id = ?, answer_date = SYSDATE\r\n"
			+ "WHERE  report_id = ?";

		Connection con = null;
		PreparedStatement ps = null;
		try {
			con = DBConnection.getConnection();
			ps  = con.prepareStatement(sql);
			ps.setString(1, status);
			ps.setString(2, answerContent);
			ps.setString(3, answerId);
			ps.setInt(4, reportId);
			result = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("updateAnswer() 오류 : " + reportId);
		} finally {
			DBConnection.closeDB(con, ps, null);
		}
		return result;
	}

	/** 아직 안 끝난 신고(접수 + 처리 중) 건수. 사이드 메뉴 배지에 쓴다 - 다른 화면을 보고 있어도 눈에 띄게. */
	public int getWaitingCount() {
		int cnt = 0;
		String sql = "SELECT COUNT(*) AS cnt FROM icn_report WHERE report_status IN ('1','2')";

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			con = DBConnection.getConnection();
			ps  = con.prepareStatement(sql);
			rs  = ps.executeQuery();
			if (rs.next()) cnt = rs.getInt("cnt");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("getWaitingCount() 오류");
		} finally {
			DBConnection.closeDB(con, ps, rs);
		}
		return cnt;
	}

	/** 대시보드용 : 아직 안 끝난 신고 중 오래된 순 limit 건 */
	public ArrayList<ReportDto> getWaitingList(int limit) {
		ArrayList<ReportDto> dtos = new ArrayList<>();
		String sql =
			  "SELECT * FROM (\r\n"
			+ "    SELECT " + LIST_COLS + "\r\n"
			+ "    " + LIST_FROM
			+ "    WHERE r.report_status IN ('1','2')\r\n"
			+ "    " + LIST_ORDER
			+ ") WHERE ROWNUM <= ?";

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			con = DBConnection.getConnection();
			ps  = con.prepareStatement(sql);
			ps.setInt(1, limit);
			rs  = ps.executeQuery();
			while (rs.next()) {
				dtos.add(readRow(rs, false));
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("getWaitingList() 오류 : " + sql);
		} finally {
			DBConnection.closeDB(con, ps, rs);
		}
		return dtos;
	}

	// ---------------------------------------------------------------- 공통 도우미

	// 목록·상세가 같은 컬럼을 읽으므로 한 곳에서 DTO 로 옮긴다 (컬럼이 늘 때 한 군데만 고치면 된다)
	private ReportDto readRow(ResultSet rs, boolean withContent) throws Exception {
		ReportDto dto = new ReportDto();
		dto.setReport_id(rs.getInt("report_id"));
		dto.setReport_type(rs.getString("report_type"));
		dto.setType_label(rs.getString("type_label"));
		dto.setReport_status(rs.getString("report_status"));
		dto.setStatus_label(rs.getString("status_label"));
		dto.setTitle(rs.getString("title"));
		dto.setMember_id(rs.getString("member_id"));
		dto.setMember_name(rs.getString("member_name"));
		dto.setSeat_no(rs.getString("seat_no"));
		dto.setLot_id(rs.getString("lot_id"));
		dto.setReservation_id(rs.getString("reservation_id"));
		dto.setReg_date(rs.getString("reg_date"));
		dto.setElapsed_days(rs.getInt("elapsed_days"));
		dto.setAnswer_id(rs.getString("answer_id"));
		dto.setAnswer_date(rs.getString("answer_date"));
		if (withContent) {
			dto.setContent(rs.getString("content"));
			dto.setAnswer_content(rs.getString("answer_content"));
			dto.setPhone_number(rs.getString("phone_number"));
		}
		return dto;
	}

	private void bind(PreparedStatement ps, Object[] params) throws Exception {
		for (int i = 0; i < params.length; i++) {
			ps.setObject(i + 1, params[i]);
		}
	}

}
