package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;

import common.CommonUtil;
import common.DBConnection;
import dto.PaymentDto;
import dto.ReservationInfoDto;

public class PaymentDao {
//DB연결 관련 변수
	Connection con 			= null;
	PreparedStatement ps 	= null;
	ResultSet rs 			= null;
			
//Dao 싱글톤
	private PaymentDao() {};
	private static PaymentDao dao = new PaymentDao();

	public static PaymentDao getDao() {
		return dao;
	}

//=================================이후로 예약 method=================================
	
//예약 번호 생성
	public String getReservationId() {
		String id = "";
		String sql = "select max(reservation_id) as reservation_id\r\n"
				+ "from icn_reservation";
		try {
			con = DBConnection.getConnection();
			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()) {
				String maxId = rs.getString("reservation_id");
				String todayYM = CommonUtil.getTodayYYMM();
					
				if(maxId == null || !maxId.substring(1, 6).equals(todayYM)) {
					id = "R" + todayYM + "-0001";
				} else {
					if(maxId.substring(1, 6).equals(todayYM)) {
						String n = maxId.substring(7);
						int newId = Integer.parseInt(n) + 1;
						DecimalFormat df = new DecimalFormat("0000");
						id = "R" + todayYM + "-" + df.format(newId);
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println("getReservationId() 오류" + sql);
		} finally {
			DBConnection.closeDB(con, ps, rs);
		}
			
		return id;
	}
		
//예약 저장
	public int saveReservation(ReservationInfoDto r_dto) {
		int result = 0;
		
		String start_datetime = r_dto.getReservation_start_date() + " " + r_dto.getReservation_start_time();
		String end_datetime = null;
		String sql = "";
		if(r_dto.getReservation_type().equals("1")) {
			end_datetime = r_dto.getReservation_end_date() + " " + r_dto.getReservation_end_time();
			sql = "insert into icn_reservation\r\n"
					+ "(reservation_id, reservation_status, reservation_start_time, reservation_end_time,\r\n"
					+ "reservation_type, flight_id, member_id, seat_no,\r\n"
					+ "reservation_estimate_amount, reservation_deposit_amount)\r\n"
					+ "values\r\n"
					+ "(?, ?, to_date(?,'yyyy-MM-dd hh24:mi:ss'),\r\n"
					+ "to_date(?,'yyyy-MM-dd hh24:mi:ss'),\r\n"
					+ "?, ?, ?, ?, ?, ?)";
			
			try {
				con = DBConnection.getConnection();
				LogPreparedStatement ps = new LogPreparedStatement(con, sql);
				ps.setString(1, r_dto.getReservation_id());
				ps.setString(2, r_dto.getReservation_status());
				ps.setString(3, start_datetime);
				ps.setString(4, end_datetime);
				ps.setString(5, r_dto.getReservation_type());
				ps.setInt(6, Integer.parseInt(r_dto.getFlight_no()));
//				ps.setString(7, r_dto.getMember_id());
				ps.setString(7, "manager");
				ps.setString(8, r_dto.getSeat_no());
				ps.setInt(9, r_dto.getReservation_estimate_amount());
				ps.setInt(10, r_dto.getReservation_estimate_amount());
				result = ps.executeUpdate();
			}catch(Exception e) {
				System.out.println("saveReservation() 오류:" + ps.toString());
				e.printStackTrace();
			}finally {
				DBConnection.closeDB(con, ps, rs);
			}
		} else {
			sql = "insert into icn_reservation\r\n"
					+ "(reservation_id, reservation_status, reservation_start_time,\r\n"
					+ "reservation_type, member_id, seat_no,\r\n"
					+ "reservation_deposit_amount)\r\n"
					+ "values\r\n"
					+ "(?, ?, to_date(?,'yyyy-MM-dd hh24:mi:ss'),\r\n"
					+ "?, ?, ?, ?)";
			
			try {
				con = DBConnection.getConnection();
				LogPreparedStatement ps = new LogPreparedStatement(con, sql);
				ps.setString(1, r_dto.getReservation_id());
				ps.setString(2, r_dto.getReservation_status());
				ps.setString(3, start_datetime);
				ps.setString(4, r_dto.getReservation_type());
//				ps.setString(5, r_dto.getMember_id());
				ps.setString(5, "manager");
				ps.setString(6, r_dto.getSeat_no());
				ps.setInt(7, r_dto.getReservation_deposit_amount());
				result = ps.executeUpdate();
			}catch(Exception e) {
				System.out.println("saveReservation() 오류:" + ps.toString());
				e.printStackTrace();
			}finally {
				DBConnection.closeDB(con, ps, rs);
			}
		}
		
		return result;
	}
		
//=================================이후로 결제 method=================================

//결제 번호 생성
	public String getPaymentId() {
		String id = "";
		String sql = "select max(payment_id) as payment_id\r\n"
					+ "from icn_payment";
		try {
			con = DBConnection.getConnection();
			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()) {
				String maxId = rs.getString("payment_id");
				String todayYM = CommonUtil.getTodayYYMM();
							
				if(maxId == null || !maxId.substring(1, 6).equals(todayYM)) {
					id = "P" + todayYM + "-0001";
				} else {
					if(maxId.substring(1, 6).equals(todayYM)) {
						String n = maxId.substring(7);
						int newId = Integer.parseInt(n) + 1;
						DecimalFormat df = new DecimalFormat("0000");
						id = "P" + todayYM + "-" + df.format(newId);
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println("getPaymentId() 오류" + sql);
		} finally {
			DBConnection.closeDB(con, ps, rs);
		}
					
		return id;
	}
	
//예약 시 결제 저장(예약금)
	public int savePayment(PaymentDto p_dto) {
		int result = 0;
		String sql = "insert into icn_payment\r\n"
				+ "(payment_id, payment_amount, payment_method, payment_type, payment_date, reservation_id)\r\n"
				+ "values\r\n"
				+ "(?, ?, ?, ?, to_date(?,'yyyy-MM-dd hh24:mi:ss'), ?)";
		try {
			con = DBConnection.getConnection();
			LogPreparedStatement ps = new LogPreparedStatement(con, sql);
			ps.setString(1, p_dto.getPayment_id());
			ps.setInt(2, p_dto.getPayment_deposit_amount());
			ps.setString(3, p_dto.getPayment_method());
			ps.setString(4, p_dto.getPayment_type());
			ps.setString(5, p_dto.getPayment_date());
			ps.setString(6, p_dto.getReservation_id());
			result = ps.executeUpdate();
		}catch(Exception e) {
			System.out.println("savePayment() 오류:" + ps.toString());
			e.printStackTrace();
		}finally {
			DBConnection.closeDB(con, ps, rs);
		}
		return result;
	}
}