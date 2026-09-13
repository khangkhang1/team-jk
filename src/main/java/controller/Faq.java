package controller;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.faq.FaqDelete;
import command.faq.FaqSave;
import command.faq.FaqUpdate;
import common.CommonExecute;
import dao.FaqDao;
import dto.FaqDto;

/**
 * FAQ(자주 묻는 질문) 서블릿 - 강선구 담당.
 * 팀의 Member 서블릿, 개인프로젝트 Player 서블릿과 같은 프론트컨트롤러 + Command 구조.
 *
 *   Faq                       목록. t_category 가 있으면 그 카테고리만
 *   Faq?t_gubun=writeForm     등록 화면                      (관리자)
 *   Faq?t_gubun=save          등록 처리 -> common_alert.jsp  (관리자) command.faq.FaqSave
 *   Faq?t_gubun=updateForm    수정 화면                      (관리자)
 *   Faq?t_gubun=update        수정 처리 -> common_alert.jsp  (관리자) command.faq.FaqUpdate
 *   Faq?t_gubun=delete        삭제 처리 -> common_alert.jsp  (관리자) command.faq.FaqDelete
 *
 * 관리자 판별 : 세션 sessionLevel == "top" (MemberLogin 이 manager 로그인 때 넣어줌)
 */
@WebServlet("/Faq")
public class Faq extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String gubun = request.getParameter("t_gubun");
		if (gubun == null) gubun = "list";

		// 목록 말고는 전부 관리자 전용. 화면에서 버튼을 숨겨도 주소를 직접 칠 수 있으니 서버에서 막는다.
		boolean adminOnly = gubun.equals("writeForm") || gubun.equals("save")
				|| gubun.equals("updateForm") || gubun.equals("update")
				|| gubun.equals("delete");

		if (adminOnly && !isAdmin(request)) {
			request.setAttribute("t_msg", "관리자만 이용할 수 있는 기능입니다.");
			request.setAttribute("t_url", "Faq");
			forward(request, response, "common_alert.jsp");
			return;
		}

		if (gubun.equals("writeForm")) {
			forward(request, response, "faq/faq_write.jsp");
			return;
		} else if (gubun.equals("save")) {
			CommonExecute cmd = new FaqSave();
			cmd.execute(request);
			forward(request, response, "common_alert.jsp");
			return;
		} else if (gubun.equals("updateForm")) {
			int faq_id = parseId(request.getParameter("t_faq_id"));
			FaqDto dto = new FaqDao().getFaqView(faq_id);
			if (dto == null) {
				request.setAttribute("t_msg", "존재하지 않는 글입니다.");
				request.setAttribute("t_url", "Faq");
				forward(request, response, "common_alert.jsp");
				return;
			}
			request.setAttribute("dto", dto);
			forward(request, response, "faq/faq_update.jsp");
			return;
		} else if (gubun.equals("update")) {
			CommonExecute cmd = new FaqUpdate();
			cmd.execute(request);
			forward(request, response, "common_alert.jsp");
			return;
		} else if (gubun.equals("delete")) {
			CommonExecute cmd = new FaqDelete();
			cmd.execute(request);
			forward(request, response, "common_alert.jsp");
			return;
		}

		// 기본 : 목록. 카테고리 파라미터가 없으면 전체.
		String category = request.getParameter("t_category");
		if (category == null) category = "";
		boolean isAdmin = isAdmin(request);

		ArrayList<FaqDto> dtos = new FaqDao().getFaqList(category, isAdmin);

		request.setAttribute("dtos", dtos);
		request.setAttribute("category", category);
		request.setAttribute("isAdmin", isAdmin);
		forward(request, response, "faq/faq_list.jsp");
	}

	private boolean isAdmin(HttpServletRequest request) {
		return "top".equals(request.getSession().getAttribute("sessionLevel"));
	}

	// 글번호 파라미터 -> int. 없거나 숫자가 아니면 0 (조회하면 "없는 글" 처리로 이어짐)
	public static int parseId(String s) {
		if (s != null && s.matches("[0-9]+")) return Integer.parseInt(s);
		return 0;
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
