package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import common.DBConnection;
import dto.ReportDto;

public class ReportDao {

	private static final String TYPE_LABEL =
		"DECODE(r.report_type,'1','자리 무단점유','2','시설 파손·고장','3','차량 훼손','4','불법 주차','5','기타 문의',r.report_type)";
	private static final String STATUS_LABEL =
		"DECODE(r.report_status,'1','접수','2','처리 중','3','처리 완료','4','반려',r.report_status)";

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

	private static final String LIST_ORDER =
		"ORDER BY DECODE(r.report_status,'1',1,'2',2,3), r.reg_date ASC, r.report_id ASC\r\n";

	private String searchColumn(String select) {
		if ("member_id".equals(select)) return "r.member_id";
		if ("seat_no".equals(select))   return "r.seat_no";
		return "r.title";
	}

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
		ArrayList<Object> params = new ArrayList<>();
		String sql = "SELECT COUNT(*) AS cnt\r\n" + LIST_FROM + reportWhere(select, search, status, type, params);
		return selectCount(sql, params.toArray());
	}

	public ArrayList<ReportDto> getReportList(String select, String search, String status, String type,
			int start, int end) {
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
		return selectList(sql, params.toArray());
	}

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

	public int reportSave(ReportDto dto) {
		String sql =
			  "INSERT INTO icn_report\r\n"
			+ "(report_id, report_type, report_status, title, content, member_id, seat_no, reservation_id, reg_date)\r\n"
			+ "VALUES (icn_report_seq.NEXTVAL, ?, '1', ?, ?, ?, ?, ?, SYSDATE)";
		return executeUpdate(sql, dto.getReport_type(), dto.getTitle(), dto.getContent(),
				dto.getMember_id(), dto.getSeat_no(), dto.getReservation_id());
	}

	public int updateAnswer(int reportId, String status, String answerContent, String answerId) {
		String sql =
			  "UPDATE icn_report\r\n"
			+ "SET    report_status = ?, answer_content = ?, answer_id = ?, answer_date = SYSDATE\r\n"
			+ "WHERE  report_id = ?";
		return executeUpdate(sql, status, answerContent, answerId, reportId);
	}

	public int getWaitingCount() {
		return selectCount("SELECT COUNT(*) AS cnt FROM icn_report WHERE report_status IN ('1','2')");
	}

	public ArrayList<ReportDto> getWaitingList(int limit) {
		String sql =
			  "SELECT * FROM (\r\n"
			+ "    SELECT " + LIST_COLS + "\r\n"
			+ "    " + LIST_FROM
			+ "    WHERE r.report_status IN ('1','2')\r\n"
			+ "    " + LIST_ORDER
			+ ") WHERE ROWNUM <= ?";
		return selectList(sql, limit);
	}

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

	private ArrayList<ReportDto> selectList(String sql, Object... params) {
		ArrayList<ReportDto> dtos = new ArrayList<>();
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			con = DBConnection.getConnection();
			ps  = con.prepareStatement(sql);
			bind(ps, params);
			rs  = ps.executeQuery();
			while (rs.next()) {
				dtos.add(readRow(rs, false));
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("ReportDao.selectList() 오류 : " + sql);
		} finally {
			DBConnection.closeDB(con, ps, rs);
		}
		return dtos;
	}

	private int selectCount(String sql, Object... params) {
		int cnt = 0;
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			con = DBConnection.getConnection();
			ps  = con.prepareStatement(sql);
			bind(ps, params);
			rs  = ps.executeQuery();
			if (rs.next()) cnt = rs.getInt("cnt");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("ReportDao.selectCount() 오류 : " + sql);
		} finally {
			DBConnection.closeDB(con, ps, rs);
		}
		return cnt;
	}

	private int executeUpdate(String sql, Object... params) {
		int result = 0;
		Connection con = null;
		PreparedStatement ps = null;
		try {
			con = DBConnection.getConnection();
			ps  = con.prepareStatement(sql);
			bind(ps, params);
			result = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("ReportDao.executeUpdate() 오류 : " + sql);
		} finally {
			DBConnection.closeDB(con, ps, null);
		}
		return result;
	}

	private void bind(PreparedStatement ps, Object[] params) throws Exception {
		for (int i = 0; i < params.length; i++) {
			if (params[i] == null) ps.setNull(i + 1, java.sql.Types.VARCHAR);
			else ps.setObject(i + 1, params[i]);
		}
	}

}
