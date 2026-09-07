package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import common.DBConnection;
import dto.FlightStatusDto;

// icn_flight 테이블 담당 DAO - "DB에 저장된 항공편"을 다룬다.
// 외부 API 호출은 FlightApiDao가 전담하고, 여기서는 그 결과를 저장/재사용/갱신하는 역할만 한다.
// (ParkingApiDao=외부API, 세부 좌석 DAO=DB 라는 기존 구조와 같은 패턴)
public class FlightDao {

	Connection        con = null;
	PreparedStatement ps = null;
	ResultSet         rs = null;

	private FlightStatusDto makeDto(ResultSet rs) throws Exception {
		FlightStatusDto dto = new FlightStatusDto();
		dto.setId(rs.getInt("flight_id"));
		dto.setFlightNo(rs.getString("flight_no"));
		dto.setAirport(rs.getString("airport"));
		dto.setScheduleDateTime(rs.getString("schedule_datetime"));
		dto.setEstimatedDateTime(rs.getString("estimated_datetime"));
		dto.setRemark(rs.getString("remark"));
		dto.setUpdatedAt(rs.getString("updated_at"));
		return dto;
	}

	// 편명으로 이미 저장된 항공편이 있는지 조회 (오늘 조회 범위 안에서 가장 최근 것 하나)
	public FlightStatusDto findByFlightNo(String flightNo) {
		FlightStatusDto dto = null;
		String sql = "select * from icn_flight where flight_no = ? order by flight_id desc";
		try {
			con = DBConnection.getConnection();
			ps = con.prepareStatement(sql);
			ps.setString(1, flightNo);
			rs = ps.executeQuery();
			if (rs.next()) {
				dto = makeDto(rs);
			}
		} catch (Exception e) {
			System.out.println("findByFlightNo() 오류 : " + sql);
			e.printStackTrace();
		} finally {
			DBConnection.closeDB(con, ps, rs);
		}
		return dto;
	}

	// 예약 시점에 호출 - 이미 DB에 있는 편명이면 그대로 재사용, 없으면 실시간 API로 조회해서 새로 저장.
	// 반환된 dto.getId()를 예약 테이블의 flight_id(FK)에 넣어주면 됨(오윤섭 파트 연동 지점).
	public FlightStatusDto getOrCreateFlight(String flightNo, String searchday) {
		FlightStatusDto existing = findByFlightNo(flightNo);
		if (existing != null) {
			return existing;
		}

		FlightApiDao apiDao = new FlightApiDao();
		java.util.List<FlightStatusDto> flights = apiDao.getArrivalFlights(searchday, null, null, null, "K");
		FlightStatusDto picked = null;
		for (FlightStatusDto f : flights) {
			if (flightNo.equalsIgnoreCase(f.getFlightNo())) {
				picked = f;
				break;
			}
		}
		if (picked == null) {
			return null; // 실제 API에도 없는 편명 - 컨트롤러/커맨드에서 "존재하지 않는 항공편입니다" 처리할 것
		}

		int newId = insertFlight(picked);
		picked.setId(newId);
		return picked;
	}

	private int insertFlight(FlightStatusDto dto) {
		// Oracle 드라이버 버전마다 getGeneratedKeys()가 불안정할 수 있어서,
		// 시퀀스 값을 먼저 SELECT로 받아온 뒤 그 값을 그대로 insert에 쓰는 방식(더 확실함).
		int newId = 0;
		try {
			con = DBConnection.getConnection();

			ps = con.prepareStatement("select icn_flight_seq.nextval as newid from dual");
			rs = ps.executeQuery();
			if (rs.next()) {
				newId = rs.getInt("newid");
			}
			rs.close();
			ps.close();

			String sql = "insert into icn_flight (flight_id, flight_no, airport, schedule_datetime, estimated_datetime, remark) "
					+ "values (?, ?, ?, ?, ?, ?)";
			ps = con.prepareStatement(sql);
			ps.setInt(1, newId);
			ps.setString(2, dto.getFlightNo());
			ps.setString(3, dto.getAirport());
			ps.setString(4, dto.getScheduleDateTime());
			ps.setString(5, dto.getEstimatedDateTime());
			ps.setString(6, dto.getRemark());
			ps.executeUpdate();
		} catch (Exception e) {
			System.out.println("insertFlight() 오류");
			e.printStackTrace();
			newId = 0;
		} finally {
			DBConnection.closeDB(con, ps, rs);
		}
		return newId;
	}

	// 지연 갱신(lazy update)의 핵심 메서드.
	// 마이페이지/예약조회/관리자 주차맵 등에서 "이 항공편 아직 살아있는 예약인지" 확인할 때 호출.
	// 실 API를 다시 불러서 결항 여부를 재확인하고 DB도 최신 상태로 갱신한다.
	// 반환값 true = "방금 새로 결항으로 확정됨" -> 호출한 쪽에서 예약 상태 전환(오윤섭 파트 메서드 호출)을 트리거할 것.
	public boolean refreshAndCheckNewlyCancelled(int flightId, String searchday) {
		FlightStatusDto saved = findById(flightId);
		if (saved == null || saved.isCancelled()) {
			return false; // 없거나 이미 결항 처리된 건 다시 알릴 필요 없음
		}

		FlightApiDao apiDao = new FlightApiDao();
		boolean nowCancelled = apiDao.isFlightCancelled(saved.getFlightNo(), searchday);

		if (nowCancelled) {
			updateRemark(flightId, "결항");
			return true;
		}
		return false;
	}

	private FlightStatusDto findById(int flightId) {
		FlightStatusDto dto = null;
		String sql = "select * from icn_flight where flight_id = ?";
		try {
			con = DBConnection.getConnection();
			ps = con.prepareStatement(sql);
			ps.setInt(1, flightId);
			rs = ps.executeQuery();
			if (rs.next()) {
				dto = makeDto(rs);
			}
		} catch (Exception e) {
			System.out.println("findById() 오류 : " + sql);
			e.printStackTrace();
		} finally {
			DBConnection.closeDB(con, ps, rs);
		}
		return dto;
	}

	private void updateRemark(int flightId, String remark) {
		String sql = "update icn_flight set remark = ?, updated_at = sysdate where flight_id = ?";
		try {
			con = DBConnection.getConnection();
			ps = con.prepareStatement(sql);
			ps.setString(1, remark);
			ps.setInt(2, flightId);
			ps.executeUpdate();
		} catch (Exception e) {
			System.out.println("updateRemark() 오류 : " + sql);
			e.printStackTrace();
		} finally {
			DBConnection.closeDB(con, ps, rs);
		}
	}

}
