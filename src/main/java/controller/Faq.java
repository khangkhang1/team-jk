package controller;

import common.I18n;

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
			request.setAttribute("t_msg", I18n.msg(request, "msg.adminOnly"));
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
				request.setAttribute("t_msg", I18n.msg(request, "msg.notFound"));
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

		String category = request.getParameter("t_category");
		if (category == null) category = "";
		boolean isAdmin = isAdmin(request);

		ArrayList<FaqDto> dtos = new FaqDao().getFaqList(category, isAdmin);
		// 일본어 모드면 content_ja.properties 에 번역이 있는 글은 번역으로 바꿔 보여준다 (없으면 원문)
		for (FaqDto d : dtos) {
			d.setCategory(I18n.content(request, "faq.cat." + d.getCategory(), d.getCategory()));
			d.setQuestion(I18n.content(request, "faq." + d.getFaq_id() + ".question", d.getQuestion()));
			d.setAnswer(I18n.content(request, "faq." + d.getFaq_id() + ".answer", d.getAnswer()));
		}

		request.setAttribute("dtos", dtos);
		request.setAttribute("category", category);
		request.setAttribute("isAdmin", isAdmin);
		forward(request, response, "faq/faq_list.jsp");
	}

	private boolean isAdmin(HttpServletRequest request) {
		return "top".equals(request.getSession().getAttribute("sessionLevel"));
	}

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
