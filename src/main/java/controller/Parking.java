package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.ParkingApiDao;
import dto.ParkingStatusDto;

/**
 * 주차 현황 관련 요청 담당 서블릿 (강선구 담당).
 * t_gubun=zoneStatus : 인덱스/상세맵의 구역(P1~P9)별 실시간 잔여 대수를 JSON으로 반환
 *
 * 외부 API 키는 서버에만 두고 프론트에서 data.go.kr을 직접 부르지 않는다
 * (노션 '시스템 아키텍처'의 외부 API 처리 원칙).
 */
@WebServlet("/Parking")
public class Parking extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * 우리 화면의 구역 라벨(P1~P9) -> 공공데이터 API가 주는 실제 구역명 매핑.
	 *
	 * 실제 인천공항 T1에는 P4가 없고(2026-07 폐지) P6~P9도 존재하지 않는다.
	 * 화면 구성은 팀에서 정한 P1~P9를 그대로 두기로 했으므로,
	 * 실데이터가 있는 구역만 연결하고 나머지는 매핑에서 빼둔다(= 화면의 임의 데이터 유지).
	 */
	private static final Map<String, String> ZONE_MAP = new LinkedHashMap<>();
	static {
		ZONE_MAP.put("P1", "T1 장기 P1 주차장");
		ZONE_MAP.put("P2", "T1 장기 P2 주차장");
		ZONE_MAP.put("P3", "T1 장기 P3 주차장");
		ZONE_MAP.put("P5", "T1 P5 예약주차장");
		// P4, P6~P9 : 실제 API에 없는 구역이라 매핑하지 않음 (화면에서 임의 데이터 사용)
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String gubun = request.getParameter("t_gubun");

		if ("zoneStatus".equals(gubun)) {
			zoneStatus(response);
			return;
		}
	}

	private void zoneStatus(HttpServletResponse response) throws IOException {
		ParkingApiDao dao = new ParkingApiDao();
		List<ParkingStatusDto> list = dao.getZoneStatusList();

		// 실제 구역명 -> DTO 로 먼저 색인을 만든 뒤, 우리 라벨 기준으로 재구성
		Map<String, ParkingStatusDto> byFloor = new LinkedHashMap<>();
		for (ParkingStatusDto d : list) {
			byFloor.put(d.getFloor(), d);
		}

		StringBuilder sb = new StringBuilder();
		sb.append("{");
		boolean first = true;
		for (Map.Entry<String, String> e : ZONE_MAP.entrySet()) {
			ParkingStatusDto d = byFloor.get(e.getValue());
			if (d == null) continue;          // API 응답에 없으면 건너뜀 -> 화면은 기존 임의값 유지
			if (!first) sb.append(",");
			first = false;
			sb.append("\"").append(e.getKey()).append("\":{");
			sb.append("\"floor\":\"").append(escape(d.getFloor())).append("\",");
			sb.append("\"parking\":").append(d.getParking()).append(",");
			sb.append("\"total\":").append(d.getParkingArea()).append(",");
			sb.append("\"remain\":").append(d.getRemain()).append(",");
			sb.append("\"status\":\"").append(escape(d.getStatusLabel())).append("\",");
			sb.append("\"datetm\":\"").append(escape(d.getDateTm())).append("\"");
			sb.append("}");
		}
		sb.append("}");

		response.setContentType("application/json; charset=UTF-8");
		PrintWriter out = response.getWriter();
		out.print(sb.toString());
		out.flush();
	}

	// JSON 라이브러리를 안 쓰므로 직접 이스케이프 (페이징을 직접 구현한 것과 같은 이유)
	private String escape(String s) {
		if (s == null) return "";
		return s.replace("\\", "\\\\").replace("\"", "\\\"");
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
