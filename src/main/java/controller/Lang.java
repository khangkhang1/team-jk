package controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import common.I18n;

/**
 * 화면 언어 전환. Lang?t_lang=ko|ja&t_back=돌아갈주소
 *
 * 선택한 언어는 세션에 저장되고(JSTL fmt 설정값), 이후 모든 JSP 의 <fmt:message> 가 그 언어의
 * messages_xx.properties 를 읽는다. 기본은 한국어(web.xml). 관리자 콘솔은 한국어 고정.
 */
@WebServlet("/Lang")
public class Lang extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String lang = request.getParameter("t_lang");
		I18n.setLang(request, lang);

		// 보던 화면으로 되돌아간다. 외부 주소로 튀지 않게 같은 컨텍스트 안 경로만 허용
		String back = request.getParameter("t_back");
		String ctx = request.getContextPath();
		if (back == null || !back.startsWith(ctx + "/") || back.contains("//") || back.contains("\r") || back.contains("\n")) {
			back = ctx + "/ParkingStatus";
		}
		response.sendRedirect(back);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
