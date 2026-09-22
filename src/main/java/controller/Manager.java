package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
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

import command.faq.FaqDelete;
import command.faq.FaqSave;
import command.faq.FaqUpdate;
import command.manager.GateIn;
import command.manager.GateOut;
import command.manager.NoticeDelete;
import command.manager.NoticeSave;
import command.manager.NoticeUpdate;
import command.manager.ReportAnswer;
import common.CommonExecute;
import common.CommonUtil;
import common.FeeRule;
import dao.FaqDao;
import dao.ManagerDao;
import dao.NoticeDao;
import dao.ReportDao;
import dto.FaqDto;
import dto.NoticeDto;
import dto.ReportDto;

/**
 * 관리자 콘솔 서블릿 - 강선구 담당. 전 화면 관리자(sessionLevel == "top") 전용.
 *
 *   Manager                        대시보드
 *   Manager?t_gubun=gate           입·출차 처리 (t_reservation_id 로 조회)
 *   Manager?t_gubun=gateIn         입차 처리   command.manager.GateIn  → common_alert.jsp
 *   Manager?t_gubun=gateOut        출차+정산   command.manager.GateOut → common_alert.jsp
 *   Manager?t_gubun=reservation    예약 관리 (t_select / t_search / t_status / t_nowPage)
 *   Manager?t_gubun=report         신고 내역 (t_select / t_search / t_status / t_type / t_nowPage)
 *   Manager?t_gubun=reportView     신고 상세 (t_report_id)
 *   Manager?t_gubun=reportAnswer   신고 처리  command.manager.ReportAnswer → common_alert.jsp
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

	// 아직 화면이 없는 메뉴 (AI 는 팀 상담 후로 보류)
	private static final Map<String, String> COMING_SOON = new LinkedHashMap<>();
	static {
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

		// 사이드 메뉴의 "신고 내역" 옆에 미처리 건수를 항상 띄운다.
		// 다른 화면을 보고 있어도 밀린 신고가 눈에 들어오게 하려는 것. 화면을 그리지 않는 처리에는 붙이지 않는다.
		if (!gubun.equals("salesCsv") && !gubun.equals("gateIn") && !gubun.equals("gateOut")
				&& !gubun.equals("reportAnswer")) {
			request.setAttribute("reportWaiting", new ReportDao().getWaitingCount());
		}

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

		} else if (gubun.equals("report")) {
			reportList(request);
			forward(request, response, "manager/report_list.jsp");

		} else if (gubun.equals("reportView")) {
			reportView(request);
			forward(request, response, "manager/report_view.jsp");

		} else if (gubun.equals("reportAnswer")) {
			CommonExecute cmd = new ReportAnswer();
			cmd.execute(request);
			forward(request, response, "common_alert.jsp");

		} else if (gubun.equals("faq")) {
			faqList(request);
			forward(request, response, "manager/faq_list.jsp");

		} else if (gubun.equals("faqForm")) {
			faqForm(request);
			forward(request, response, "manager/faq_form.jsp");

		} else if (gubun.equals("faqSave") || gubun.equals("faqUpdate") || gubun.equals("faqDelete")) {
			// 저장/수정/삭제 자체는 이용자 화면과 하는 일이 같아서 command.faq.* 를 그대로 쓴다.
			// 다만 끝난 뒤 돌아갈 곳만 관리자 목록으로 바꾼다 (기존 커맨드는 이용자 FAQ 로 보낸다).
			CommonExecute cmd = gubun.equals("faqSave") ? new FaqSave()
					: gubun.equals("faqUpdate") ? new FaqUpdate() : new FaqDelete();
			cmd.execute(request);
			backToManager(request, "faq", "faqForm", "t_faq_id");
			forward(request, response, "common_alert.jsp");

		} else if (gubun.equals("notice")) {
			noticeList(request);
			forward(request, response, "manager/notice_list.jsp");

		} else if (gubun.equals("noticeForm")) {
			noticeForm(request);
			forward(request, response, "manager/notice_form.jsp");

		} else if (gubun.equals("noticeSave")) {
			CommonExecute cmd = new NoticeSave();
			cmd.execute(request);
			forward(request, response, "common_alert.jsp");

		} else if (gubun.equals("noticeUpdate")) {
			CommonExecute cmd = new NoticeUpdate();
			cmd.execute(request);
			forward(request, response, "common_alert.jsp");

		} else if (gubun.equals("noticeDelete")) {
			CommonExecute cmd = new NoticeDelete();
			cmd.execute(request);
			forward(request, response, "common_alert.jsp");

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
		request.setAttribute("reportWait", new ReportDao().getWaitingList(5));   // 아직 안 끝난 신고 (오래된 순)
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
		if (current_page < 1) current_page = 1;                 // 주소창에 0 을 넣어도 빈 목록이 나오지 않게

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

	// ---------------------------------------------------------------- 신고 내역
	// 이용자가 올린 신고를 접수 → 처리 중 → 처리 완료/반려로 관리하는 화면.
	// 전화로 받아 적던 것을 화면에 남기면 누가·언제·어떻게 처리했는지가 기록으로 남는다 (対応履歴の可視化).
	private void reportList(HttpServletRequest request) {
		ReportDao dao = new ReportDao();

		String select = CommonUtil.getCheckNull(request.getParameter("t_select"));
		String search = CommonUtil.getCheckNull(request.getParameter("t_search")).trim();
		String status = CommonUtil.getCheckNull(request.getParameter("t_status"));
		String type   = CommonUtil.getCheckNull(request.getParameter("t_type"));
		if (select.equals("")) select = "title";
		// 코드값이 아니면 "전체"로 본다. 이상한 값이 그대로 화면 선택박스에 남지 않게 한다.
		if (!status.matches("[1-4]")) status = "";
		if (!type.matches("[1-5]"))   type   = "";

		String nowPage = request.getParameter("t_nowPage");
		int current_page = (nowPage == null || !nowPage.matches("[0-9]+")) ? 1 : Integer.parseInt(nowPage);
		if (current_page < 1) current_page = 1;                 // 주소창에 0 을 넣어도 빈 목록이 나오지 않게

		int totalCount = dao.getReportCount(select, search, status, type);
		int total_page = totalCount / LIST_PER_PAGE;
		if (totalCount % LIST_PER_PAGE != 0) total_page = total_page + 1;
		if (total_page == 0) total_page = 1;
		if (current_page > total_page) current_page = total_page;

		int start = (current_page - 1) * LIST_PER_PAGE + 1;
		int end   = current_page * LIST_PER_PAGE;

		request.setAttribute("dtos", dao.getReportList(select, search, status, type, start, end));
		request.setAttribute("select", select);
		request.setAttribute("search", search);
		request.setAttribute("status", status);
		request.setAttribute("type", type);
		request.setAttribute("totalCount", totalCount);
		request.setAttribute("totalPage", total_page);
		request.setAttribute("nowPage", current_page);
		request.setAttribute("startNo", start);
		request.setAttribute("activeMenu", "report");
		request.setAttribute("pageTitle", "문의 내역");
	}

	private void reportView(HttpServletRequest request) {
		String rid = CommonUtil.getCheckNull(request.getParameter("t_report_id")).trim();

		if (rid.matches("[0-9]+")) {
			ReportDto dto = new ReportDao().getReportView(Integer.parseInt(rid));
			if (dto != null) request.setAttribute("dto", dto);      // 없으면 화면이 "없는 신고번호" 를 보여준다
		}
		request.setAttribute("rid", rid);
		request.setAttribute("listUrl", listQuery(request));
		request.setAttribute("activeMenu", "report");
		request.setAttribute("pageTitle", "문의 상세");
	}

	// 상세에서 "목록으로" 를 눌렀을 때 보던 검색 조건·페이지로 돌아가게 주소를 만들어 둔다.
	// 조건을 잃어버리면 관리자가 매번 다시 검색해야 한다 (한 건 처리하고 목록 → 다음 건 처리의 반복이라 체감이 크다).
	private String listQuery(HttpServletRequest request) {
		StringBuilder sb = new StringBuilder("Manager?t_gubun=report");
		String[] keys = { "t_select", "t_search", "t_status", "t_type", "t_nowPage" };
		for (String key : keys) {
			String value = CommonUtil.getCheckNull(request.getParameter(key));
			if (!value.equals("")) {
				try {
					// 한글 검색어가 주소에 그대로 들어가면 깨지므로 인코딩한다
					sb.append("&").append(key).append("=").append(URLEncoder.encode(value, "UTF-8"));
				} catch (Exception ignore) {
				}
			}
		}
		return sb.toString();
	}

	// ---------------------------------------------------------------- FAQ 관리
	// 이용자 화면(/Faq)과 같은 DAO 를 쓰되, 목록·등록·수정을 관리자 레이아웃 안에서 끝낸다.
	// 관리 중에 이용자 화면으로 튕겨 나갔다 돌아오면 흐름이 끊긴다 (画面遷移を減らす).

	// FaqDao 는 카테고리를 SQL 문자열에 그대로 붙이므로, 화면에서 온 값을 그대로 넘기지 않고
	// 정해둔 목록에 있는 것만 통과시킨다 (SQL 인젝션 방지).
	private static final String[] FAQ_CATEGORIES = { "예약", "요금·결제", "입·출차", "항공편" };

	private String faqCategory(HttpServletRequest request) {
		String category = CommonUtil.getCheckNull(request.getParameter("t_category")).trim();
		for (String c : FAQ_CATEGORIES) {
			if (c.equals(category)) return category;
		}
		return "";
	}

	private void faqList(HttpServletRequest request) {
		String category = faqCategory(request);
		ArrayList<FaqDto> dtos = new FaqDao().getFaqList(category, true);   // true : 숨김(use_yn=N) 글도 본다

		int hidden = 0;
		for (FaqDto d : dtos) {
			if ("N".equals(d.getUse_yn())) hidden++;
		}

		request.setAttribute("dtos", dtos);
		request.setAttribute("category", category);
		request.setAttribute("categories", FAQ_CATEGORIES);
		request.setAttribute("totalCount", dtos.size());
		request.setAttribute("hiddenCount", hidden);
		request.setAttribute("activeMenu", "faq");
		request.setAttribute("pageTitle", "FAQ 관리");
	}

	// 등록·수정 겸용 화면. t_faq_id 가 있으면 수정.
	private void faqForm(HttpServletRequest request) {
		int faqId = Faq.parseId(request.getParameter("t_faq_id"));
		if (faqId > 0) {
			FaqDto dto = new FaqDao().getFaqView(faqId);
			if (dto != null) request.setAttribute("dto", dto);
		}
		request.setAttribute("categories", FAQ_CATEGORIES);
		request.setAttribute("activeMenu", "faq");
		request.setAttribute("pageTitle", "FAQ 관리");
	}

	// 재사용한 command.faq.* 가 정해둔 이동 주소(이용자 FAQ)를 관리자 콘솔 주소로 바꿔준다
	private void backToManager(HttpServletRequest request, String listGubun, String formGubun, String idParam) {
		String url = CommonUtil.getCheckNull((String) request.getAttribute("t_url"));
		String id  = CommonUtil.getCheckNull(request.getParameter(idParam)).trim();

		if (url.contains("Form")) {          // 입력이 잘못돼 폼으로 되돌리는 경우
			request.setAttribute("t_url", "Manager?t_gubun=" + formGubun
					+ (id.equals("") ? "" : "&" + idParam + "=" + id));
		} else {
			request.setAttribute("t_url", "Manager?t_gubun=" + listGubun);
		}
	}

	// ---------------------------------------------------------------- 공지사항 관리
	private void noticeList(HttpServletRequest request) {
		String search = CommonUtil.getCheckNull(request.getParameter("t_search")).trim();
		ArrayList<NoticeDto> dtos = new NoticeDao().getNoticeList(search);

		int important = 0;
		for (NoticeDto d : dtos) {
			if ("Y".equals(d.getImportant())) important++;
		}

		request.setAttribute("dtos", dtos);
		request.setAttribute("search", search);
		request.setAttribute("totalCount", dtos.size());
		request.setAttribute("importantCount", important);
		request.setAttribute("activeMenu", "notice");
		request.setAttribute("pageTitle", "공지사항 관리");
	}

	// 등록·수정 겸용 화면. t_no 가 있으면 수정.
	private void noticeForm(HttpServletRequest request) {
		String no = CommonUtil.getCheckNull(request.getParameter("t_no")).trim();
		if (!no.equals("")) {
			NoticeDto dto = new NoticeDao().getNoticeView(no);
			if (dto != null) request.setAttribute("dto", dto);
		}
		request.setAttribute("activeMenu", "notice");
		request.setAttribute("pageTitle", "공지사항 관리");
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
