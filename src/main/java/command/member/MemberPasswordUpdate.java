package command.member;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import common.CommonExecute;
import dao.MemberDao;

public class MemberPasswordUpdate implements CommonExecute {

	@Override
	public void execute(HttpServletRequest request) {
		MemberDao dao = MemberDao.getdao();
		HttpSession session = request.getSession();

		String id = (String) session.getAttribute("sessionId");
		String currentPassword = request.getParameter("t_current_password");
		String newPassword = request.getParameter("t_new_password");
		String newPasswordConfirm = request.getParameter("t_new_password_confirm");

		if (id == null) {
			alert(request, "로그인 정보가 만료되었습니다.", "Member");
			return;
		}

		if (isEmpty(currentPassword)) {
			alert(request, "현재 비밀번호를 입력하세요.", "Member?t_gubun=passwordUpdateForm");
			return;
		}

		String passwordPattern = "^(?=.*[a-z])[A-Za-z0-9!@#$%^&*_+?.-]{6,16}$";
		if (isEmpty(newPassword) || !newPassword.matches(passwordPattern)) {
			alert(request, "새 비밀번호는 영문 소문자를 최소 1자 포함하여 6~16자로 입력해주세요.",
					"Member?t_gubun=passwordUpdateForm");
			return;
		}

		if (!newPassword.equals(newPasswordConfirm)) {
			alert(request, "새 비밀번호가 일치하지 않습니다.", "Member?t_gubun=passwordUpdateForm");
			return;
		}

		if (currentPassword.equals(newPassword)) {
			alert(request, "현재 비밀번호와 다른 비밀번호를 입력하세요.", "Member?t_gubun=passwordUpdateForm");
			return;
		}

		try {
			String encryptedCurrent = dao.encryptSHA256(currentPassword);
			if (dao.getCheckPassword(id, encryptedCurrent) != 1) {
				alert(request, "현재 비밀번호가 일치하지 않습니다.", "Member?t_gubun=passwordUpdateForm");
				return;
			}

			String encryptedNew = dao.encryptSHA256(newPassword);
			int result = dao.memberPasswordUpdate(id, encryptedNew);

			if (result == 1) {
				request.setAttribute("t_msg", "비밀번호가 변경되었습니다. 다시 로그인해주세요.");
				request.setAttribute("t_url", "Member");
				session.invalidate();
			} else {
				alert(request, "비밀번호 변경에 실패했습니다.", "Member?t_gubun=passwordUpdateForm");
			}
		} catch (Exception e) {
			e.printStackTrace();
			alert(request, "비밀번호 변경 중 오류가 발생했습니다.", "Member?t_gubun=passwordUpdateForm");
		}
	}

	private boolean isEmpty(String value) {
		return value == null || value.trim().equals("");
	}

	private void alert(HttpServletRequest request, String message, String url) {
		request.setAttribute("t_msg", message);
		request.setAttribute("t_url", url);
	}
}
