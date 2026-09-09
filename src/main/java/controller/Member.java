package controller;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.member.MemberLogin;
import command.member.MemberLogout;
import command.member.MemberMyInfo;
import command.member.MemberPasswordUpdate;
import command.member.MemberSave;
import command.member.MemberSendPassword;
import command.member.MemberUpdate;
import common.CommonExecute;

/**
 * Servlet implementation class Member
 */
@WebServlet("/Member")
public class Member extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Member() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		String gubun = request.getParameter("t_gubun");
		String viewPage = "";

		if (gubun == null)
			gubun = "login";
		request.setAttribute("apple_gubun", gubun);

		if (gubun.equals("login")) {
			viewPage = "member/member_login.jsp";
		} else if (gubun.equals("join")) {
			viewPage = "member/member_join.jsp";

		} else if (gubun.equals("memberSave")) {
			MemberSave mem = new MemberSave();
			mem.execute(request);
			viewPage = "common_alert.jsp";

		} else if (gubun.equals("memberLogin")) {
			MemberLogin mem = new MemberLogin();
			mem.execute(request);
			viewPage = "common_alert.jsp";
		} else if (gubun.equals("logout")) {
			MemberLogout mem = new MemberLogout();
			mem.execute(request);
			viewPage = "common_alert.jsp";
		} else if (gubun.equals("myinfo")) {
			String id = (String) request.getSession().getAttribute("sessionId");
			if (id == null) {
				String msg = "로그인 정보가 만료되었습니다.";
				request.setAttribute("t_msg", msg);
				request.setAttribute("t_url", "Member");
				viewPage = "common_alert.jsp";
			} else {
				MemberMyInfo mem = new MemberMyInfo();
				mem.execute(request);
				viewPage = "member/member_myinfo.jsp";
			}
		} else if (gubun.equals("memberUpdateForm")) {
			String id = (String) request.getSession().getAttribute("sessionId");
			if (id == null) {
				String msg = "로그인 정보가 만료되었습니다.";
				request.setAttribute("t_msg", msg);
				request.setAttribute("t_url", "Member");
				viewPage = "common_alert.jsp";
			} else {
				MemberMyInfo mem = new MemberMyInfo();
				mem.execute(request);
				viewPage = "member/member_update.jsp";
			}
		} else if (gubun.equals("memberUpdate")) {
			MemberUpdate mem = new MemberUpdate();
			mem.execute(request);
			viewPage = "common_alert_view.jsp";
		} else if (gubun.equals("passwordUpdateForm")) {
			viewPage = "member/member_password.jsp";
		} else if (gubun.equals("passwordUpdate")) {
			MemberPasswordUpdate mem = new MemberPasswordUpdate();
			mem.execute(request);
			viewPage = "common_alert_view.jsp";
		} else if (gubun.equals("findPassword")) { // 비밀번호 찾기
			viewPage = "member/member_findPassword.jsp";
		} else if (gubun.equals("sendPassword")) { // 비밀번호 메일 보내기
			CommonExecute mem = new MemberSendPassword();
			mem.execute(request);
			viewPage = "common_alert_view.jsp";
		}

		RequestDispatcher rd = request.getRequestDispatcher(viewPage);
		rd.forward(request, response);

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
