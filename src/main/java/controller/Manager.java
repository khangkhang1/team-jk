package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.manager.GateIn;
import command.manager.GateOut;
import common.CommonExecute;
import common.CommonUtil;
import common.FeeRule;
import dao.ManagerDao;

/**
 * 관리자 콘솔 서블릿 - 강선구 담당. 전 화면 관리자(sessionLevel == "top") 전용.
 *
 *   Manager                        대시보드
 *   Manager?t_gubun=gate           입·출차 처리 (t_reservation_id 로 조회)
 *   Manager?t_gubun=gateIn         입차 처리   command.manager.GateIn  → common_alert.jsp
 *   Manager?t_gubun=gateOut        출차+정산   command.manager.GateOut → common_alert.jsp
 *   Manager?t_gubun=reservation    예약 관리 (t_select / t_search / t_status / t_nowPage)
 *   Manager?t_gubun=seat           좌석·구역 현황 (t_lot)
 *   Manager?t_gubun=sales          매출 통계 (t_from / t_to)
 *   Manager?t_gubun=salesCsv       매출 통계 CSV 내려받기 (엑셀에서 바로 열림)
 *   Manager?t_gubun=notice / ai    준비 중 자리표시
 *   FAQ 관리는 기존 Faq 서블릿.
 */
@WebServlet("/Manager")
public class Manager extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final int LIST_PER_PAGE = 15;

	// 아직 화면이 없는 메뉴 (공지사항은 정규 파트, AI 는 보류)
	private static final Map<String, String> COMING_SOON = new LinkedHashMap<>();
	static {
		COMING_SOON.put("notice", "공지사항 관리");
		COMING_SOON.put("ai", "AI 어시스턴트");
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");

		if (!"top".equals(request.getSession().getAttribute("sessionLevel"))) {
			request.setAttribute("t_msg", "관리자만 접근할 수 있는 화면입니다.");
			request.setAttribute("t_url", "Member");
			forward(request, response, "common_alert.jsp");
			return;
		}

		String gubun = request.getParameter("t_gubun");
		if (gubun == null) gubun = "dashboard";

		ManagerDao dao = new ManagerDao();

		if (COMING_SOON.containsKey(gubun)) {
			request.setAttribute("activeMenu", gubun);
			request.setAttribute("pageTitle", COMING_SOON.get(gubun));
			forward(request, response, "manager/coming_soon.jsp");

		} else if (gubun.equals("gateIn")) {
			CommonExecute cmd = new GateIn();
			cmd.execute(request);
			forward(request, response, "common_alert.jsp");

		} else if (gubun.equals("gateOut")) {
			CommonExecute cmd = new GateOut();
			cmd.execute(request);
			forward(request, response, "common_alert.jsp");

		} else if (gubun.equals("gate")) {
			gate(request, dao);
			forward(request, response, "manager/gate.jsp");

		} else if (gubun.equals("reservation")) {
			reservationList(request, dao);
			forward(request, response, "manager/reservation_list.jsp");

		} else if (gubun.equals("seat")) {
			seat(request, dao);
			forward(request, response, "manager/seat.jsp");

		} else if (gubun.equals("sales")) {
			sales(request, dao);
			forward(request, response, "manager/sales.jsp");

		} else if (gubun.equals("salesCsv")) {
			salesCsv(request, response, dao);

		} else {
			dashboard(request, dao);
			forward(request, response, "manager/dashboard.jsp");
		}
	}

	// ---------------------------------------------------------------- 대시보드
	private void dashboard(HttpServletRequest request, ManagerDao dao) {
		request.setAttribute("kpi",    dao.getKpi());
		request.setAttribute("daily",  dao.getDailyStats(14));
		request.setAttribute("lots",   dao.getLotOccupancy());
		request.setAttribute("unsold", dao.getUnsoldByLot(30));
		request.setAttribute("recent", dao.getRecentReservations(10));
		request.setAttribute("check",  dao.getIntegrityCheck());
		request.setAttribute("activeMenu", "dashboard");
		request.setAttribute("pageTitle", "대시보드");
	}

	// ---------------------------------------------------------------- 입·출차 처리
	// 예약번호가 오면 상세 + 결제 내역 + (주차 중이면) 지금 출차할 때의 정산 미리보기까지 만들어 준다
	private void gate(HttpServletRequest request, ManagerDao dao) {
		String rid = CommonUtil.getCheckNull(request.getParameter("t_reservation_id")).trim().toUpperCase();

		request.setAttribute("todayList", dao.getGateTodayList());
		request.setAttribute("rid", rid);

		if (!rid.equals("")) {
			HashMap<String, Object> view = dao.getReservationView(rid);
			if (view.isEmpty()) {
				request.setAttribute("notFound", Boolean.TRUE);
			} else {
				request.setAttribute("view", view);
				request.setAttribute("payments", dao.getPaymentList(rid));

				if ("2".equals(String.valueOf(view.get("status")))) {
					String type  = String.valueOf(view.get("reservation_type"));
					double hours = ((Number) view.get("hours_now")).doubleValue();
					int deposit  = ((Number) view.get("paid_deposit")).intValue();
					int total    = FeeRule.totalFee(type, hours);
					request.setAttribute("feeHours",   FeeRule.billableHours(hours));
					request.setAttribute("feeRate",    FeeRule.hourlyRate(type));
					request.setAttribute("feeTotal",   total);
					request.setAttribute("feeDeposit", deposit);
					request.setAttribute("feeDue",     total - deposit);
				}
			}
		}
		request.setAttribute("activeMenu", "gate");
		request.setAttribute("pageTitle", "입·출차 처리");
	}

	// ---------------------------------------------------------------- 예약 관리
	private void reservationList(HttpServletRequest request, ManagerDao dao) {
		String select = CommonUtil.getCheckNull(request.getParameter("t_select"));
		String search = CommonUtil.getCheckNull(request.getParameter("t_search")).trim();
		String status = CommonUtil.getCheckNull(request.getParameter("t_status"));
		if (select.equals("")) select = "reservation_id";

		String nowPage = request.getParameter("t_nowPage");
		int current_page = (nowPage == null || !nowPage.matches("[0-9]+")) ? 1 : Integer.parseInt(nowPage);

		int totalCount = dao.getReservationCount(select, search, status);
		int total_page = totalCount / LIST_PER_PAGE;
		if (totalCount % LIST_PER_PAGE != 0) total_page = total_page + 1;
		if (total_page == 0) total_page = 1;
		if (current_page > total_page) current_page = total_page;

		int start = (current_page - 1) * LIST_PER_PAGE + 1;
		int end   = current_page * LIST_PER_PAGE;

		request.setAttribute("dtos", dao.getReservationList(select, search, status, start, end));
		request.setAttribute("select", select);
		request.setAttribute("search", search);
		request.setAttribute("status", status);
		request.setAttribute("totalCount", totalCount);
		request.setAttribute("totalPage", total_page);
		request.setAttribute("nowPage", current_page);
		request.setAttribute("startNo", start);
		request.setAttribute("activeMenu", "reservation");
		request.setAttribute("pageTitle", "예약 관리");
	}

	// ---------------------------------------------------------------- 좌석·구역 현황
	private void seat(HttpServletRequest request, ManagerDao dao) {
		ArrayList<HashMap<String, Object>> lots = dao.getLotOccupancy();

		// 구역은 DB 에 있는 것만 허용. 없거나 이상한 값이면 첫 구역
		String lot = CommonUtil.getCheckNull(request.getParameter("t_lot")).trim().toUpperCase();
		boolean known = false;
		for (HashMap<String, Object> l : lots) {
			if (lot.equals(String.valueOf(l.get("lot_id")))) known = true;
		}
		if (!known && !lots.isEmpty()) lot = String.valueOf(lots.get(0).get("lot_id"));

		ArrayList<HashMap<String, Object>> seats = dao.getSeatStatus(lot);
		int parking = 0, reserved = 0;
		for (HashMap<String, Object> s : seats) {
			String state = String.valueOf(s.get("state"));
			if (state.equals("parking")) parking++;
			else if (state.equals("reserved")) reserved++;
		}

		request.setAttribute("lots", lots);
		request.setAttribute("lot", lot);
		request.setAttribute("seats", seats);
		request.setAttribute("parkingCnt", parking);
		request.setAttribute("reservedCnt", reserved);
		request.setAttribute("freeCnt", seats.size() - parking - reserved);
		request.setAttribute("activeMenu", "seat");
		request.setAttribute("pageTitle", "좌석·구역 현황");
	}

	// ---------------------------------------------------------------- 매출 통계
	// 기간 파라미터 검증. 형식이 아니면 이번 달 1일 ~ 오늘
	private String[] period(HttpServletRequest request) {
		String from = CommonUtil.getCheckNull(request.getParameter("t_from"));
		String to   = CommonUtil.getCheckNull(request.getParameter("t_to"));
		LocalDate today = LocalDate.now();
		if (!from.matches("\\d{4}-\\d{2}-\\d{2}")) from = today.withDayOfMonth(1).toString();
		if (!to.matches("\\d{4}-\\d{2}-\\d{2}"))   to   = today.toString();
		if (from.compareTo(to) > 0) { String t = from; from = to; to = t; }   // 순서가 뒤집혀 있으면 바꿔준다
		return new String[] { from, to };
	}

	private void sales(HttpServletRequest request, ManagerDao dao) {
		String[] p = period(request);
		request.setAttribute("from", p[0]);
		request.setAttribute("to",   p[1]);
		request.setAttribute("summary",  dao.getSalesSummary(p[0], p[1]));
		request.setAttribute("daily",    dao.getSalesDaily(p[0], p[1]));
		request.setAttribute("byLot",    dao.getSalesByLot(p[0], p[1]));
		request.setAttribute("byMethod", dao.getSalesByMethod(p[0], p[1]));
		request.setAttribute("activeMenu", "sales");
		request.setAttribute("pageTitle", "매출 통계");
	}

	// CSV 내려받기. 엑셀이 한글을 깨뜨리지 않도록 맨 앞에 UTF-8 BOM 을 붙인다.
	// (일본 현장은 엑셀 보고가 많아서 "화면 → 엑셀" 한 번에 가는 버튼을 둔 것. 라이브러리 없이 CSV 로 충분)
	private void salesCsv(HttpServletRequest request, HttpServletResponse response, ManagerDao dao) throws IOException {
		String[] p = period(request);
		String fileName = "sales_" + p[0].replace("-", "") + "_" + p[1].replace("-", "") + ".csv";

		StringBuilder sb = new StringBuilder("﻿");
		sb.append("매출 통계,").append(p[0]).append(" ~ ").append(p[1]).append("\r\n\r\n");

		HashMap<String, Object> s = dao.getSalesSummary(p[0], p[1]);
		sb.append("구분,금액\r\n");
		sb.append("확정 매출,").append(s.get("confirmed_sales")).append("\r\n");
		sb.append("입금 합계,").append(s.get("paid_in")).append("\r\n");
		sb.append("환불,").append(s.get("refund")).append("\r\n");
		sb.append("결제 건수,").append(s.get("pay_cnt")).append("\r\n");
		sb.append("출차 완료 예약,").append(s.get("done_cnt")).append("\r\n\r\n");

		appendCsvTable(sb, "일별", "날짜", "day", dao.getSalesDaily(p[0], p[1]));
		appendCsvTable(sb, "구역별", "구역", "lot_id", dao.getSalesByLot(p[0], p[1]));
		appendCsvTable(sb, "결제수단별", "결제수단", "method", dao.getSalesByMethod(p[0], p[1]));

		response.setContentType("text/csv; charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
		PrintWriter out = response.getWriter();
		out.print(sb.toString());
		out.flush();
	}

	private void appendCsvTable(StringBuilder sb, String title, String keyLabel, String keyName,
			ArrayList<HashMap<String, Object>> rows) {
		sb.append("[").append(title).append("]\r\n");
		sb.append(keyLabel).append(",확정 매출,입금 합계,환불,결제 건수\r\n");
		for (HashMap<String, Object> r : rows) {
			sb.append(csv(r.get(keyName))).append(",")
			  .append(r.get("confirmed_sales")).append(",")
			  .append(r.get("paid_in")).append(",")
			  .append(r.get("refund")).append(",")
			  .append(r.get("pay_cnt")).append("\r\n");
		}
		sb.append("\r\n");
	}

	// 값에 쉼표·따옴표가 있으면 따옴표로 감싼다 (CSV 규칙)
	private String csv(Object v) {
		String s = v == null ? "" : String.valueOf(v);
		if (s.contains(",") || s.contains("\"") || s.contains("\n")) {
			s = "\"" + s.replace("\"", "\"\"") + "\"";
		}
		return s;
	}

	private void forward(HttpServletRequest request, HttpServletResponse response, String page)
			throws ServletException, IOException {
		RequestDispatcher rd = request.getRequestDispatcher(page);
		rd.forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
