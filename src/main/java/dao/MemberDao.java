package dao;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Timestamp;

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
		String sql = "select name from icn_member where member_id=? and password=?";
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
			ps.setString(2, password);
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

}
