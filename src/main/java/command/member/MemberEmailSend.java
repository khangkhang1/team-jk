package command.member;

import java.io.IOException;
import java.security.SecureRandom;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import mail.SendMail;

@WebServlet("/MemberEmailSend")
public class MemberEmailSend extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

		request.setCharacterEncoding("utf-8");
		response.setContentType("text/plain;charset=utf-8");

		String email = request.getParameter("t_email");

		if (email != null) {
			email = email.trim();
		}

		String emailPattern = "^[a-zA-Z0-9!@#$%^&*_.-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

		if (email == null || !email.matches(emailPattern)) {
			response.getWriter().print("올바른 이메일 형식을 입력해주세요.");
			return;
		}

		HttpSession session = request.getSession();

		// 1분 재발송 제한
		Long lastSent = (Long) session.getAttribute("emailVerifyLastSent");

		long now = System.currentTimeMillis();

		if (lastSent != null && now - lastSent < 60_000) {
			long remainSecond = (60_000 - (now - lastSent)) / 1000 + 1;

			response.getWriter().print("인증번호 재발송은 " + remainSecond + "초 후 가능합니다.");
			return;
		}

		// 6자리 인증번호 생성
		SecureRandom random = new SecureRandom();
		String verifyCode = String.format("%06d", random.nextInt(1_000_000));

		// 환경변수 이름은 본인이 등록한 이름으로 맞추기
		String fromUserEmail = System.getenv("GMAIL_USERNAME");
		String fromUserPassword = System.getenv("GMAIL_APP_PASSWORD");

		if (fromUserEmail == null || fromUserPassword == null) {
			response.getWriter().print("메일 환경설정이 없습니다.");
			return;
		}

		SendMail sendMail = new SendMail(fromUserEmail, fromUserPassword);

		boolean success = sendMail.sendPassword(email, "[인천공항 주차예약] 이메일 인증번호",
				"인증번호는 " + verifyCode + "입니다. 3분 안에 입력해주세요.");

		if (!success) {
			response.getWriter().print("인증번호 발송에 실패했습니다. 잠시 후 다시 시도해주세요.");
			return;
		}

		/*
		 * 재발송에 성공하면 setAttribute가 기존 값을 자동으로 덮어쓴다. 따라서 이전 인증번호는 더 이상 사용할 수 없다.
		 */
		session.setAttribute("emailVerifyCode", verifyCode);
		session.setAttribute("emailVerifyEmail", email);
		session.setAttribute("emailVerifyExpire", now + 180_000); // 3분
		session.setAttribute("emailVerifyLastSent", now);

		// 이메일을 바꿔 재발송한 경우 기존 인증 완료 상태도 제거
		session.removeAttribute("verifiedEmail");

		response.getWriter().print("인증번호를 발송했습니다.");
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		doPost(request, response);
	}
}