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

	public int checkId(String member_id) {
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

	public int getCheckPassword(String member_id, String password) {
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

	public String getLoginName(String member_id, String password) {
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

	public MemberDto getMemberInfo(String member_id) {
		MemberDto dto = null;
		String sql = "select name,password,phone_number,email,vehicle_number,vehicle_type,reg_date,update_date,exit_date from icn_member where member_id=?";
		try {
			con = DBConnection.getConnection();
			ps = new LogPreparedStatement(con, sql);
			ps.setString(1, member_id);
			rs = ps.executeQuery();
			if (rs.next()) {
				dto=new MemberDto();
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

	public int memberPasswordUpdate(String member_id, String password) {
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

	public int memberSave(MemberDto dto) {
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

	public int memberUpdate(MemberDto dto) {
		int result=0;
		String sql="update icn_member set name=?,phone_number=?,email=?,vehicle_number=?,vehicle_type=?,update_date=sysdate where member_id=? ";
		try {
			con=DBConnection.getConnection();
			ps=new LogPreparedStatement(con, sql);
			ps.setString(1,dto.getName());
			ps.setString(2,dto.getPhone_number());
			ps.setString(3,dto.getEmail());
			ps.setString(4,dto.getVehicle_number());
			ps.setString(5,dto.getVehicle_type());
			ps.setString(6,dto.getMember_id());
			result=ps.executeUpdate();
		}catch(Exception e) {
			e.printStackTrace();
			System.out.println("Error: "+ps.toString());
		}finally {
			DBConnection.closeDB(con, ps, rs);
		}
		return result;
	}

	public int memberExit(String id) {
		int result=0;
		String sql="update icn_member set exit_date=sysdate where member_id=?";
		try {
			con=DBConnection.getConnection();
			ps=new LogPreparedStatement(con, sql);
			ps.setString(1,id);
			result=ps.executeUpdate();
		}catch(Exception e) {
			e.printStackTrace();
			System.out.println("Error: "+ps.toString());
		}finally {
			DBConnection.closeDB(con, ps, rs);
		}
		return result;
	}

	public List<Map<String, Object>> getReservationInfo(String memberId) {
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
		try (Connection connection = DBConnection.getConnection();
				LogPreparedStatement statement = new LogPreparedStatement(connection, sql)) {
			statement.setString(1, memberId);
			try (ResultSet result = statement.executeQuery()) {
				while (result.next()) {
					Map<String, Object> reservation = new LinkedHashMap<>();
					reservation.put("reservation_id", result.getString("reservation_id"));
					reservation.put("reservation_status", result.getString("reservation_status"));
					reservation.put("reservation_type", result.getString("reservation_type"));
					reservation.put("seat_no", result.getString("seat_no"));
					reservation.put("flight_no", result.getString("flight_no"));
					reservation.put("start_at", result.getString("start_at"));
					reservation.put("end_at", result.getString("end_at"));
					reservation.put("out_at", result.getString("out_at"));
					reservation.put("parking_start_at", result.getString("parking_start_at"));
					reservation.put("arrive_at", result.getString("arrive_at"));
					reservation.put("reserved_at", result.getString("reserved_at"));
					reservation.put("can_cancel", result.getInt("can_cancel"));
					reservation.put("reservation_estimate_amount", result.getObject("reservation_estimate_amount"));
					reservation.put("reservation_deposit_amount", result.getObject("reservation_deposit_amount"));
					reservation.put("reservation_final_amount", result.getObject("reservation_final_amount"));
					reservations.add(reservation);
				}
			}
		} catch (Exception e) {
			throw new IllegalStateException("예약 내역 조회에 실패했습니다.", e);
		}
		return reservations;
	}

	public int cancelReservation(String memberId, String reservationId) {
		String sql = "update icn_reservation set reservation_status = '4' "
				+ "where reservation_id = ? and member_id = ? "
				+ "and reservation_status = '1' and reservation_start_time > sysdate";
		try (Connection connection = DBConnection.getConnection();
				LogPreparedStatement statement = new LogPreparedStatement(connection, sql)) {
			statement.setString(1, reservationId);
			statement.setString(2, memberId);
			return statement.executeUpdate();
		} catch (Exception e) {
			throw new IllegalStateException("예약 취소에 실패했습니다.", e);
		}
	}

}
