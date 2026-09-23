package command.member;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/MemberEmailCheck")
public class MemberEmailCheck extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws IOException {

        request.setCharacterEncoding("utf-8");
        response.setContentType("text/plain;charset=utf-8");

        String inputCode = request.getParameter("t_email_code");
        HttpSession session = request.getSession();

        String savedCode =
                (String) session.getAttribute("emailVerifyCode");
        String savedEmail =
                (String) session.getAttribute("emailVerifyEmail");
        Long expire =
                (Long) session.getAttribute("emailVerifyExpire");

        if (savedCode == null || savedEmail == null || expire == null) {
            response.getWriter().print(
                    "인증번호를 먼저 발송해주세요.");
            return;
        }

        // 3분 경과
        if (System.currentTimeMillis() > expire) {
            session.removeAttribute("emailVerifyCode");
            session.removeAttribute("emailVerifyEmail");
            session.removeAttribute("emailVerifyExpire");

            response.getWriter().print(
                    "인증번호가 만료되었습니다. 다시 발송해주세요.");
            return;
        }

        if (inputCode == null || !savedCode.equals(inputCode.trim())) {
            response.getWriter().print(
                    "인증번호가 일치하지 않습니다.");
            return;
        }

        // 가입 시 이 값으로 최종 서버 검증
        session.setAttribute("verifiedEmail", savedEmail);

        // 인증번호는 한 번 성공하면 제거
        session.removeAttribute("emailVerifyCode");
        session.removeAttribute("emailVerifyEmail");
        session.removeAttribute("emailVerifyExpire");

        response.getWriter().print("success");
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws IOException {
        doPost(request, response);
    }
}