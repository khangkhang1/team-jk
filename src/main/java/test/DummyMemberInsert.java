package test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Random;

import common.DBConnection;
import dao.MemberDao;
import dto.MemberDto;

public class DummyMemberInsert {

	private static final int    COUNT    = 100;
	private static final String PASSWORD = "1234";

	private static final String ID_PATTERN    = "^(?=.*[a-z])[a-zA-Z0-9!@#$%^&*_.-]{4,20}$";
	private static final String PHONE_PATTERN = "^010-?\\d{4}-?\\d{4}$";
	private static final String EMAIL_PATTERN = "^[a-zA-Z0-9!@#$%^&*_.-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

	private static final String[][] SURNAMES = {
		{"김","kim"},{"김","kim"},{"김","kim"},{"이","lee"},{"이","lee"},{"박","park"},{"박","park"},
		{"최","choi"},{"정","jung"},{"강","kang"},{"조","cho"},{"윤","yoon"},{"장","jang"},{"임","lim"},
		{"한","han"},{"오","oh"},{"서","seo"},{"신","shin"},{"권","kwon"},{"황","hwang"},{"안","ahn"},
		{"송","song"},{"류","ryu"},{"홍","hong"}
	};
	private static final String[] GIVEN = {
		"민준","서준","도윤","예준","시우","하준","지호","주원","지후","준우","현우","건우","우진","선우","태윤",
		"서연","서윤","지우","서현","민서","하은","하윤","윤서","지민","채원","수아","지아","지윤","은서","다은",
		"예은","유진","수빈","민지","지원","은우","시윤","하린","예린","소윤","성민","영호","미경","정훈","혜진"
	};
	private static final String PLATE_CHARS = "가나다라마거너더러머버서어저고노도로모보소오조구누두루무부수우주";

	public static void main(String[] args) throws Exception {
		if (args.length > 0 && args[0].equals("delete")) {
			delete();
			return;
		}
		if (args.length > 0 && args[0].equals("password")) {
			resetPassword();
			return;
		}

		Random r = new Random(20260922);
		MemberDao dao = MemberDao.getdao();
		String hashed = dao.encryptSHA256(PASSWORD);

		int ok = 0, tries = 0;
		while (ok < COUNT && tries < COUNT * 20) {
			tries++;
			String[] sur = SURNAMES[r.nextInt(SURNAMES.length)];
			String name  = sur[0] + GIVEN[r.nextInt(GIVEN.length)];
			String id    = sur[1] + (char) ('a' + r.nextInt(26)) + (char) ('a' + r.nextInt(26)) + (10 + r.nextInt(90));
			String phone = String.format("010-0000-%04d", ok + 1);
			String email = id + "@example.com";

			if (!id.matches(ID_PATTERN) || !phone.matches(PHONE_PATTERN) || !email.matches(EMAIL_PATTERN)) continue;
			if (dao.checkId(id) > 0) continue;

			MemberDto dto = new MemberDto();
			dto.setMember_id(id);
			dto.setName(name);
			dto.setPassword(hashed);
			dto.setPhone_number(phone);
			dto.setEmail(email);
			dto.setVehicle_number(plate(r));
			dto.setVehicle_type(vehicleType(r));

			if (dao.memberSave(dto) == 1) {
				spreadRegDate(id, 1 + r.nextInt(120), r.nextInt(24 * 60));
				ok++;
			}
		}
		System.out.println("더미 회원 " + ok + "명 등록 (비밀번호 " + PASSWORD + ")");
	}

	private static String plate(Random r) {
		String front = r.nextInt(10) < 6 ? String.valueOf(100 + r.nextInt(900)) : String.valueOf(10 + r.nextInt(90));
		return front + PLATE_CHARS.charAt(r.nextInt(PLATE_CHARS.length())) + " " + (1000 + r.nextInt(9000));
	}

	private static String vehicleType(Random r) {
		int n = r.nextInt(100);
		if (n < 15) return "E";
		if (n < 20) return "D";
		return "N";
	}

	private static void spreadRegDate(String id, int daysAgo, int minutes) throws Exception {
		String sql = "UPDATE icn_member SET reg_date = TRUNC(SYSDATE) - ? + ? / 1440 WHERE member_id = ?";
		try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, daysAgo);
			ps.setInt(2, minutes);
			ps.setString(3, id);
			ps.executeUpdate();
		}
	}

	private static void resetPassword() throws Exception {
		String sql = "UPDATE icn_member SET password = ? WHERE email LIKE '%@example.com'";
		try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, MemberDao.getdao().encryptSHA256(PASSWORD));
			System.out.println("더미 회원 " + ps.executeUpdate() + "명 비밀번호를 " + PASSWORD + " 로 변경");
		}
	}

	private static void delete() throws Exception {
		String sql = "DELETE FROM icn_member WHERE email LIKE '%@example.com'";
		try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
			System.out.println("더미 회원 " + ps.executeUpdate() + "명 삭제");
		}
	}

}
