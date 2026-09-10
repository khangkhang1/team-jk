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
	public int saveReservation(ReservationInfoDto r_dto, PaymentDto p_dto) {
		int result = 0;
		String start_datetime = r_dto.getReservation_start_date() + " " + r_dto.getReservation_start_time();
		String end_date = null;
		if(r_dto.getReservation_type().equals("1")) {
			end_date = r_dto.getReservation_end_date() + " " + r_dto.getReservation_end_time();
		}
		String sql = "";
		try {
			con = DBConnection.getConnection(); 
			LogPreparedStatement ps = new LogPreparedStatement(con, sql);
			
			result = ps.executeUpdate();
		}catch(Exception e) {
			System.out.println("saveReservation() 오류:" + ps.toString());
			e.printStackTrace();
		}finally {
			DBConnection.closeDB(con, ps, rs);
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
}
