package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.FlightApiDao;
import dto.FlightStatusDto;

/**
 * 항공편 관련 요청 담당 서블릿 (강선구 담당).
 * 지금은 예약 페이지의 "항공편 검색"(자동완성) 기능만 붙어 있음.
 * t_gubun=search : 입력한 편명 접두어로 시작하는 항공편을 JSON으로 반환 (AJAX 전용, 화면 forward 없음)
 *
 * 예약 저장/결항 재확인은 예약 테이블(오윤섭 담당)이 완성된 뒤에
 * command.flight 패키지에 커맨드로 추가할 예정 - 지금은 검색까지만.
 */
@WebServlet("/Flight")
public class Flight extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String gubun = request.getParameter("t_gubun");

		if ("search".equals(gubun)) {
			searchFlight(request, response);
			return;
		}
	}

	// 항공편명 자동완성 검색 - 주소 검색 UX와 동일한 방식(4차 회의에서 제안한 방식)
	private void searchFlight(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String prefix = request.getParameter("t_flight_no");
		String searchday = request.getParameter("t_searchday"); // yyyyMMdd, 안 넘어오면 API 기본값(오늘)

		FlightApiDao dao = new FlightApiDao();
		List<FlightStatusDto> list = dao.searchByFlightNoPrefix(prefix, searchday);

		response.setContentType("application/json; charset=UTF-8");
		PrintWriter out = response.getWriter();
		out.print(toJsonArray(list));
		out.flush();
	}

	// JSON 라이브러리(gson 등)가 프로젝트에 없어서 직접 문자열로 만듦 - 페이징을 직접 구현한 것과 같은 이유(CommonUtil 참고).
	// 결과 건수가 많지 않은 자동완성용이라 이 정도 직접 구현으로 충분함.
	private String toJsonArray(List<FlightStatusDto> list) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (int i = 0; i < list.size(); i++) {
			FlightStatusDto f = list.get(i);
			if (i > 0) sb.append(",");
			sb.append("{");
			sb.append("\"flightNo\":\"").append(escape(f.getFlightNo())).append("\",");
			sb.append("\"airline\":\"").append(escape(f.getAirline())).append("\",");
			sb.append("\"airport\":\"").append(escape(f.getAirport())).append("\",");
			sb.append("\"scheduleDateTime\":\"").append(escape(f.getScheduleDateTime())).append("\"");
			sb.append("}");
		}
		sb.append("]");
		return sb.toString();
	}

	private String escape(String s) {
		if (s == null) return "";
		return s.replace("\\", "\\\\").replace("\"", "\\\"");
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
