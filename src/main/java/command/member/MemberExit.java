package command.member;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import common.CommonExecute;
import dao.MemberDao;

public class MemberExit implements CommonExecute {

	@Override
	public void execute(HttpServletRequest request) {
		MemberDao dao = MemberDao.getdao();
		String id = (String) request.getSession().getAttribute("sessionId");

		if (id == null) {
			alert(request, "로그인 정보가 만료되었습니다.");
			return;
		}

		int result = dao.memberExit(id);
		String msg = result == 1 ? "회원 탈퇴 성공!" : "회원 탈퇴 실패!";
		request.getSession().invalidate();
		request.setAttribute("t_msg", msg);
		request.setAttribute("t_url", "ParkingStatus");

	}

	private void alert(HttpServletRequest request, String message) {
		request.setAttribute("t_msg", message);
		request.setAttribute("t_url", "Member?t_gubun=myinfo");
	}

}
