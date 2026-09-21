package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import common.DBConnection;
import dto.ReservationMapDto;

public class ReservationMapDao {
	Connection con = null;
	PreparedStatement ps = null;
	ResultSet rs = null;
	
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
				+ "    ICN_FLIGHT f"
				+ " ON p.FLIGHT_no = f.FLIGHT_NO \r\n"
				+ " "
				+ "WHERE "
				+ "    s.LOT_ID = ? " // 세 번째 ? -> map
				+ "ORDER BY "
				+ "    s.SEAT_NO ASC";
		
		try {
			con = DBConnection.getConnection();
			ps = con.prepareStatement(sql); // LogPreparedStatement를 사용하셨다면 복구하셔도 됩니다.
			
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
	
	// ====================================================================
	// 결항 랜덤 배정 업데이트 로직
	// ====================================================================
	
	/**
	 * 1. 특정 시간대(startTime ~ endTime)에 P6~P9 구역 중 예약이 없는 빈자리 목록 조회
	 */
	public List<String> getAvailableSeatsP6toP9(String startTime, String endTime) {
		List<String> availableSeats = new ArrayList<>();
		
		String sql = "SELECT SEAT_NO FROM ICN_SEAT "
				   + "WHERE LOT_ID IN ('P6', 'P7', 'P8', 'P9') "
				   + "  AND SEAT_NO NOT IN ( "
				   + "      SELECT SEAT_NO FROM ICN_RESERVATION "
				   + "      WHERE SEAT_NO IS NOT NULL "
				   + "        AND RESERVATION_START_TIME < TO_DATE(?, 'YYYY-MM-DD HH24:MI') "
				   + "        AND NVL(RESERVATION_END_TIME, TO_DATE('9999-12-31 23:59', 'YYYY-MM-DD HH24:MI')) > TO_DATE(?, 'YYYY-MM-DD HH24:MI') "
				   + "  )";
		
		try {
			con = DBConnection.getConnection();
			ps = con.prepareStatement(sql); 
			ps.setString(1, endTime);   // 첫 번째 ? -> endTime
			ps.setString(2, startTime); // 두 번째 ? -> startTime
			
			rs = ps.executeQuery();
			while (rs.next()) {
				availableSeats.add(rs.getString("SEAT_NO"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnection.closeDB(con, ps, rs);
		}
		
		return availableSeats;
	}

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
			DBConnection.closeDB(con, ps, rs);
		}
		
		return isSuccess;
	}

	/**
	 * 3. [업그레이드 버전] 결항된 비행기(f.UPDATED_AT IS NOT NULL)와 연결된 좌석을 찾고,
	 *    해당 좌석의 '미래 예약건(피해자)'들을 자동으로 찾아 빈자리로 옮겨줍니다.
	 */
	public void autoChange() {
		// ICN_FLIGHT 테이블을 조인하여 결항된 비행기를 물고 있는 자리의 미래 예약건만 추출!
		String selectSql = "SELECT "
				         + "    r.RESERVATION_ID, "
				         + "    r.SEAT_NO AS CANCELLED_SEAT_NO, "
				         + "    TO_CHAR(r.RESERVATION_START_TIME, 'YYYY-MM-DD HH24:MI') AS START_TIME, "
				         + "    TO_CHAR(r.RESERVATION_END_TIME, 'YYYY-MM-DD HH24:MI') AS END_TIME "
				         + "FROM ICN_RESERVATION r "
				         + "WHERE r.RESERVATION_START_TIME > SYSDATE " // 1. 미래의 예약건 중에서
				         + "  AND r.SEAT_NO IN ( "
				         + "      -- 2. 현재 주차 중인데 비행기가 결항(UPDATED_AT IS NOT NULL)된 좌석 번호 찾기 "
				         + "      SELECT p.SEAT_NO "
				         + "      FROM ICN_RESERVATION p "
				         + " 	  JOIN ICN_FLIGHT f ON p.FLIGHT_no = f.FLIGHT_no  "
				         + "      WHERE p.RESERVATION_START_TIME <= SYSDATE "
				         + "        AND NVL(p.RESERVATION_END_TIME, SYSDATE + 1) >= SYSDATE "
				         + "        AND f.UPDATED_AT IS NOT NULL " 
				         + "  )";
		
		List<java.util.Map<String, String>> targetReservations = new ArrayList<>();
		
		try {
			con = DBConnection.getConnection();
			ps = con.prepareStatement(selectSql);
			rs = ps.executeQuery();
			
			while (rs.next()) {
				java.util.Map<String, String> map = new java.util.HashMap<>();
				map.put("resId", rs.getString("RESERVATION_ID"));
				map.put("cancelledSeat", rs.getString("CANCELLED_SEAT_NO"));
				map.put("start", rs.getString("START_TIME"));
				map.put("end", rs.getString("END_TIME"));
				targetReservations.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnection.closeDB(con, ps, rs);
		}
		
		// 찾아낸 피해 예약 건들을 돌면서 빈자리 랜덤 배정
		for (java.util.Map<String, String> res : targetReservations) {
			String resId = res.get("resId");
			String cancelledSeat = res.get("cancelledSeat");
			String startTime = res.get("start");
			String endTime = res.get("end");
			
			// 1번 메서드 호출: P6~P9에서 해당 시간대의 빈자리 목록 조회
			List<String> availableSeats = getAvailableSeatsP6toP9(startTime, endTime);
			
			if (availableSeats != null && !availableSeats.isEmpty()) {
				// 빈자리 목록을 무작위로 섞어서 하나 추출
				java.util.Collections.shuffle(availableSeats);
				String luckyNewSeat = availableSeats.get(0);
				
				// 2번 메서드 호출: DB 업데이트
				updateReservationSeat(resId, luckyNewSeat);
				
				System.out.println("예약 [" + resId + "] 님이 결항으로 묶인 " + cancelledSeat + " 자리에서 " + luckyNewSeat + "(으)로 자동 배정되었습니다.");
			} else {
				System.out.println("예약 [" + resId + "] 님의 시간대에 P6~P9 구역 빈자리가 없어 재배정 실패!");
			}
		}
	}
}