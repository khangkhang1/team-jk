package command.member;

import common.I18n;

import javax.servlet.http.HttpServletRequest;

import common.CommonExecute;
import dao.MemberDao;

public class MemberReservationCancel implements CommonExecute {

	@Override
	public void execute(HttpServletRequest request) {
		String memberId = (String) request.getSession().getAttribute("sessionId");
		String reservationId = request.getParameter("t_reservation_id");
		int result = 0;

		if (memberId != null && reservationId != null && !reservationId.trim().isEmpty()) {
			try {
				result = MemberDao.getdao().cancelReservation(memberId, reservationId);
			} catch (IllegalStateException e) {
				e.printStackTrace();
				request.setAttribute("t_msg", I18n.msg(request, "msg.cancelError"));
				request.setAttribute("t_url", "Member?t_gubun=myreservation");
				return;
			}
		}

		if (result == 1) {
			request.setAttribute("t_msg", I18n.msg(request, "msg.cancelOk"));
		} else {
			request.setAttribute("t_msg", I18n.msg(request, "msg.cancelNotAllowed"));
		}
		request.setAttribute("t_url", "Member?t_gubun=myreservation");
	}
}
