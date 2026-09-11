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
	 * 우리 화면의 구역 라벨(P1~P9)에 실데이터를 붙이는 매핑.
	 *
	 * [2026-09-08 임정규 제안 -> 팀 채택] API를 두 개 나눠 쓴다.
	 *   - 장기/예약/주차타워 : StatusOfParking (구역 단위 집계를 그대로 줌)
	 *   - 단기 4구역        : ParkLocationData를 parkzoneno(01~04)로 집계
	 * 이유: StatusOfParking은 단기주차장을 "층" 단위로만 줘서(지상층 1,052면 한 덩어리)
	 *       구역으로 쪼개 놓은 우리 주차맵에 붙일 수가 없다.
	 *
	 * [P4 처리] 실제 인천공항 T1의 P4는 2026-07에 폐지돼서 API에 데이터가 없다.
	 *   화면의 P4 칸을 비워두지 않기 위해, 실존하는 "T1 장기 P1 주차타워" 데이터를
	 *   P4 자리에 붙이기로 함(팀 결정). 즉 P4 칸에 뜨는 수치는 주차타워의 실시간 실측값이다.
	 *   ※ 우리 화면의 P1~P9 라벨 자체가 팀이 임의로 정한 것이라 실제 공항 구역명과 다르다.
	 *     발표/면접에서 "실제 공항과 구역 구성이 다르다"는 질문이 나오면 이 점을 설명할 것.
	 */
	private static final Map<String, String> ZONE_MAP = new LinkedHashMap<>();
	static {
		ZONE_MAP.put("P1", "T1 장기 P1 주차장");
		ZONE_MAP.put("P2", "T1 장기 P2 주차장");
		ZONE_MAP.put("P3", "T1 장기 P3 주차장");
		ZONE_MAP.put("P4", "T1 장기 P1 주차타워");   // 실제 P4는 폐지됨 -> 주차타워 데이터로 대체
		ZONE_MAP.put("P5", "T1 P5 예약주차장");
	}

	/**
	 * 단기주차장 구역 매핑 : 우리 라벨 -> ParkLocationData의 parkzoneno.
	 * 구 API가 개별 주차면마다 달고 오는 구역번호(01~04)를 세서 구역 단위 점유를 만든다.
	 *
	 * ※ 주의 : 이 01~04는 "단기주차장 지상층을 넷으로 쪼갠 것"이 아니다.
	 *   (지상층 전체가 1,052면인데 구역 하나가 988~1,338면이라 크기가 안 맞음)
	 *   두 API의 총 면수도 4,614 vs 3,403으로 안 맞으므로, 화면에서 장기 쪽 면수와
	 *   더해서 전체 합계를 내면 안 된다. 혼잡도/점유율 표시 용도로만 쓸 것.
	 */
	private static final Map<String, String> SHORT_ZONE_MAP = new LinkedHashMap<>();
	static {
		SHORT_ZONE_MAP.put("P6", "01");
		SHORT_ZONE_MAP.put("P7", "02");
		SHORT_ZONE_MAP.put("P8", "03");
		SHORT_ZONE_MAP.put("P9", "04");
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

		// ① 장기/예약/주차타워 - StatusOfParking 쪽. 실제 구역명으로 색인을 만들어 둔다.
		Map<String, ParkingStatusDto> byFloor = new LinkedHashMap<>();
		for (ParkingStatusDto d : dao.getZoneStatusList()) {
			byFloor.put(d.getFloor(), d);
		}

		// ② 단기 4구역 - ParkLocationData를 구역번호로 집계한 쪽. 60초 캐시가 DAO 안에 있다.
		Map<String, ParkingStatusDto> byShortZone = new LinkedHashMap<>();
		for (ParkingStatusDto d : dao.getShortTermZoneStatusList()) {
			// floor 형식이 "T1 단기주차장 01구역" 이므로 구역번호만 잘라 키로 쓴다
			String f = d.getFloor();
			if (f == null) continue;
			int idx = f.lastIndexOf(' ');
			if (idx < 0) continue;
			byShortZone.put(f.substring(idx + 1).replace("구역", ""), d);
		}

		// ③ 우리 라벨(P1~P9) 순서로 하나의 JSON으로 합친다.
		//    화면은 두 API의 차이를 몰라도 되게 같은 모양으로 내려준다.
		//    다만 어느 쪽에서 온 값인지는 source로 구분해 둔다(나중에 디버깅/설명용).
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		boolean first = true;

		for (Map.Entry<String, String> e : ZONE_MAP.entrySet()) {
			ParkingStatusDto d = byFloor.get(e.getValue());
			if (d == null) continue;          // API 응답에 없으면 건너뜀 -> 화면은 기존 임의값 유지
			if (!first) sb.append(",");
			first = false;
			appendZone(sb, e.getKey(), d, "StatusOfParking");
		}

		for (Map.Entry<String, String> e : SHORT_ZONE_MAP.entrySet()) {
			ParkingStatusDto d = byShortZone.get(e.getValue());
			if (d == null) continue;
			if (!first) sb.append(",");
			first = false;
			appendZone(sb, e.getKey(), d, "ParkLocationData");
		}

		sb.append("}");

		response.setContentType("application/json; charset=UTF-8");
		PrintWriter out = response.getWriter();
		out.print(sb.toString());
		out.flush();
	}

	// 구역 한 건을 JSON 오브젝트로 붙인다 (두 API 결과를 같은 모양으로 맞추는 부분)
	private void appendZone(StringBuilder sb, String label, ParkingStatusDto d, String source) {
		sb.append("\"").append(label).append("\":{");
		sb.append("\"floor\":\"").append(escape(d.getFloor())).append("\",");
		sb.append("\"parking\":").append(d.getParking()).append(",");
		sb.append("\"total\":").append(d.getParkingArea()).append(",");
		sb.append("\"remain\":").append(d.getRemain()).append(",");
		sb.append("\"status\":\"").append(escape(d.getStatusLabel())).append("\",");
		sb.append("\"datetm\":\"").append(escape(d.getDateTm())).append("\",");
		sb.append("\"source\":\"").append(escape(source)).append("\"");
		sb.append("}");
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
