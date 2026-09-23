package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.FlightApiDao;
import dto.FlightStatusDto;

/**
 * 항공편 관련 요청 담당 서블릿 (강선구 담당).
 *
 * t_gubun=arrivals : 도착편 목록을 JSON으로 반환 (AJAX 전용) - flight/flight_search.jsp 가 사용
 * t_gubun=search   : 입력한 편명 접두어로 시작하는 항공편을 JSON 배열로 반환 (자동완성용)
 * t_gubun=list     : (비워둠) 강선구 손코딩 연습 자리 - flight_list.jsp 로 forward 하는 방식
 *
 * 두 AJAX 분기 모두 DB를 거치지 않는다. 검색할 때마다 쓰지도 않을 항공편을 DB에 쌓을 이유가 없어서,
 * 실시간 API 결과를 그대로 내려준다. DB(icn_flight)에는 예약을 확정한 편 1건만 FlightDao가 저장한다.
 *
 * 예약 저장/결항 재확인은 예약 테이블(오윤섭 담당)이 완성된 뒤에
 * command.flight 패키지에 커맨드로 추가할 예정.
 */
@WebServlet("/Flight")
public class Flight extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final int SEARCH_LIMIT = 50;   // 자동완성은 50건이면 충분 ("K" 한 글자에 수백 건이 나옴)

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String gubun = request.getParameter("t_gubun");

		if ("arrivals".equals(gubun)) {
			arrivals(request, response);
			return;
		}
		if ("search".equals(gubun)) {
			searchFlight(request, response);
			return;
		}

		// 예전엔 여기서 아무것도 안 하고 빈 화면을 돌려줘서, 주소를 잘못 쳐도 원인을 알 수 없었다.
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		response.setContentType("text/plain; charset=UTF-8");
		response.getWriter().print("지원하지 않는 t_gubun 입니다 : " + gubun + "\n(사용 가능 : arrivals, search)");
	}

	// ─────────────────────────────────────────────────────────────
	// 도착편 목록 (AJAX, JSON)
	//
	// 파라미터
	//   t_searchday : yyyyMMdd. 없으면 오늘. 조회 가능 범위는 오늘-3일 ~ 오늘+6일
	//   t_keyword   : 편명/항공사/출발지/공항코드 일부. 공동운항 편명(AA8905 등)으로도 찾아진다
	//   t_status    : all(기본) / scheduled(예정) / arrived(도착·착륙) / delayed(지연) / cancelled(결항)
	//   t_terminal  : all(기본) / T1 / T2
	//
	// 응답
	//   성공 : {"ok":true, "searchday":..., "total":실제운항편수, "count":조건에맞는수, "counts":{...}, "flights":[...]}
	//   실패 : {"ok":false, "message":"원인"}  + HTTP 400/500
	//          -> 화면이 이 message를 그대로 보여준다. secret.properties 누락 안내문도 여기로 나온다.
	//
	// 공동운항 편명(Slave)은 따로 줄을 만들지 않고 실제 운항편 줄의 codeshares 에 넣는다.
	// 오늘 실측으로 1,156건 중 622건이 공동운항이라, 그대로 뿌리면 같은 비행기가 서너 번씩 보인다.
	// ─────────────────────────────────────────────────────────────
	private void arrivals(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String keyword = trim(request.getParameter("t_keyword")).toUpperCase();
		String status = trim(request.getParameter("t_status"));
		String terminal = trim(request.getParameter("t_terminal"));

		FlightApiDao.ArrivalsOfDay day;
		try {
			day = new FlightApiDao().getArrivalsOfDay(request.getParameter("t_searchday"));
		} catch (IllegalArgumentException e) {      // 날짜 형식·범위 오류 = 요청 쪽 문제
			writeJson(response, HttpServletResponse.SC_BAD_REQUEST, errorJson(e.getMessage()));
			return;
		} catch (RuntimeException e) {              // 설정 누락 / 인증 실패 / 통신 실패 = 서버 쪽 문제
			e.printStackTrace();
			writeJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorJson(e.getMessage()));
			return;
		}

		Map<FlightStatusDto, List<String>> grouped = FlightApiDao.groupCodeshares(day.getFlights());

		// 상태 버튼 옆에 건수를 띄우려고, 상태 조건만 빼고 먼저 센다
		int cAll = 0, cScheduled = 0, cArrived = 0, cDelayed = 0, cCancelled = 0;
		StringBuilder rows = new StringBuilder();
		int count = 0;

		for (Map.Entry<FlightStatusDto, List<String>> e : grouped.entrySet()) {
			FlightStatusDto f = e.getKey();
			List<String> codeshares = e.getValue();

			if (!matchesTerminal(f, terminal)) continue;
			String matchedBy = matchKeyword(f, codeshares, keyword);
			if (matchedBy == null) continue;

			cAll++;
			if (f.isScheduled()) cScheduled++;
			if (isArrived(f)) cArrived++;
			if (f.isDelayed()) cDelayed++;
			if (f.isCancelled()) cCancelled++;

			if (!matchesStatus(f, status)) continue;

			if (count > 0) rows.append(",");
			appendFlight(rows, f, codeshares, matchedBy);
			count++;
		}

		StringBuilder sb = new StringBuilder();
		sb.append("{\"ok\":true");
		sb.append(",\"searchday\":\"").append(day.getSearchday()).append("\"");
		sb.append(",\"today\":\"").append(FlightApiDao.today()).append("\"");
		sb.append(",\"minDay\":\"").append(FlightApiDao.minDay()).append("\"");
		sb.append(",\"maxDay\":\"").append(FlightApiDao.maxDay()).append("\"");
		sb.append(",\"fetchedAt\":\"").append(formatTime(day.getFetchedAt())).append("\"");
		sb.append(",\"fromCache\":").append(day.isFromCache());
		sb.append(",\"stale\":").append(day.isStale());
		sb.append(",\"total\":").append(grouped.size());
		sb.append(",\"rawTotal\":").append(day.getFlights().size());
		sb.append(",\"count\":").append(count);
		sb.append(",\"counts\":{\"all\":").append(cAll)
			.append(",\"scheduled\":").append(cScheduled)
			.append(",\"arrived\":").append(cArrived)
			.append(",\"delayed\":").append(cDelayed)
			.append(",\"cancelled\":").append(cCancelled).append("}");
		sb.append(",\"flights\":[").append(rows).append("]}");

		writeJson(response, HttpServletResponse.SC_OK, sb.toString());
	}

	// 검색어가 어디에 걸렸는지 돌려준다. 안 걸리면 null.
	// 공동운항 편명으로 걸린 경우 그 편명을 돌려줘서 화면에 "AA8905로 찾음"을 표시한다.
	private String matchKeyword(FlightStatusDto f, List<String> codeshares, String keyword) {
		if (keyword.isEmpty()) return "";
		if (contains(f.getFlightNo(), keyword)) return f.getFlightNo();
		if (contains(f.getAirline(), keyword)
				|| contains(f.getAirport(), keyword)
				|| contains(f.getAirportCode(), keyword)) {
			return "";
		}
		for (String no : codeshares) {
			if (contains(no, keyword)) return no;
		}
		return null;
	}

	private boolean matchesStatus(FlightStatusDto f, String status) {
		switch (status) {
			case "scheduled": return f.isScheduled();
			case "arrived":   return isArrived(f);
			case "delayed":   return f.isDelayed();
			case "cancelled": return f.isCancelled();
			default:          return true;
		}
	}

	private boolean matchesTerminal(FlightStatusDto f, String terminal) {
		if ("T1".equals(terminal)) return "P01".equals(f.getTerminalId()) || "P02".equals(f.getTerminalId());
		if ("T2".equals(terminal)) return "P03".equals(f.getTerminalId());
		return true;
	}

	private boolean isArrived(FlightStatusDto f) {
		return "도착".equals(f.getRemark()) || "착륙".equals(f.getRemark());
	}

	private void appendFlight(StringBuilder sb, FlightStatusDto f, List<String> codeshares, String matchedBy) {
		sb.append("{");
		sb.append("\"flightNo\":\"").append(escape(f.getFlightNo())).append("\",");
		sb.append("\"airline\":\"").append(escape(f.getAirline())).append("\",");
		sb.append("\"airport\":\"").append(escape(f.getAirport())).append("\",");
		sb.append("\"airportCode\":\"").append(escape(f.getAirportCode())).append("\",");
		sb.append("\"scheduleDateTime\":\"").append(escape(f.getScheduleDateTime())).append("\",");
		sb.append("\"estimatedDateTime\":\"").append(escape(f.getEstimatedDateTime())).append("\",");
		sb.append("\"remark\":\"").append(escape(f.getRemark())).append("\",");
		sb.append("\"terminal\":\"").append(escape(f.getTerminalName())).append("\",");
		sb.append("\"exit\":\"").append(escape(f.getGateNumber())).append("\",");
		sb.append("\"carousel\":\"").append(escape(f.getCarousel())).append("\",");
		sb.append("\"matchedBy\":\"").append(escape(matchedBy)).append("\",");
		sb.append("\"codeshares\":[");
		for (int i = 0; i < codeshares.size(); i++) {
			if (i > 0) sb.append(",");
			sb.append("\"").append(escape(codeshares.get(i))).append("\"");
		}
		sb.append("]}");
	}

	// ─────────────────────────────────────────────────────────────
	// 항공편명 자동완성 검색 - 주소 검색 UX와 동일한 방식(4차 회의에서 제안한 방식)
	// 응답은 예전과 같은 JSON 배열. 필드만 몇 개 늘었다(뒤에 붙였으므로 기존 사용처에 영향 없음).
	// 실패하면 HTTP 500 + {"ok":false,"message":...} (예전엔 실패해도 빈 배열이라 원인을 몰랐음)
	// ─────────────────────────────────────────────────────────────
	private void searchFlight(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String prefix = request.getParameter("t_flight_no");
		String searchday = request.getParameter("t_searchday"); // yyyyMMdd, 안 넘어오면 오늘

		List<FlightStatusDto> list;
		try {
			FlightApiDao.ArrivalsOfDay day = new FlightApiDao().getArrivalsOfDay(searchday);
			list = FlightApiDao.filterByPrefix(day.getFlights(), prefix);
		} catch (IllegalArgumentException e) {
			writeJson(response, HttpServletResponse.SC_BAD_REQUEST, errorJson(e.getMessage()));
			return;
		} catch (RuntimeException e) {
			e.printStackTrace();
			writeJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorJson(e.getMessage()));
			return;
		}

		writeJson(response, HttpServletResponse.SC_OK, toJsonArray(list));
	}

	// JSON 라이브러리(gson 등)가 프로젝트에 없어서 직접 문자열로 만듦 - 페이징을 직접 구현한 것과 같은 이유(CommonUtil 참고).
	private String toJsonArray(List<FlightStatusDto> list) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		int n = Math.min(list.size(), SEARCH_LIMIT);
		for (int i = 0; i < n; i++) {
			FlightStatusDto f = list.get(i);
			if (i > 0) sb.append(",");
			sb.append("{");
			sb.append("\"flightNo\":\"").append(escape(f.getFlightNo())).append("\",");
			sb.append("\"airline\":\"").append(escape(f.getAirline())).append("\",");
			sb.append("\"airport\":\"").append(escape(f.getAirport())).append("\",");
			sb.append("\"scheduleDateTime\":\"").append(escape(f.getScheduleDateTime())).append("\",");
			sb.append("\"remark\":\"").append(escape(f.getRemark())).append("\",");
			sb.append("\"terminal\":\"").append(escape(f.getTerminalName())).append("\",");
			sb.append("\"masterFlightId\":\"").append(escape(f.getMasterFlightId())).append("\"");
			sb.append("}");
		}
		sb.append("]");
		return sb.toString();
	}

	// ─────────────────────────────────────────────────────────────
	// 공통
	// ─────────────────────────────────────────────────────────────
	private void writeJson(HttpServletResponse response, int status, String json) throws IOException {
		response.setStatus(status);
		response.setContentType("application/json; charset=UTF-8");
		response.setHeader("Cache-Control", "no-store");   // 브라우저가 옛 결과를 재사용하지 않게
		PrintWriter out = response.getWriter();
		out.print(json);
		out.flush();
	}

	private String errorJson(String message) {
		return "{\"ok\":false,\"message\":\"" + escape(message == null ? "알 수 없는 오류" : message.trim()) + "\"}";
	}

	private String formatTime(long millis) {
		SimpleDateFormat f = new SimpleDateFormat("HH:mm:ss");
		f.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
		return f.format(new Date(millis));
	}

	private static boolean contains(String value, String upperKeyword) {
		return value != null && value.toUpperCase().contains(upperKeyword);
	}

	private static String trim(String s) {
		return s == null ? "" : s.trim();
	}

	// JSON 문자열 이스케이프. 예전엔 \ 와 " 만 처리해서, 줄바꿈이 들어간 에러 메시지
	// (secret.properties 안내문이 여러 줄이다)를 넣으면 JSON 자체가 깨졌다.
	private String escape(String s) {
		if (s == null) return "";
		StringBuilder sb = new StringBuilder(s.length() + 8);
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			switch (c) {
				case '"':  sb.append("\\\""); break;
				case '\\': sb.append("\\\\"); break;
				case '\n': sb.append("\\n"); break;
				case '\r': sb.append("\\r"); break;
				case '\t': sb.append("\\t"); break;
				default:
					if (c < 0x20) {
						sb.append(String.format("\\u%04x", (int) c));
					} else {
						sb.append(c);
					}
			}
		}
		return sb.toString();
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}