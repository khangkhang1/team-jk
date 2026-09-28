package command.member;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import common.CommonExecute;
import dao.MemberDao;

public class MemberReservation implements CommonExecute {

	@Override
	public void execute(HttpServletRequest request) {
		MemberDao dao = MemberDao.getdao();
		String id=(String)request.getSession().getAttribute("sessionId");
		List<Map<String, Object>> reservations = dao.getReservationInfo(id);
		request.setAttribute("reservationList", reservations);

	}

}
