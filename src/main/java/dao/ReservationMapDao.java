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
	
	//결항렌덤 배정 업데이트
	
	/**
	 * 2. 예약 건의 좌석(SEAT_NO)을 새로운 좌석으로 업데이트 (UPDATE)
	 */
	public boolean updateReservationSeat(String reservationId, String newSeatNo) {
		boolean isSuccess = false;
		String sql = "UPDATE ICN_RESERVATION SET SEAT_NO = ? WHERE RESERVATION_ID = ?";
		
		try {
			con = DBConnection.getConnection();
			ps = con.prepareStatement(sql);
			ps.setString(1, newSeatNo);
			ps.setString(2, reservationId);
			
			int result = ps.executeUpdate();
			if (result > 0) isSuccess = true;
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnection.closeDB(con, ps, rs); // UPDATE는 rs가 없으므로 내부적으로 null 처리됨
		}
		
		return isSuccess;
	}

	/**
	 * 3. [핵심 실행 메서드] 특정 자리가 결항으로 묶였을 때, 그 자리를 예약한 다음 손님들을 찾아 랜덤으로 자리를 옮겨줍니다.
	 * Controller나 Service에서 이 메서드 하나만 호출하면 됩니다.
	 */
	public void reassignCancelledSeat(String cancelledSeatNo) {
		// 1) 결항된 자리에 예약된 '미래의 예약 건'들을 가져옵니다.
		String selectSql = "SELECT RESERVATION_ID, "
				         + "TO_CHAR(RESERVATION_START_TIME, 'YYYY-MM-DD HH24:MI') AS START_TIME, "
				         + "TO_CHAR(RESERVATION_END_TIME, 'YYYY-MM-DD HH24:MI') AS END_TIME "
				         + "FROM ICN_RESERVATION "
				         + "WHERE SEAT_NO = ? "
				         + "  AND RESERVATION_START_TIME > SYSDATE"; // 현재 시간 이후의 예약 건만 타겟
		
		// 데이터를 임시로 담을 리스트 (Map 구조 활용)
		List<java.util.Map<String, String>> targetReservations = new ArrayList<>();
		
		try {
			con = DBConnection.getConnection();
			ps = con.prepareStatement(selectSql);
			ps.setString(1, cancelledSeatNo);
			rs = ps.executeQuery();
			
			while (rs.next()) {
				java.util.Map<String, String> map = new java.util.HashMap<>();
				map.put("resId", rs.getString("RESERVATION_ID"));
				map.put("start", rs.getString("START_TIME"));
				map.put("end", rs.getString("END_TIME"));
				targetReservations.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnection.closeDB(con, ps, rs);
		}
		
		// 2) 찾아낸 피해 예약 건들을 돌면서 빈자리 랜덤 배정
		for (java.util.Map<String, String> res : targetReservations) {
			String resId = res.get("resId");
			String startTime = res.get("start");
			String endTime = res.get("end");
			
			// 해당 시간에 P6~P9에서 비어있는 자리 목록 싹 다 가져오기
			List<String> availableSeats = getAvailableSeatsP6toP9(startTime, endTime);
			
			if (availableSeats != null && !availableSeats.isEmpty()) {
				// ★ 핵심 로직: 빈자리 목록을 무작위로 섞습니다. (java.util.Collections 필요)
				java.util.Collections.shuffle(availableSeats);
				
				// 섞인 리스트의 첫 번째 자리가 랜덤 뽑기 결과
				String luckyNewSeat = availableSeats.get(0);
				
				// 해당 손님의 예약을 새로운 자리로 DB 업데이트
				updateReservationSeat(resId, luckyNewSeat);
				
				System.out.println("예약번호 [" + resId + "] 님이 결항으로 인해 " + cancelledSeatNo + " -> " + luckyNewSeat + "(으)로 자동 배정되었습니다.");
			} else {
				System.out.println("예약번호 [" + resId + "] 님의 시간대에 P6~P9 구역 빈자리가 없어 재배정 실패!");
				// 빈자리가 아예 없을 때의 관리자 알림 등 예외 처리 구역
			}
		}
	}
	private List<String> getAvailableSeatsP6toP9(String startTime, String endTime) {
		
		
		return null;
	}
	
	
}
