package command.member;

import java.io.IOException;
import java.security.SecureRandom;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.MemberDao;
import mail.SendMail;

@WebServlet("/MemberRecovery")
public class MemberRecovery extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final SecureRandom RANDOM = new SecureRandom();
	private static final long CODE_LIFETIME = 300_000L;
	private static final long VERIFIED_LIFETIME = 600_000L;
	private static final long RESEND_DELAY = 30_000L;
	private static final String EMAIL_PATTERN = "^[a-zA-Z0-9!@#$%^&*_.-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
	private static final String ID_PATTERN = "^(?=.*[a-z])[a-zA-Z0-9!@#$%^&*_.-]{4,20}$";
	private static final String PASSWORD_PATTERN = "^(?=.*[a-z])[A-Za-z0-9!@#$%^&*_+?.-]{6,16}$";

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/plain;charset=UTF-8");
		response.setHeader("Cache-Control", "no-store");
		String action = request.getParameter("action");
		HttpSession session = request.getSession();
		String result;
		try {
			if ("send".equals(action)) result = sendCode(request, session);
			else if ("verify".equals(action)) result = verifyCode(request, session);
			else if ("findId".equals(action)) result = findId(session);
			else if ("reset".equals(action)) result = resetPassword(request, session);
			else result = "invalid";
		} catch (Exception e) {
			log("회원 계정 찾기 처리 오류", e);
			result = "server_error";
		}
		response.getWriter().print(result);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
	}

	private String sendCode(HttpServletRequest request, HttpSession session) {
		String mode = trim(request.getParameter("mode"));
		String email = trim(request.getParameter("email"));
		String memberId = trim(request.getParameter("memberId"));
		if ((!"id".equals(mode) && !"password".equals(mode))
				|| email == null || email.length() > 100 || !email.matches(EMAIL_PATTERN)
				|| ("password".equals(mode) && (memberId == null || !memberId.matches(ID_PATTERN)))) {
			return "invalid";
		}

		long now = System.currentTimeMillis();
		Long lastSent = (Long) session.getAttribute("recoveryLastSent");
		if (lastSent != null && now - lastSent < RESEND_DELAY) return "wait";
		clearVerification(session);
		session.setAttribute("recoveryLastSent", now);

		MemberDao dao = MemberDao.getdao();
		boolean exists = "id".equals(mode) ? dao.findActiveIdByEmail(email) != null
				: dao.hasActiveAccount(memberId, email);
		// 계정 존재 여부는 인증 전 화면에 알려주지 않는다.
		if (!exists) return "sent";

		String sender = System.getenv("GMAIL_USERNAME");
		String appPassword = System.getenv("GMAIL_APP_PASSWORD");
		if (sender == null || appPassword == null) return "mail_error";

		String code = String.format("%06d", RANDOM.nextInt(1_000_000));
		boolean sent = new SendMail(sender, appPassword).sendPassword(email,
				"[인천공항 주차예약] 계정 찾기 인증번호",
				"인증번호는 " + code + "입니다. 5분 안에 입력해주세요.");
		if (!sent) return "mail_error";

		session.setAttribute("recoveryMode", mode);
		session.setAttribute("recoveryEmail", email);
		session.setAttribute("recoveryMemberId", memberId);
		session.setAttribute("recoveryCode", code);
		session.setAttribute("recoveryCodeExpires", now + CODE_LIFETIME);
		session.setAttribute("recoveryAttempts", 0);
		return "sent";
	}

	private String verifyCode(HttpServletRequest request, HttpSession session) {
		String code = trim(request.getParameter("code"));
		String email = trim(request.getParameter("email"));
		String memberId = trim(request.getParameter("memberId"));
		String savedEmail = (String) session.getAttribute("recoveryEmail");
		String savedId = (String) session.getAttribute("recoveryMemberId");
		if (email == null || savedEmail == null || !savedEmail.equals(email)
				|| ("password".equals(session.getAttribute("recoveryMode"))
						&& (memberId == null || !memberId.equals(savedId)))) return "invalid";
		String saved = (String) session.getAttribute("recoveryCode");
		Long expires = (Long) session.getAttribute("recoveryCodeExpires");
		if (saved == null || expires == null) return "send_first";
		if (System.currentTimeMillis() > expires) {
			clearVerification(session);
			return "expired";
		}
		int attempts = (Integer) session.getAttribute("recoveryAttempts");
		if (attempts >= 5) {
			clearVerification(session);
			return "too_many";
		}
		if (!saved.equals(code)) {
			session.setAttribute("recoveryAttempts", attempts + 1);
			return "wrong_code";
		}
		session.removeAttribute("recoveryCode");
		session.removeAttribute("recoveryCodeExpires");
		session.removeAttribute("recoveryAttempts");
		session.setAttribute("recoveryVerifiedUntil", System.currentTimeMillis() + VERIFIED_LIFETIME);
		return "verified";
	}

	private String findId(HttpSession session) {
		if (!verified(session, "id")) return "verify_first";
		String email = (String) session.getAttribute("recoveryEmail");
		String id = MemberDao.getdao().findActiveIdByEmail(email);
		clearVerification(session);
		return id == null ? "not_found" : "id:" + id;
	}

	private String resetPassword(HttpServletRequest request, HttpSession session) throws Exception {
		if (!verified(session, "password")) return "verify_first";
		String password = request.getParameter("password");
		String confirm = request.getParameter("confirm");
		if (password == null || !password.matches(PASSWORD_PATTERN)) return "invalid_password";
		if (!password.equals(confirm)) return "password_mismatch";

		String memberId = (String) session.getAttribute("recoveryMemberId");
		String email = (String) session.getAttribute("recoveryEmail");
		String encrypted = MemberDao.getdao().encryptSHA256(password);
		int changed = MemberDao.getdao().resetPasswordForEmail(memberId, email, encrypted);
		clearVerification(session);
		return changed == 1 ? "updated" : "not_found";
	}

	private boolean verified(HttpSession session, String mode) {
		Long until = (Long) session.getAttribute("recoveryVerifiedUntil");
		return mode.equals(session.getAttribute("recoveryMode"))
				&& session.getAttribute("recoveryEmail") != null
				&& until != null && System.currentTimeMillis() <= until;
	}

	private void clearVerification(HttpSession session) {
		session.removeAttribute("recoveryMode");
		session.removeAttribute("recoveryEmail");
		session.removeAttribute("recoveryMemberId");
		session.removeAttribute("recoveryCode");
		session.removeAttribute("recoveryCodeExpires");
		session.removeAttribute("recoveryAttempts");
		session.removeAttribute("recoveryVerifiedUntil");
	}

	private String trim(String value) {
		return value == null ? null : value.trim();
	}
}
