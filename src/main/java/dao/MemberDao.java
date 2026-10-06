package dao;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

import common.DBConnection;
import dto.MemberDto;

/** 회원 가입과 인증에 필요한 DB 접근을 담당한다. */
public class MemberDao {
	private MemberDao() {
	};

	private static MemberDao dao = new MemberDao();

	public static MemberDao getdao() {
		return dao;
	}

	Connection con = null;
	LogPreparedStatement ps = null;
	ResultSet rs = null;

	public synchronized int checkId(String member_id) {
		int count = 0;
		String sql = "select count(*) as count from icn_member where member_id=?";
		try {
			con = DBConnection.getConnection();
			ps = new LogPreparedStatement(con, sql);
			ps.setString(1, member_id);
			rs = ps.executeQuery();
			if (rs.next()) {
				count = rs.getInt("count");
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error: " + ps.toString());
		} finally {
			DBConnection.closeDB(con, ps, rs);
		}
		return count;
	}

	public String encryptSHA256(String value) throws NoSuchAlgorithmException {
		String encryptData = "";

		MessageDigest sha = MessageDigest.getInstance("SHA-256");
		sha.update(value.getBytes());

		byte[] digest = sha.digest();
		for (int i = 0; i < digest.length; i++) {
			encryptData += Integer.toHexString(digest[i] & 0xFF).toUpperCase();
		}
		return encryptData;
	}

	public synchronized int getCheckPassword(String member_id, String password) {
		int count = 0;
		String sql = "select count(*) as count from icn_member where member_id=? and  password=?";
		try {
			con = DBConnection.getConnection();
			ps = new LogPreparedStatement(con, sql);
			ps.setString(1, member_id);
			ps.setString(2, password);
			rs = ps.executeQuery();
			if (rs.next()) {
				count = rs.getInt("count");
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error: " + ps.toString());
		} finally {
			DBConnection.closeDB(con, ps, rs);
		}
		return count;
	}

	public synchronized String getLoginName(String member_id, String password) {
		String name = "";
		String sql = "select name from icn_member where member_id=? and password=? and exit_date is null";
		try {
			con = DBConnection.getConnection();
			ps = new LogPreparedStatement(con, sql);
			ps.setString(1, member_id);
			ps.setString(2, password);
			rs = ps.executeQuery();
			if (rs.next()) {
				name = rs.getString("name");
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error: " + ps.toString());
		} finally {
			DBConnection.closeDB(con, ps, rs);
		}
		return name;
	}

	public synchronized MemberDto getMemberInfo(String member_id) {
		MemberDto dto = null;
		String sql = "select name,password,phone_number,email,vehicle_number,vehicle_type,reg_date,update_date,exit_date from icn_member where member_id=?";
		try {
			con = DBConnection.getConnection();
			ps = new LogPreparedStatement(con, sql);
			ps.setString(1, member_id);
			rs = ps.executeQuery();
			if (rs.next()) {
				dto = new MemberDto();
				dto.setMember_id(member_id);
				dto.setName(rs.getString("name"));
				// dto.setPassword(rs.getString("password"));
				dto.setPhone_number(rs.getString("phone_number"));
				dto.setEmail(rs.getString("email"));
				dto.setVehicle_number(rs.getString("vehicle_number"));
				dto.setVehicle_type(rs.getString("vehicle_type"));
				dto.setReg_date(rs.getTimestamp("reg_date"));
				Timestamp update_date = rs.getTimestamp("update_date");
				Timestamp exit_date = rs.getTimestamp("exit_date");
				dto.setUpdate_date(update_date);
				dto.setExit_date(exit_date);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error: " + ps.toString());
		} finally {
			DBConnection.closeDB(con, ps, rs);
		}
		return dto;
	}

	public synchronized int memberPasswordUpdate(String member_id, String password) {
		int result = 0;
		String sql = "update icn_member set password=? where member_id=?";
		try {
			con = DBConnection.getConnection();
			ps = new LogPreparedStatement(con, sql);
			ps.setString(1, password);
			ps.setString(2, member_id);
			result = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error: " + ps.toString());
		} finally {
			DBConnection.closeDB(con, ps, rs);
		}
		return result;
	}

	public synchronized String findActiveIdByEmail(String email) {
		String id = null;
		String sql = "select member_id from icn_member where email=? "
				+ "and exit_date is null";
		try {
			con = DBConnection.getConnection();
			ps = new LogPreparedStatement(con, sql);
			ps.setString(1, email);
			rs = ps.executeQuery();
			if (rs.next()) {
				id = rs.getString("member_id");
				if (rs.next()) throw new IllegalStateException("이메일에 연결된 활성 계정이 여러 개입니다.");
			}
		} catch (Exception e) {
			throw new IllegalStateException("아이디 조회에 실패했습니다.", e);
		} finally {
			DBConnection.closeDB(con, ps, rs);
			con = null;
			ps = null;
			rs = null;
		}
		return id;
	}

	public synchronized boolean hasActiveAccount(String memberId, String email) {
		String sql = "select 1 from icn_member where member_id=? and email=? and exit_date is null";
		try {
			con = DBConnection.getConnection();
			ps = new LogPreparedStatement(con, sql);
			ps.setString(1, memberId);
			ps.setString(2, email);
			rs = ps.executeQuery();
			return rs.next();
		} catch (Exception e) {
			throw new IllegalStateException("계정 확인에 실패했습니다.", e);
		} finally {
			DBConnection.closeDB(con, ps, rs);
			con = null;
			ps = null;
			rs = null;
		}
	}

	public synchronized int resetPasswordForEmail(String memberId, String email, String encryptedPassword) {
		String sql = "update icn_member set password=?, update_date=sysdate "
				+ "where member_id=? and email=? and exit_date is null";
		try {
			con = DBConnection.getConnection();
			ps = new LogPreparedStatement(con, sql);
			ps.setString(1, encryptedPassword);
			ps.setString(2, memberId);
			ps.setString(3, email);
			return ps.executeUpdate();
		} catch (Exception e) {
			throw new IllegalStateException("비밀번호 재설정에 실패했습니다.", e);
		} finally {
			DBConnection.closeDB(con, ps, rs);
			con = null;
			ps = null;
			rs = null;
		}
	}

	public synchronized int memberSave(MemberDto dto) {
		int result = 0;
		String sql = "insert into icn_member (member_id,name,password,phone_number,email,vehicle_number,vehicle_type) values (?,?,?,?,?,?,?)";
		try {
			con = DBConnection.getConnection();
			ps = new LogPreparedStatement(con, sql);
			ps.setString(1, dto.getMember_id());
			ps.setString(2, dto.getName());
			ps.setString(3, dto.getPassword());
			ps.setString(4, dto.getPhone_number());
			ps.setString(5, dto.getEmail());
			ps.setString(6, dto.getVehicle_number());
			ps.setString(7, dto.getVehicle_type());
			result = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error: " + ps.toString());
		} finally {
			DBConnection.closeDB(con, ps, rs);
		}
		return result;
	}

	public synchronized int memberUpdate(MemberDto dto) {
		int result = 0;
		String sql = "update icn_member set name=?,phone_number=?,email=?,vehicle_number=?,vehicle_type=?,update_date=sysdate where member_id=? ";
		try {
			con = DBConnection.getConnection();
			ps = new LogPreparedStatement(con, sql);
			ps.setString(1, dto.getName());
			ps.setString(2, dto.getPhone_number());
			ps.setString(3, dto.getEmail());
			ps.setString(4, dto.getVehicle_number());
			ps.setString(5, dto.getVehicle_type());
			ps.setString(6, dto.getMember_id());
			result = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error: " + ps.toString());
		} finally {
			DBConnection.closeDB(con, ps, rs);
		}
		return result;
	}

	public synchronized int memberExit(String id) {
		int result = 0;
		String sql = "update icn_member set exit_date=sysdate where member_id=?";
		try {
			con = DBConnection.getConnection();
			ps = new LogPreparedStatement(con, sql);
			ps.setString(1, id);
			result = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error: " + ps.toString());
		} finally {
			DBConnection.closeDB(con, ps, rs);
		}
		return result;
	}

	public synchronized List<Map<String, Object>> getReservationInfo(String memberId) {
		List<Map<String, Object>> reservations = new ArrayList<>();
		String sql = "select r.reservation_id, r.reservation_status, r.reservation_type, r.seat_no, r.flight_no, "
				+ "to_char(r.reservation_start_time, 'YYYY-MM-DD HH24:MI') as start_at, "
				+ "to_char(r.reservation_end_time, 'YYYY-MM-DD HH24:MI') as end_at, "
				+ "to_char(r.reservation_out_time, 'YYYY-MM-DD HH24:MI') as out_at, "
				+ "to_char(r.reservation_parking_start_time, 'YYYY-MM-DD HH24:MI') as parking_start_at, "
				+ "to_char(r.reservation_arrive_time, 'YYYY-MM-DD HH24:MI') as arrive_at, "
				+ "to_char(r.reservation_date, 'YYYY-MM-DD HH24:MI') as reserved_at, "
				+ "case when r.reservation_status = '1' and r.reservation_start_time > sysdate "
				+ "then 1 else 0 end as can_cancel, "
				+ "r.reservation_estimate_amount, r.reservation_deposit_amount, r.reservation_final_amount "
				+ "from icn_reservation r where r.member_id = ? "
				+ "order by r.reservation_date desc, r.reservation_id desc";
		try {
			con = DBConnection.getConnection();
			ps = new LogPreparedStatement(con, sql);
			ps.setString(1, memberId);
			rs = ps.executeQuery();
			while (rs.next()) {
					Map<String, Object> reservation = new LinkedHashMap<>();
					reservation.put("reservation_id", rs.getString("reservation_id"));
					reservation.put("reservation_status", rs.getString("reservation_status"));
					reservation.put("reservation_type", rs.getString("reservation_type"));
					reservation.put("seat_no", rs.getString("seat_no"));
					reservation.put("flight_no", rs.getString("flight_no"));
					reservation.put("start_at", rs.getString("start_at"));
					reservation.put("end_at", rs.getString("end_at"));
					reservation.put("out_at", rs.getString("out_at"));
					reservation.put("parking_start_at", rs.getString("parking_start_at"));
					reservation.put("arrive_at", rs.getString("arrive_at"));
					reservation.put("reserved_at", rs.getString("reserved_at"));
					reservation.put("can_cancel", rs.getInt("can_cancel"));
					reservation.put("reservation_estimate_amount", rs.getObject("reservation_estimate_amount"));
					reservation.put("reservation_deposit_amount", rs.getObject("reservation_deposit_amount"));
					reservation.put("reservation_final_amount", rs.getObject("reservation_final_amount"));
					reservations.add(reservation);
			}
		} catch (Exception e) {
			throw new IllegalStateException("예약 내역 조회에 실패했습니다.", e);
		} finally {
			DBConnection.closeDB(con, ps, rs);
			con = null;
			ps = null;
			rs = null;
		}
		return reservations;
	}

	public synchronized int cancelReservation(String memberId, String reservationId) {
		String sql = "update icn_reservation set reservation_status = '4' "
				+ "where reservation_id = ? and member_id = ? "
				+ "and reservation_status = '1' and reservation_start_time > sysdate";
		try {
			con = DBConnection.getConnection();
			con.setAutoCommit(false);
			ps = new LogPreparedStatement(con, sql);
			ps.setString(1, reservationId);
			ps.setString(2, memberId);
			int result = ps.executeUpdate();
			ps.close();
			ps = null;
			if (result != 1) {
				con.rollback();
				return 0;
			}

			sql = "update icn_payment set payment_type = '3', payment_amount = 0 "
					+ "where reservation_id = ? and payment_type = '1'";
			ps = new LogPreparedStatement(con, sql);
			ps.setString(1, reservationId);
			result = ps.executeUpdate();
			if (result != 1) {
				con.rollback();
				return 0;
			}

			con.commit();
			return 1;
		} catch (Exception e) {
			if (con != null) {
				try {
					con.rollback();
				} catch (Exception rollbackError) {
					e.addSuppressed(rollbackError);
				}
			}
			throw new IllegalStateException("예약 취소에 실패했습니다.", e);
		} finally {
			DBConnection.closeDB(con, ps, rs);
			con = null;
			ps = null;
			rs = null;
		}
	}

}
