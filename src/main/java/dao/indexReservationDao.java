package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import common.DBConnection;
import dto.MemberDto;
import dto.ReservationInfoDto;

public class indexReservationDao {
	Connection con = null;
	PreparedStatement ps = null;
	ResultSet rs = null;
	
	
	
	
	public ReservationInfoDto getFinalPayment(String id) {
		ReservationInfoDto dto = null;
		String sql = "select reservation_id, member_id, seat_no,\r\n"
				+ "       reservation_status,reservation_start_time, reservation_end_time,\r\n"
				+ "       reservation_parking_start_time,reservation_date,\r\n"
				+ "       reservation_final_amount,reservation_type\r\n"
				+ "from (\r\n"
				+ "        select *\r\n"
				+ "        from icn_reservation\r\n"
				+ "        where member_id = '"+id+"'\r\n"
				+ "        order by reservation_date desc\r\n"
				+ ")\r\n"
				+ "where rownum = 1";
		
		try {
			con = DBConnection.getConnection();
			ps  = con.prepareStatement(sql);
			rs  = ps.executeQuery();	
			if(rs.next()){		
				//String   = rs.getString("id");
				String reservation_id 		= rs.getString("reservation_id");
//				String member_id 		= rs.getString("member_id");
				String seat_no 		= rs.getString("seat_no");
				String reservation_status 		= rs.getString("reservation_status");
				String reservation_start_time 		= rs.getString("reservation_start_time");
				String reservation_end_time 		= rs.getString("reservation_end_time");
				String reservation_parking_start_time 		= rs.getString("reservation_parking_start_time");
				String reservation_date 		= rs.getString("reservation_date");
				String reservation_type 		= rs.getString("reservation_type");
				int reservation_final_amount 		= rs.getInt("reservation_final_amount");
				
				dto = new ReservationInfoDto(reservation_final_amount, reservation_id, reservation_status, reservation_start_time,reservation_end_time, id, seat_no, reservation_parking_start_time, reservation_date,reservation_type);
				
			}
		}catch(Exception e) {
			System.out.println("getFinalPayment() 오류:"+sql);
			e.printStackTrace();
		}finally {
			DBConnection.closeDB(con, ps, rs);
		}		
		
		
		return dto;
		
		
		
		
		
	}
	
	
	
	
	
}
