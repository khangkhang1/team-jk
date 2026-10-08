package controller;

import common.I18n;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.member.MemberExit;
import command.member.MemberLogin;
import command.member.MemberLogout;
import command.member.MemberMyInfo;
import command.member.MemberPasswordUpdate;
import command.member.MemberReservation;
import command.member.MemberReservationCancel;
import command.member.MemberSave;
import command.member.MemberUpdate;

/**
 * Servlet implementation class Member
 */
@WebServlet("/Member")
public class Member extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String REMEMBER_ID_COOKIE = "savedMemberId";
	private static final int REMEMBER_ID_LIFETIME = 60 * 60 * 24 * 30;

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
		if (!"POST".equalsIgnoreCase(request.getMethod())
				&& (gubun.equals("memberSave") || gubun.equals("memberLogin")
						|| gubun.equals("memberUpdate") || gubun.equals("passwordUpdate")
						|| gubun.equals("memberExit") || gubun.equals("reservationCancel"))) {
			response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			return;
		}
		request.setAttribute("apple_gubun", gubun);

		if (gubun.equals("login")) {
			request.setAttribute("rememberedId", readRememberedId(request));
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
			updateRememberedIdCookie(request, response);
			viewPage = "common_alert.jsp";
		} else if (gubun.equals("logout")) {
			MemberLogout mem = new MemberLogout();
			mem.execute(request);
			viewPage = "common_alert.jsp";
		} else if (gubun.equals("myinfo")) {
			String id = (String) request.getSession().getAttribute("sessionId");
			if (id == null) {
				String msg = I18n.msg(request, "msg.sessionExpired");
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
				String msg = I18n.msg(request, "msg.sessionExpired");
				request.setAttribute("t_msg", msg);
				request.setAttribute("t_url", "Member");
				viewPage = "common_alert.jsp";
			} else {
				MemberMyInfo mem = new MemberMyInfo();
				mem.execute(request);
				viewPage = "member/member_update.jsp";
			}
		} else if (gubun.equals("memberUpdate")) {
			String id = (String) request.getSession().getAttribute("sessionId");
			if (id == null) {
				request.setAttribute("t_msg", I18n.msg(request, "msg.sessionExpired"));
				request.setAttribute("t_url", "Member");
				viewPage = "common_alert.jsp";
			} else {
				MemberUpdate mem = new MemberUpdate();
				mem.execute(request);
				viewPage = "common_alert.jsp";
			}
		} else if (gubun.equals("passwordUpdateForm")) {
			String id = (String) request.getSession().getAttribute("sessionId");
			if (id == null) {
				request.setAttribute("t_msg", I18n.msg(request, "msg.sessionExpired"));
				request.setAttribute("t_url", "Member");
				viewPage = "common_alert.jsp";
			} else {
				viewPage = "member/member_password.jsp";
			}
		} else if (gubun.equals("passwordUpdate")) {
			MemberPasswordUpdate mem = new MemberPasswordUpdate();
			mem.execute(request);
			viewPage = "common_alert.jsp";
		} else if (gubun.equals("findId")) {
			viewPage = "member/member_findId.jsp";
		} else if (gubun.equals("findPassword")) {
			viewPage = "member/member_findPassword.jsp";
		} else if (gubun.equals("sendPassword")) {
			viewPage = "member/member_findPassword.jsp";
		} else if (gubun.equals("memberExit")) {
			MemberExit mem = new MemberExit();
			mem.execute(request);
			viewPage = "common_alert.jsp";
		} else if (gubun.equals("myreservation")) {
			String id = (String) request.getSession().getAttribute("sessionId");
			if (id == null) {
				request.setAttribute("t_msg", I18n.msg(request, "msg.sessionExpired"));
				request.setAttribute("t_url", "Member");
				viewPage = "common_alert.jsp";
			} else {
				MemberReservation mem = new MemberReservation();
				mem.execute(request);
				viewPage = "member/member_myreservation.jsp";
			}
		} else if (gubun.equals("reservationCancel")) {
			String id = (String) request.getSession().getAttribute("sessionId");
			if (id == null) {
				request.setAttribute("t_msg", I18n.msg(request, "msg.sessionExpired"));
				request.setAttribute("t_url", "Member");
			} else {
				MemberReservationCancel mem = new MemberReservationCancel();
				mem.execute(request);
			}
			viewPage = "common_alert.jsp";
		}

		RequestDispatcher rd = request.getRequestDispatcher(viewPage);
		rd.forward(request, response);

	}

	/** 로그인 입력칸에 표시할 아이디만 읽는다. 쿠키로 로그인 여부를 판단하지 않는다. */
	private String readRememberedId(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies == null) return "";
		for (Cookie cookie : cookies) {
			if (!REMEMBER_ID_COOKIE.equals(cookie.getName())) continue;
			String value = cookie.getValue();
			if (value == null) return "";
			try {
				String id = URLDecoder.decode(value, StandardCharsets.UTF_8);
				return id.length() <= 20 ? id : "";
			} catch (IllegalArgumentException e) {
				return "";
			}
		}
		return "";
	}

	/** 체크한 경우 이번 로그인 성공 시에만 저장하고, 체크 해제 후 제출하면 삭제한다. */
	private void updateRememberedIdCookie(HttpServletRequest request, HttpServletResponse response) {
		boolean remember = "Y".equals(request.getParameter("t_rememberId"));
		if (remember && !Boolean.TRUE.equals(request.getAttribute("loginSuccess"))) return;

		String value = remember
				? URLEncoder.encode(request.getParameter("t_id"), StandardCharsets.UTF_8) : "";
		Cookie cookie = new Cookie(REMEMBER_ID_COOKIE, value);
		String contextPath = request.getContextPath();
		cookie.setPath(contextPath.isEmpty() ? "/" : contextPath);
		cookie.setMaxAge(remember ? REMEMBER_ID_LIFETIME : 0);
		cookie.setHttpOnly(true);
		cookie.setSecure(request.isSecure());
		response.addCookie(cookie);
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
