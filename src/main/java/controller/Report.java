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

import command.report.ReportSave;
import common.CommonExecute;
import common.CommonUtil;

@WebServlet("/Report")
public class Report extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public static final Map<String, String> TYPES = new LinkedHashMap<>();
	static {
		TYPES.put("1", "자리 무단점유");
		TYPES.put("2", "시설 파손·고장");
		TYPES.put("3", "차량 훼손");
		TYPES.put("4", "불법 주차");
		TYPES.put("5", "기타 문의");
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");

		if (request.getSession().getAttribute("sessionId") == null) {
			request.setAttribute("t_msg", "로그인 후 문의할 수 있습니다.");
			request.setAttribute("t_url", "Member");
			forward(request, response, "common_alert.jsp");
			return;
		}

		String gubun = CommonUtil.getCheckNull(request.getParameter("t_gubun"));

		if (gubun.equals("save")) {
			CommonExecute cmd = new ReportSave();
			cmd.execute(request);
			forward(request, response, "common_alert.jsp");

		} else {
			String type = CommonUtil.getCheckNull(request.getParameter("t_report_type")).trim();
			request.setAttribute("types", TYPES);
			request.setAttribute("type", TYPES.containsKey(type) ? type : "");
			forward(request, response, "report/report_write.jsp");
		}
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
