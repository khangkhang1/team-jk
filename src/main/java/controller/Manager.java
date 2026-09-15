package controller;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.ManagerDao;

/**
 * 관리자 화면 서블릿 - 강선구 담당.
 *   Manager                    대시보드 (기본)
 *   Manager?t_gubun=gate       입·출차 처리      (준비 중)
 *   Manager?t_gubun=reservation 예약 관리        (준비 중)
 *   Manager?t_gubun=seat       좌석·구역 현황     (준비 중)
 *   Manager?t_gubun=sales      매출 통계          (준비 중)
 *   Manager?t_gubun=notice     공지사항 관리      (준비 중)
 *   Manager?t_gubun=ai         AI 어시스턴트      (준비 중)
 *   FAQ 관리는 기존 Faq 서블릿을 그대로 쓴다.
 *
 * 전 화면 관리자 전용 : 세션 sessionLevel == "top" 이 아니면 로그인 화면으로 보낸다.
 */
@WebServlet("/Manager")
public class Manager extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// 아직 화면이 없는 메뉴. 사이드 메뉴 이름과 같이 관리한다.
	private static final Map<String, String> COMING_SOON = new LinkedHashMap<>();
	static {
		COMING_SOON.put("gate", "입·출차 처리");
		COMING_SOON.put("reservation", "예약 관리");
		COMING_SOON.put("seat", "좌석·구역 현황");
		COMING_SOON.put("sales", "매출 통계");
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

		if (COMING_SOON.containsKey(gubun)) {
			request.setAttribute("activeMenu", gubun);
			request.setAttribute("pageTitle", COMING_SOON.get(gubun));
			forward(request, response, "manager/coming_soon.jsp");
			return;
		}

		// 기본 : 대시보드
		ManagerDao dao = new ManagerDao();
		request.setAttribute("kpi",    dao.getKpi());
		request.setAttribute("daily",  dao.getDailyStats(14));
		request.setAttribute("lots",   dao.getLotOccupancy());
		request.setAttribute("unsold", dao.getUnsoldByLot(30));
		request.setAttribute("recent", dao.getRecentReservations(10));
		request.setAttribute("check",  dao.getIntegrityCheck());
		request.setAttribute("activeMenu", "dashboard");
		request.setAttribute("pageTitle", "대시보드");
		forward(request, response, "manager/dashboard.jsp");
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
