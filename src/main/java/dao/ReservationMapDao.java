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
	public List<ReservationMapDto> getPakingMap(String map ,String startTime, String endTime) {
		ArrayList<ReservationMapDto> dtos=new ArrayList<>();
		String sql = "SELECT  LOT_ID,SEAT_NO,park_status\r\n"
				+ "FROM ICN_SEAT \r\n"
				+ "where LOT_ID='?'";
		
		try {
			con=DBConnection.getConnection();
			LogPreparedStatement ps =new LogPreparedStatement(con, sql);
			
			ps.setString(1, map);
			rs=ps.executeQuery();
			String parkingLotId =rs.getString("LOT_ID");
			String seatId=rs.getString("SEAT_NO");
			String type = rs.getString("park_status");
			ReservationMapDto dto =new ReservationMapDto(parkingLotId, seatId, type, true,startTime,endTime);
		} catch (Exception e) {
			// TODO: handle exception
		}finally {
			DBConnection.closeDB(con, ps, rs);
		}
		
		
		return dtos;
	}
	
	//주차상태 업데이트
 
	
	
}
