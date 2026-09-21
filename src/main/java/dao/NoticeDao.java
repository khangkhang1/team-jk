package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

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

	// ================================================================
	// 아래 3개는 정규상 공지 게시판(command/notice/*, notice/*.jsp)용.
	// 2026-09-21 병합 때 ijg 에서 그대로 가져왔다.
	// 위쪽 관리자 콘솔용 메서드와 이름이 겹치지 않아(인자 수가 다름) 함께 둘 수 있다.
	// ※ select/search 를 SQL 에 문자열로 붙이고 있어 검색어에 작은따옴표가 들어오면 깨진다.
	//   위쪽 관리자용처럼 ? 바인딩으로 바꾸는 게 맞다(정규상 확인 필요).
	// ================================================================

	public int getTotalCount(String select, String search) {
		int count = 0;
		String sql = "select count(*) as count from icn_notice where " + select + " like '%" + search + "%'";
		Connection con = null; PreparedStatement ps = null; ResultSet rs = null;
		try {
			con = DBConnection.getConnection();
			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) count = rs.getInt("count");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("getTotalCount() 오류 : " + sql);
		} finally {
			DBConnection.closeDB(con, ps, rs);
		}
		return count;
	}

	/** 페이지 단위 목록. 작성자 이름을 icn_member 와 조인해서 가져온다. */
	public List<NoticeDto> getNoticeList(String select, String search, int start, int end) {
		List<NoticeDto> dtos = new ArrayList<>();
		String sql =
			  "select * from ( "
			+ "  select rownum as rnum, tbl.* from ( "
			+ "    select n.no, n.title, n.attach, n.important, m.name, "
			+ "           to_char(n.reg_date,'yyyy-MM-dd') as reg_date, n.hit "
			+ "    from icn_notice n, icn_member m "
			+ "    where n.reg_id = m.member_id "
			+ "    and n." + select + " like '%" + search + "%' "
			+ "    order by case when n.important = 'Y' then 0 else 1 end, n.no desc "
			+ "  ) tbl "
			+ ") where rnum >= " + start + " and rnum <= " + end;

		Connection con = null; PreparedStatement ps = null; ResultSet rs = null;
		try {
			con = DBConnection.getConnection();
			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				dtos.add(new NoticeDto(
					rs.getString("no"), rs.getString("title"), "",
					rs.getString("important"), rs.getString("attach"), rs.getInt("hit"),
					rs.getString("name"), rs.getString("reg_date")));
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("getNoticeList(paged) 오류 : " + sql);
		} finally {
			DBConnection.closeDB(con, ps, rs);
		}
		return dtos;
	}

	/** 다음 번호를 'N001' 형태로. 위 nextNo(con) 와 같은 일을 하지만 커넥션을 스스로 연다. */
	public String getNoticeNo() {
		String no = "";
		String sql = "select nvl(max(no),'N000') as no from icn_notice";
		Connection con = null; PreparedStatement ps = null; ResultSet rs = null;
		try {
			con = DBConnection.getConnection();
			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) {
				no = rs.getString("no").substring(1);
				no = new DecimalFormat("N000").format(Integer.parseInt(no) + 1);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("getNoticeNo() 오류 : " + sql);
		} finally {
			DBConnection.closeDB(con, ps, rs);
		}
		return no;
	}
}
