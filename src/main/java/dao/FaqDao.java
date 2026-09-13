package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import common.DBConnection;
import dto.FaqDto;

public class FaqDao {
	Connection con = null;
	PreparedStatement ps = null;
	ResultSet rs = null;

	public ArrayList<FaqDto> getFaqList(String searchCategory, boolean isAdmin) {
		ArrayList<FaqDto> dtos = new ArrayList<>();

		String sql = "SELECT faq_id, category, question, answer, sort_no, use_yn, hit, reg_id,\r\n"
				+ "       TO_CHAR(reg_date, 'yy-MM-dd') AS reg_date\r\n"
				+ "FROM   icn_faq\r\n"
				+ "WHERE  1 = 1\r\n";

		if (!isAdmin) {
			sql += "AND    use_yn = 'Y'\r\n";
		}
		if (searchCategory != null && !searchCategory.equals("")) {
			sql += "AND    category = '" + searchCategory + "'\r\n";
		}
		sql += "ORDER BY sort_no, faq_id";

		try {
			con = DBConnection.getConnection();
			ps  = con.prepareStatement(sql);
			rs  = ps.executeQuery();

			while (rs.next()) {
				int    faq_id   = rs.getInt("faq_id");
				int    sort_no  = rs.getInt("sort_no");
				int    hit      = rs.getInt("hit");
				String category = rs.getString("category");
				String question = rs.getString("question");
				String answer   = rs.getString("answer");
				String use_yn   = rs.getString("use_yn");
				String reg_id   = rs.getString("reg_id");
				String reg_date = rs.getString("reg_date");

				FaqDto dto = new FaqDto(faq_id, sort_no, hit, category, question, answer, use_yn, reg_id, reg_date);
				dtos.add(dto);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("getFaqList() 오류 : " + sql);
		} finally {
			DBConnection.closeDB(con, ps, rs);
		}

		return dtos;
	}

	// FAQ 한 건 조회 (수정 화면에서 기존 값을 채울 때)
	public FaqDto getFaqView(int faq_id) {
		FaqDto dto = null;
		String sql = "SELECT faq_id, category, question, answer, sort_no, use_yn, hit, reg_id,\r\n"
				+ "       TO_CHAR(reg_date, 'yy-MM-dd') AS reg_date\r\n"
				+ "FROM   icn_faq\r\n"
				+ "WHERE  faq_id = " + faq_id;

		try {
			con = DBConnection.getConnection();
			ps  = con.prepareStatement(sql);
			rs  = ps.executeQuery();

			if (rs.next()) {
				int    sort_no  = rs.getInt("sort_no");
				int    hit      = rs.getInt("hit");
				String category = rs.getString("category");
				String question = rs.getString("question");
				String answer   = rs.getString("answer");
				String use_yn   = rs.getString("use_yn");
				String reg_id   = rs.getString("reg_id");
				String reg_date = rs.getString("reg_date");

				dto = new FaqDto(faq_id, sort_no, hit, category, question, answer, use_yn, reg_id, reg_date);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("getFaqView() 오류 : " + sql);
		} finally {
			DBConnection.closeDB(con, ps, rs);
		}

		return dto;
	}

	// FAQ 등록
	public int faqSave(FaqDto dto) {
		int result = 0;
		String sql = "INSERT INTO icn_faq\r\n"
				+ "(faq_id, category, question, answer, sort_no, use_yn, reg_id)\r\n"
				+ "VALUES\r\n"
				+ "(icn_faq_seq.NEXTVAL, '" + dto.getCategory() + "', '" + dto.getQuestion() + "',\r\n"
				+ " '" + dto.getAnswer() + "', " + dto.getSort_no() + ", '" + dto.getUse_yn() + "', '" + dto.getReg_id() + "')";

		try {
			con = DBConnection.getConnection();
			ps  = con.prepareStatement(sql);
			result = ps.executeUpdate();     // INSERT/UPDATE/DELETE 는 executeUpdate. 처리된 행 수가 돌아온다
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("faqSave() 오류 : " + sql);
		} finally {
			DBConnection.closeDB(con, ps, rs);
		}

		return result;
	}

	// FAQ 수정
	public int faqUpdate(FaqDto dto) {
		int result = 0;
		String sql = "UPDATE icn_faq\r\n"
				+ "SET    category = '" + dto.getCategory() + "',\r\n"
				+ "       question = '" + dto.getQuestion() + "',\r\n"
				+ "       answer   = '" + dto.getAnswer() + "',\r\n"
				+ "       sort_no  = " + dto.getSort_no() + ",\r\n"
				+ "       use_yn   = '" + dto.getUse_yn() + "'\r\n"
				+ "WHERE  faq_id = " + dto.getFaq_id();

		try {
			con = DBConnection.getConnection();
			ps  = con.prepareStatement(sql);
			result = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("faqUpdate() 오류 : " + sql);
		} finally {
			DBConnection.closeDB(con, ps, rs);
		}

		return result;
	}

	// FAQ 삭제
	public int faqDelete(int faq_id) {
		int result = 0;
		String sql = "DELETE FROM icn_faq\r\n"
				+ "WHERE  faq_id = " + faq_id;

		try {
			con = DBConnection.getConnection();
			ps  = con.prepareStatement(sql);
			result = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("faqDelete() 오류 : " + sql);
		} finally {
			DBConnection.closeDB(con, ps, rs);
		}

		return result;
	}
}
