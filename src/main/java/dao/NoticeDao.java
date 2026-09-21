package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import common.DBConnection;
import dto.NoticeDto;

/**
 * 공지사항(icn_notice) DAO - 관리자 콘솔용. 강선구.
 *
 * 값은 전부 ? 바인딩합니다 (검색어에 작은따옴표가 들어와도 SQL 이 바뀌지 않게).
 *
 * [기본키 이야기]
 *   icn_notice 의 no 는 시퀀스가 아니라 VARCHAR2(4) 입니다. 그래서 저장할 때
 *   지금 있는 번호 중 가장 큰 값 + 1 을 'N001' 형태로 만들어 넣습니다 (nextNo).
 *   숫자가 아닌 값이 섞여 있어도 TO_NUMBER 에서 터지지 않도록 REGEXP_LIKE 로 걸러냅니다.
 *   ※ 여러 명이 같은 순간에 등록하면 번호가 겹칠 수 있는 방식입니다. 시퀀스가 정석이라
 *     테이블을 고칠 수 있게 되면 icn_notice_seq 로 바꾸는 게 맞습니다 (정규상과 협의 필요).
 */
public class NoticeDao {

	/** 목록. 중요 공지를 맨 위로, 그 다음 최신 번호 순. search 가 있으면 제목에서 찾는다. */
	public ArrayList<NoticeDto> getNoticeList(String search) {
		ArrayList<NoticeDto> dtos = new ArrayList<>();
		String sql =
			  "SELECT no, title, NVL(important,'N') AS important, attach, NVL(hit,0) AS hit, reg_id,\r\n"
			+ "       TO_CHAR(reg_date,'YYYY-MM-DD') AS reg_date\r\n"
			+ "FROM   icn_notice\r\n"
			+ "WHERE  UPPER(title) LIKE UPPER('%' || ? || '%')\r\n"
			+ "ORDER BY DECODE(NVL(important,'N'),'Y',0,1), reg_date DESC, no DESC";

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			con = DBConnection.getConnection();
			ps  = con.prepareStatement(sql);
			ps.setString(1, search == null ? "" : search);
			rs  = ps.executeQuery();
			while (rs.next()) {
				dtos.add(readRow(rs, false));
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("getNoticeList() 오류 : " + sql);
		} finally {
			DBConnection.closeDB(con, ps, rs);
		}
		return dtos;
	}

	/** 한 건. 없으면 null */
	public NoticeDto getNoticeView(String no) {
		NoticeDto dto = null;
		String sql =
			  "SELECT no, title, content, NVL(important,'N') AS important, attach, NVL(hit,0) AS hit, reg_id,\r\n"
			+ "       TO_CHAR(reg_date,'YYYY-MM-DD HH24:MI') AS reg_date\r\n"
			+ "FROM   icn_notice\r\n"
			+ "WHERE  no = ?";

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			con = DBConnection.getConnection();
			ps  = con.prepareStatement(sql);
			ps.setString(1, no);
			rs  = ps.executeQuery();
			if (rs.next()) {
				dto = readRow(rs, true);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("getNoticeView() 오류 : " + no);
		} finally {
			DBConnection.closeDB(con, ps, rs);
		}
		return dto;
	}

	public int noticeSave(NoticeDto dto) {
		String sql =
			  "INSERT INTO icn_notice (no, title, content, important, hit, reg_id, reg_date)\r\n"
			+ "VALUES (?, ?, ?, ?, 0, ?, SYSDATE)";

		int result = 0;
		Connection con = null;
		PreparedStatement ps = null;
		try {
			con = DBConnection.getConnection();
			ps  = con.prepareStatement(sql);
			ps.setString(1, nextNo(con));
			ps.setString(2, dto.getTitle());
			ps.setString(3, dto.getContent());
			ps.setString(4, dto.getImportant());
			ps.setString(5, dto.getReg_id());
			result = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("noticeSave() 오류 : " + sql);
		} finally {
			DBConnection.closeDB(con, ps, null);
		}
		return result;
	}

	public int noticeUpdate(NoticeDto dto) {
		String sql =
			  "UPDATE icn_notice\r\n"
			+ "SET    title = ?, content = ?, important = ?\r\n"
			+ "WHERE  no = ?";
		return executeUpdate(sql, dto.getTitle(), dto.getContent(), dto.getImportant(), dto.getNo());
	}

	public int noticeDelete(String no) {
		return executeUpdate("DELETE FROM icn_notice WHERE no = ?", no);
	}

	// ---------------------------------------------------------------- 도우미

	// 다음 번호. 같은 커넥션 안에서 구해야 INSERT 직전 값과 어긋나지 않는다.
	private String nextNo(Connection con) throws Exception {
		// 이미 들어 있는 번호가 'N001' 형식(정규상 화면에서 넣은 값)이라 같은 형식으로 맞춘다.
		// 'N' 뒤 숫자만 떼어 가장 큰 값 + 1. 형식이 다른 행은 걸러서 TO_NUMBER 가 터지지 않게 한다.
		String sql =
			  "SELECT 'N' || LPAD(NVL(MAX(TO_NUMBER(SUBSTR(no,2))),0) + 1, 3, '0') AS next_no\r\n"
			+ "FROM   icn_notice\r\n"
			+ "WHERE  REGEXP_LIKE(no, '^N[0-9]+$')";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) return rs.getString("next_no");
			return "N001";
		} finally {
			if (rs != null) try { rs.close(); } catch (Exception ignore) { }
			if (ps != null) try { ps.close(); } catch (Exception ignore) { }
		}
	}

	private NoticeDto readRow(ResultSet rs, boolean withContent) throws Exception {
		NoticeDto dto = new NoticeDto();
		dto.setNo(rs.getString("no"));
		dto.setTitle(rs.getString("title"));
		dto.setImportant(rs.getString("important"));
		dto.setAttach(rs.getString("attach"));
		dto.setHit(rs.getInt("hit"));
		dto.setReg_id(rs.getString("reg_id"));
		dto.setReg_date(rs.getString("reg_date"));
		if (withContent) dto.setContent(rs.getString("content"));
		return dto;
	}

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
			System.out.println("NoticeDao.executeUpdate() 오류 : " + sql);
		} finally {
			DBConnection.closeDB(con, ps, null);
		}
		return result;
	}

}
