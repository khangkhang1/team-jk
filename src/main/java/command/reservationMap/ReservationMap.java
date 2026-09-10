package command.reservationMap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import common.CommonExecute;
import common.DBConnection;
import dao.ReservationMapDao;
import dto.ReservationMapDto;

public class ReservationMap implements CommonExecute {

	@Override
	public void execute(HttpServletRequest request) {
		//전체 주차 데이터 입력 .. 지금 고민중인거 스테이터스 변수를 1~3으로 놔눠서 저장 할지 아님 걍 
		//이름 처럼 저장 할지 고민된다
		
//		String[] zoneIds = {"P1", "P2", "P3", "P4", "P5", "P6", "P7", "P8", "P9"};
//		int[] seatCounts = {52, 50, 48, 50, 48, 52, 53, 51, 51};
//
//		String sql = "INSERT INTO ICN_SEAT (SEAT_NO, LOT_ID, PARK_STATUS, SEAT_TYPE) VALUES (?, ?, ?, ?)";
//
//		try (Connection con = DBConnection.getConnection();
//			 PreparedStatement ps = con.prepareStatement(sql)) {
//
//			// 트랜잭션 수동 관리를 위해 autoCommit 해제
//			con.setAutoCommit(false);
//
//			for (int z = 0; z < zoneIds.length; z++) {
//				String lotId = zoneIds[z];
//				int totalSeats = seatCounts[z];
//
//				for (int i = 1; i <= totalSeats; i++) {
//					String seatNo = String.format("%s-%02d", lotId, i);
//					
//					String seatType = "NORMAL";
//					if (i % 12 == 0) {
//						seatType = "DISABLED";
//					} else if (i % 9 == 0) {
//						seatType = "EV";
//					}
//
//					String parkStatus = "FREE";
//
//					ps.setString(1, seatNo);
//					ps.setString(2, lotId);
//					ps.setString(3, parkStatus);
//					ps.setString(4, seatType);
//
//					ps.addBatch();
//				}
//			}
//
//			// 일괄 실행 및 트랜잭션 커밋
//			int[] result = ps.executeBatch();
//			con.commit();
//			
//			System.out.println("성공적으로 총 " + result.length + "개의 주차 좌석 데이터를 저장했습니다.");
//
//		} catch (SQLException e) {
//			System.out.println("좌석 데이터 삽입 중 오류 발생!");
//			e.printStackTrace();
//	}
		//
		ReservationMapDao dao = ReservationMapDao.getDao();
		
		List<ReservationMapDto> dtos= dao.getPakingMap(null, null, null);
		
	}
}