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
	public List<ReservationMapDto> getPakingMap(String map, String startTime, String endTime) {
	    ArrayList<ReservationMapDto> dtos = new ArrayList<>();
	    
	    // 오라클 교차 시간 비교 공식:
	    // RESERVATION_START_TIME < 조회종료시간(endTime) AND RESERVATION_END_TIME > 조회시작시간(startTime)
	    String sql = "SELECT "
	            + "    s.LOT_ID, "
	            + "    s.SEAT_NO, "
	            + "    DECODE(s.SEAT_TYPE, 'N', '일반차', 'E', '수소차', 'D', '장애인차') AS SEAT_TYPE_NM, "
	            + "    CASE "
	            + "        WHEN p.SEAT_NO IS NOT NULL AND f.UPDATED_AT IS NOT NULL THEN '결항 재배정중' "
	            + "        WHEN p.SEAT_NO IS NOT NULL THEN '예약중' "
	            + "        ELSE '예약 가능' "
	            + "    END AS PARK_STATUS "
	            + "FROM "
	            + "    ICN_SEAT s "
	            + "LEFT JOIN "
	            + "    ICN_RESERVATION p ON s.SEAT_NO = p.SEAT_NO "
	            + "   AND p.RESERVATION_START_TIME < TO_DATE(?, 'YYYY-MM-DD HH24:MI') " // 첫 번째 ? -> endTime
	            + "   AND NVL(p.RESERVATION_END_TIME, TO_DATE('9999-12-31 23:59', 'YYYY-MM-DD HH24:MI')) > TO_DATE(?, 'YYYY-MM-DD HH24:MI') " // 두 번째 ? -> startTime
	            + "LEFT JOIN "
	            + "    ICN_FLIGHT f ON p.FLIGHT_ID = f.FLIGHT_ID "
	            + "WHERE "
	            + "    s.LOT_ID = ? " // 세 번째 ? -> map
	            + "ORDER BY "
	            + "    s.SEAT_NO ASC";
	    
	    try {
	        con = DBConnection.getConnection();
	        ps = new LogPreparedStatement(con, sql);
	        
	        // ★ [중요] 바인딩 순서 세팅 확인
	        ps.setString(1, endTime);    // 첫 번째 ?에는 endTime
	        ps.setString(2, startTime);  // 두 번째 ?에는 startTime
	        ps.setString(3, map);        // 세 번째 ?에는 LOT_ID (P1)
	        
	        rs = ps.executeQuery();
	        while (rs.next()) {
	            String parkingLotId = rs.getString("LOT_ID");
	            String seatId = rs.getString("SEAT_NO");
	            String type = rs.getString("SEAT_TYPE_NM");
	            String isReserved = rs.getString("PARK_STATUS");
	            
	            ReservationMapDto dto = new ReservationMapDto(parkingLotId, seatId, type, isReserved, startTime, endTime);
	            dtos.add(dto);
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        DBConnection.closeDB(con, ps, rs);
	    }
	    
	    return dtos;
	}
	
	//주차상태 업데이트
 
	
	
}
