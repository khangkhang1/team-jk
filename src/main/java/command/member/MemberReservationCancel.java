package command.member;

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
				request.setAttribute("t_msg", "예약 취소 중 오류가 발생했습니다. 다시 시도해주세요.");
				request.setAttribute("t_url", "Member?t_gubun=myreservation");
				return;
			}
		}

		if (result == 1) {
			request.setAttribute("t_msg", "예약이 취소되었습니다.");
		} else {
			request.setAttribute("t_msg", "취소할 수 없는 예약입니다. 예약 상태와 입차 예정 시각을 확인해주세요.");
		}
		request.setAttribute("t_url", "Member?t_gubun=myreservation");
	}
}
