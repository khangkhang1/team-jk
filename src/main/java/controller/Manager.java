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
