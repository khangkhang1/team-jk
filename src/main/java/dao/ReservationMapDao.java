package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import common.DBConnection;
import dto.ReservationMapDto;

public class ReservationMapDao {
	Connection 		  con=null;
	PreparedStatement  ps=null;
	ResultSet		   rs=null;
	private ReservationMapDao(){}
	private static ReservationMapDao dao = new ReservationMapDao();
	public static ReservationMapDao getDao(){
		return dao;
	}
	//주차 상태
	public List<ReservationMapDto> getPakingMap(String map ,String startTime, String endTime) {
		ArrayList<ReservationMapDto> dtos=new ArrayList<>();
		String sql = "SELECT \r\n"
				+ "    s.LOT_ID,\r\n"
				+ "    s.SEAT_NO,\r\n"
				+ "    DECODE(s.SEAT_TYPE, 'N', '일반차', 'E', '수소차', 'D', '장애인차') AS SEAT_TYPE_NM,\r\n"
				+ "    CASE \r\n"
				+ "        WHEN p.SEAT_NO IS NOT NULL THEN '예약중' \r\n"
				+ "        ELSE '예약 가능' \r\n"
				+ "    END AS PARK_STATUS\r\n"
				+ "FROM \r\n"
				+ "    ICN_SEAT s\r\n"
				+ "LEFT JOIN \r\n"
				+ "    ICN_RESERVATION p \r\n"
				+ "    ON s.SEAT_NO = p.SEAT_NO\r\n"
				+ "   AND p.RESERVATION_START_TIME < TO_DATE(?, 'YYYY-MM-DD HH24:MI')\r\n"
				+ "   AND NVL(p.RESERVATION_END_TIME, TO_DATE('9999-12-31 23:59', 'YYYY-MM-DD HH24:MI')) > TO_DATE(?, 'YYYY-MM-DD HH24:MI')\r\n"
				+ "WHERE \r\n"
				+ "    s.LOT_ID = ?\r\n"
				+ "ORDER BY \r\n"
				+ "    s.SEAT_NO ASC";
		
		try {
			con=DBConnection.getConnection();
			ps =new LogPreparedStatement(con, sql);
			
			
			ps.setString(1, endTime);
			ps.setString(2, startTime);
			ps.setString(3, map);
			rs=ps.executeQuery();
			while (rs.next()) {
				String parkingLotId =rs.getString("LOT_ID");
			String seatId=rs.getString("SEAT_NO");
			String type = rs.getString("SEAT_TYPE_NM");
			
			String isReserved =rs.getString("PARK_STATUS");
			ReservationMapDto dto =new ReservationMapDto(parkingLotId, seatId, type, isReserved,startTime,endTime);
			dtos.add(dto);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			}finally {
			DBConnection.closeDB(con, ps, rs);
		}
		
		
		return dtos;
	}
	
	//주차상태 업데이트
 
	
	
}
